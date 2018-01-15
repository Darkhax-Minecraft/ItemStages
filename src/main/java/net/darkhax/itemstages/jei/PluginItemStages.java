package net.darkhax.itemstages.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IIngredientRegistry;
import net.darkhax.gamestages.capabilities.PlayerDataHandler;
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

        if (player != null && player.getEntityWorld().isRemote) {

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

            for (final Entry<ItemStack, String> entry : ItemStages.ITEM_STAGES.entrySet()) {

                // If player has the stage, it is whitelisted.
                if (stageData.hasUnlockedStage(entry.getValue())) {

                    toWhitelist.add(entry.getKey());
                }

                // If player doesn't have the stage, it is blacklisted.
                else {

                    toBlacklist.add(entry.getKey());
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