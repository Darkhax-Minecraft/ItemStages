package net.darkhax.itemstages.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IIngredientRegistry;
import net.darkhax.gamestages.capabilities.PlayerDataHandler;
import net.darkhax.itemstages.ItemEntry;
import net.darkhax.itemstages.ItemStages;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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

        if (player.getEntityWorld().isRemote) {

            if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {

                Minecraft.getMinecraft().addScheduledTask( () -> syncHiddenItems(player));
                return;
            }

            final long time = System.currentTimeMillis();
            ItemStages.LOG.info("Starting a JEI Sync");
            final Set<ItemStack> toBlacklist = new HashSet<>();
            final Set<ItemStack> toWhitelist = new HashSet<>();

            final PlayerDataHandler.IStageData stageData = PlayerDataHandler.getStageData(player);

            for (final Entry<Item, ItemEntry> entry : ItemStages.ITEM_STAGES.entrySet()) {

                for (final Entry<String, ItemStack[]> stageEntry : entry.getValue().entries.entrySet()) {

                    if (stageData.hasUnlockedStage(stageEntry.getKey())) {

                        Collections.addAll(toWhitelist, stageEntry.getValue());
                    }

                    else {

                        Collections.addAll(toBlacklist, stageEntry.getValue());
                    }
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