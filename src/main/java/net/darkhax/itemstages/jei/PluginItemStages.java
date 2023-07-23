package net.darkhax.itemstages.jei;

import com.ibm.icu.text.DecimalFormat;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.darkhax.gamestages.event.StagesSyncedEvent;
import net.darkhax.itemstages.ItemStages;
import net.darkhax.itemstages.Restriction;
import net.darkhax.itemstages.RestrictionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.util.thread.EffectiveSide;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class PluginItemStages implements IModPlugin {

    private static final ResourceLocation PLUGIN_ID = new ResourceLocation("itemstages", "main");
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    private IJeiRuntime runtime;
    private final List<ItemStack> hiddenItems = new ArrayList<>();

    public PluginItemStages() {

        if (EffectiveSide.get().isClient()) {

            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, StagesSyncedEvent.class, e -> this.updateHiddenItems());
            MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, RecipesUpdatedEvent.class, e -> this.updateHiddenItems());
        }
    }

    @Override
    public ResourceLocation getPluginUid() {

        return PLUGIN_ID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

        this.runtime = jeiRuntime;
    }

    private void updateHiddenItems() {

        if (this.runtime != null) {

            this.runtime.getIngredientManager();

            final long syncStart = System.nanoTime();
            final IIngredientManager ingredients = this.runtime.getIngredientManager();

            ItemStages.LOGGER.debug("Syncing JEI with ItemStages.");

            this.restoreStagedItems(ingredients);
            this.collectStagedIngredients(ingredients);
            this.hideStagedIngredients(ingredients);

            ItemStages.LOGGER.debug("JEI has been synced with ItemStages. Took {}ms.", FORMAT.format((System.nanoTime() - syncStart) / 1000000));
        }
    }

    private void restoreStagedItems(IIngredientManager ingredients) {

        // Restore the JEI ingredient list to it's previous state.
        final long restoreStart = System.nanoTime();
        ItemStages.LOGGER.debug("Restoring {} hidden items.", this.hiddenItems.size());

        if (!this.hiddenItems.isEmpty()) {

            ingredients.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, this.hiddenItems);
            this.hiddenItems.clear();
        }

        ItemStages.LOGGER.debug("Items list restored. Took {}ms.", FORMAT.format((System.nanoTime() - restoreStart) / 1000000));
    }

    private void collectStagedIngredients(IIngredientManager ingredients) {

        // Calculate the list of items to hide from JEI.
        ItemStages.LOGGER.debug("Calculating items to hide.");
        final long hideCalcStart = System.nanoTime();
        final RestrictionManager restrictions = RestrictionManager.INSTANCE;
        final Player player = Minecraft.getInstance().player;//TODO PlayerUtils.getClientPlayer();
        final IStageData stageData = GameStageHelper.getPlayerData(player);

        for (final ItemStack ingredient : ingredients.getAllIngredients(VanillaTypes.ITEM_STACK)) {

            final Restriction restriction = restrictions.getRestriction(player, stageData, ingredient);

            if (restriction != null && restriction.shouldHideInJEI()) {

                this.hiddenItems.add(ingredient);
            }
        }

        ItemStages.LOGGER.debug("Marked {} entries for hiding. Took {}ms.", this.hiddenItems.size(), FORMAT.format((System.nanoTime() - hideCalcStart) / 1000000));
    }

    private void hideStagedIngredients(IIngredientManager ingredients) {

        // Hide hidden entries from JEI.
        ItemStages.LOGGER.debug("Hiding {} entries from JEI.", this.hiddenItems.size());
        final long hideStart = System.nanoTime();

        if (!this.hiddenItems.isEmpty()) {

            ingredients.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, this.hiddenItems);
        }

        ItemStages.LOGGER.debug("All entries hidden. Took {}ms.", FORMAT.format((System.nanoTime() - hideStart) / 1000000));
    }
}