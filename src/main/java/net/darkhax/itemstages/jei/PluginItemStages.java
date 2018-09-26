package net.darkhax.itemstages.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.itemstages.ConfigurationHandler;
import net.darkhax.itemstages.ItemStages;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@JEIPlugin
public class PluginItemStages implements IModPlugin {

    public static IIngredientBlacklist blacklist;
    public static IIngredientRegistry ingredientRegistry;
    public static IIngredientHelper<ItemStack> ingredientHelper;

    @Override
    public void register (IModRegistry registry) {

        blacklist = registry.getJeiHelpers().getIngredientBlacklist();
        ingredientRegistry = registry.getIngredientRegistry();
        ingredientHelper = ingredientRegistry.getIngredientHelper(ItemStack.class);
    }

    @SideOnly(Side.CLIENT)
    public static void syncHiddenItems (EntityPlayer player) {

        if (player != null && player.getEntityWorld().isRemote && ConfigurationHandler.hideRestrictionsInJEI) {

            // JEI only allows blacklisting from the main client thread.
            if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {

                // Reschedules the sync to the correct thread.
                Minecraft.getMinecraft().addScheduledTask( () -> syncHiddenItems(player));
                return;
            }

            ItemStages.LOG.info("Syncing {} items with JEI!.", ItemStages.ITEM_STAGES.size());
            final long time = System.currentTimeMillis();

            final Collection<ItemStack> itemBlacklist = new ArrayList<>();
            final Collection<ItemStack> itemWhitelist = new ArrayList<>();
            final Collection<FluidStack> fluidBlacklist = new ArrayList<>();
            final Collection<FluidStack> fluidWhitelist = new ArrayList<>();

            // Loops through all the known stages
            for (final String key : ItemStages.SORTED_STAGES.keySet()) {

                // Gets all items staged to the current stage.
                final List<ItemStack> entries = ItemStages.SORTED_STAGES.get(key);

                // If player has the stage, it is whitelisted.
                if (GameStageHelper.clientHasStage(player, key)) {

                    itemWhitelist.addAll(ingredientHelper.expandSubtypes(entries));
                }

                // If player doesn't have the stage, it is blacklisted.
                else {

                    itemBlacklist.addAll(ingredientHelper.expandSubtypes(entries));
                }
            }

            for (final String key : ItemStages.FLUID_STAGES.keySet()) {

                if (GameStageHelper.clientHasStage(player, key)) {

                    fluidWhitelist.addAll(ItemStages.FLUID_STAGES.get(key));
                }

                else {

                    fluidBlacklist.addAll(ItemStages.FLUID_STAGES.get(key));
                }
            }

            if (!itemBlacklist.isEmpty()) {

                ingredientRegistry.removeIngredientsAtRuntime(ItemStack.class, itemBlacklist);
            }

            if (!itemWhitelist.isEmpty()) {

                ingredientRegistry.addIngredientsAtRuntime(ItemStack.class, itemWhitelist);
            }

            if (!fluidBlacklist.isEmpty()) {

                ingredientRegistry.removeIngredientsAtRuntime(FluidStack.class, fluidBlacklist);
            }

            if (!fluidWhitelist.isEmpty()) {

                ingredientRegistry.addIngredientsAtRuntime(FluidStack.class, fluidWhitelist);
            }

            ItemStages.LOG.info("Finished JEI Sync, took " + (System.currentTimeMillis() - time) + "ms. " + itemBlacklist.size() + " hidden, " + itemWhitelist.size() + " shown.");
        }
    }
}