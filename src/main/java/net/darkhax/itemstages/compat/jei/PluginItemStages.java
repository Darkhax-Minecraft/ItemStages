package net.darkhax.itemstages.compat.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mezz.jei.Internal;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.GameStages;
import net.darkhax.itemstages.ConfigurationHandler;
import net.darkhax.itemstages.ItemStages;
import net.darkhax.itemstages.commands.CommandDumpJeiStages;
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
    public static IRecipeRegistry recipeRegistry;
    
    @Override
    public void register (IModRegistry registry) {
        
        blacklist = registry.getJeiHelpers().getIngredientBlacklist();
        ingredientRegistry = registry.getIngredientRegistry();
        ingredientHelper = ingredientRegistry.getIngredientHelper(VanillaTypes.ITEM);
        
        GameStages.COMMAND.addSubcommand(new CommandDumpJeiStages());
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
                if (GameStageHelper.hasStage(player, key)) {
                    
                    itemWhitelist.addAll(ingredientHelper.expandSubtypes(entries));
                }
                
                // If player doesn't have the stage, it is blacklisted.
                else {
                    
                    itemBlacklist.addAll(ingredientHelper.expandSubtypes(entries));
                }
            }
            
            for (final String key : ItemStages.FLUID_STAGES.keySet()) {
                
                if (GameStageHelper.hasStage(player, key)) {
                    
                    fluidWhitelist.addAll(ItemStages.FLUID_STAGES.get(key));
                }
                
                else {
                    
                    fluidBlacklist.addAll(ItemStages.FLUID_STAGES.get(key));
                }
            }
            
            if (!itemBlacklist.isEmpty()) {
                
                ingredientRegistry.removeIngredientsAtRuntime(VanillaTypes.ITEM, itemBlacklist);
            }
            
            if (!itemWhitelist.isEmpty()) {
                
                ingredientRegistry.addIngredientsAtRuntime(VanillaTypes.ITEM, itemWhitelist);
            }
            
            if (!fluidBlacklist.isEmpty()) {
                
                ingredientRegistry.removeIngredientsAtRuntime(VanillaTypes.FLUID, fluidBlacklist);
            }
            
            if (!fluidWhitelist.isEmpty()) {
                
                ingredientRegistry.addIngredientsAtRuntime(VanillaTypes.FLUID, fluidWhitelist);
            }
            
            final IRecipeRegistry recipeManager = Internal.getRuntime().getRecipeRegistry();
            
            for (final String categoryStage : ItemStages.recipeCategoryStages.keySet()) {
                
                final boolean hasStage = GameStageHelper.hasStage(player, categoryStage);
                
                for (final String category : ItemStages.recipeCategoryStages.get(categoryStage)) {
                    
                    if (hasStage) {
                        
                        recipeManager.unhideRecipeCategory(category);
                    }
                    
                    else {
                        
                        recipeManager.hideRecipeCategory(category);
                    }
                }
            }
            
            ItemStages.LOG.info("Finished JEI Sync, took " + (System.currentTimeMillis() - time) + "ms. " + itemBlacklist.size() + " hidden, " + itemWhitelist.size() + " shown.");
        }
    }
}