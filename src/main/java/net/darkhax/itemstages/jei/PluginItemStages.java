package net.darkhax.itemstages.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IIngredientRegistry;
import net.darkhax.gamestages.capabilities.PlayerDataHandler;
import net.darkhax.itemstages.ConfigurationHandler;
import net.darkhax.itemstages.ItemStages;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@JEIPlugin
public class PluginItemStages implements IModPlugin {

    public static IIngredientBlacklist blacklist;
    public static IIngredientRegistry ingredientRegistry;

    @Override
    public void register (IModRegistry registry) {

        blacklist = registry.getJeiHelpers().getIngredientBlacklist();
        ingredientRegistry = registry.getIngredientRegistry();
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

            final List<ItemStack> toBlacklist = new ArrayList<>();
            final List<ItemStack> toWhitelist = new ArrayList<>();

            // Gets the client player's stage data
            final PlayerDataHandler.IStageData stageData = PlayerDataHandler.getStageData(player);

            // Loops through all the known stages
            for (final String key : ItemStages.SORTED_STAGES.keySet()) {

                // Gets all items staged to the current stage.
                final List<ItemStack> entries = ItemStages.SORTED_STAGES.get(key);

                // If player has the stage, it is whitelisted.
                if (stageData.hasUnlockedStage(key)) {

                    toWhitelist.addAll(entries);
                }

                // If player doesn't have the stage, it is blacklisted.
                else {

                    toBlacklist.addAll(entries);
                }
            }

            if (!toBlacklist.isEmpty()) {

                ingredientRegistry.removeIngredientsAtRuntime(ItemStack.class, toBlacklist);
            }

            if (!toWhitelist.isEmpty()) {

                ingredientRegistry.addIngredientsAtRuntime(ItemStack.class, toWhitelist);
            }

            ItemStages.LOG.info("Finished JEI Sync, took " + (System.currentTimeMillis() - time) + "ms. " + toBlacklist.size() + " hidden, " + toWhitelist.size() + " shown.");
        }
    }
}