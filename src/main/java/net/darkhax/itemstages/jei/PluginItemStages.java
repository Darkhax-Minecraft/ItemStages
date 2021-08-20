package net.darkhax.itemstages.jei;

import java.util.ArrayList;
import java.util.List;

import com.ibm.icu.text.DecimalFormat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.darkhax.gamestages.event.StagesSyncedEvent;
import net.darkhax.itemstages.ItemStages;
import net.darkhax.itemstages.Restriction;
import net.darkhax.itemstages.RestrictionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class PluginItemStages implements IModPlugin {
    
    private static final ResourceLocation PLUGIN_ID = new ResourceLocation("itemstages", "main");
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    
    private IIngredientManager ingredients;
    private final List<ItemStack> hiddenItems = new ArrayList<>();
    
    public PluginItemStages() {
        
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, StagesSyncedEvent.class, e -> this.updateHiddenItems());
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, RecipesUpdatedEvent.class, e -> this.updateHiddenItems());
    }
    
    @Override
    public ResourceLocation getPluginUid () {
        
        return PLUGIN_ID;
    }
    
    @Override
    public void onRuntimeAvailable (IJeiRuntime jeiRuntime) {
        
        this.ingredients = jeiRuntime.getIngredientManager();
    }
    
    private void updateHiddenItems () {
        
        final long syncStart = System.nanoTime();
        ItemStages.LOGGER.debug("Syncing JEI with ItemStages.");
        
        this.restoreStagedItems();
        this.collectStagedIngredients();
        this.hideStagedIngredients();
        
        ItemStages.LOGGER.debug("JEI has been synced with ItemStages. Took {}ms.", FORMAT.format((System.nanoTime() - syncStart) / 1000000));
    }
    
    private void restoreStagedItems () {
        
        // Restore the JEI ingredient list to it's previous state.
        final long restoreStart = System.nanoTime();
        ItemStages.LOGGER.debug("Restoring {} hidden items.", this.hiddenItems.size());
        
        if (!this.hiddenItems.isEmpty()) {
            
            this.ingredients.addIngredientsAtRuntime(VanillaTypes.ITEM, this.hiddenItems);
            this.hiddenItems.clear();
        }
        
        ItemStages.LOGGER.debug("Items list restored. Took {}ms.", FORMAT.format((System.nanoTime() - restoreStart) / 1000000));
    }
    
    private void collectStagedIngredients () {
        
        // Calculate the list of items to hide from JEI.
        ItemStages.LOGGER.debug("Calculating items to hide.");
        final long hideCalcStart = System.nanoTime();
        final RestrictionManager restrictions = RestrictionManager.INSTANCE;
        final PlayerEntity player = PlayerUtils.getClientPlayer();
        final IStageData stageData = GameStageHelper.getPlayerData(player);
        
        for (final ItemStack ingredient : this.ingredients.getAllIngredients(VanillaTypes.ITEM)) {
            
            final Restriction restriction = restrictions.getRestriction(player, stageData, ingredient);
            
            if (restriction != null && restriction.shouldHideInJEI()) {
                
                this.hiddenItems.add(ingredient);
            }
        }
        
        ItemStages.LOGGER.debug("Marked {} entries for hiding. Took {}ms.", this.hiddenItems.size(), FORMAT.format((System.nanoTime() - hideCalcStart) / 1000000));
    }
    
    private void hideStagedIngredients () {
        
        // Hide hidden entries from JEI.
        ItemStages.LOGGER.debug("Hiding {} entries from JEI.", this.hiddenItems.size());
        final long hideStart = System.nanoTime();
        
        if (!this.hiddenItems.isEmpty()) {
            
            this.ingredients.removeIngredientsAtRuntime(VanillaTypes.ITEM, this.hiddenItems);
        }
        
        ItemStages.LOGGER.debug("All entries hidden. Took {}ms.", FORMAT.format((System.nanoTime() - hideStart) / 1000000));
    }
}