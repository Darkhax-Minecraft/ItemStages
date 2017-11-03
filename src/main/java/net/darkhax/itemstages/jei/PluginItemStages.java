package net.darkhax.itemstages.jei;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IIngredientRegistry;
import net.darkhax.itemstages.ItemEntry;
import net.darkhax.itemstages.ItemStages;
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

        final long time = System.currentTimeMillis();
        ItemStages.LOG.info("Starting a JEI Sync");
        final Set<ItemStack> toBlacklist = new HashSet<>();
        final Set<ItemStack> toWhitelist = new HashSet<>();

        for (final Entry<Item, ItemEntry> entry : ItemStages.ITEM_STAGES.entrySet()) {

            for (final Entry<String, ItemStack[]> stageEntry : entry.getValue().ENTRIES.entrySet()) {

                for (final ItemStack stack : stageEntry.getValue()) {

                    if (ItemStages.isRestricted(player, stack)) {

                        toBlacklist.add(stack);
                    }

                    else {

                        toWhitelist.add(stack);
                    }
                }
            }
        }

        if (!toBlacklist.isEmpty()) {

            ingredientRegistry.removeIngredientsAtRuntime(ItemStack.class, new ArrayList<>(toBlacklist));
        }

        if (!toWhitelist.isEmpty()) {

            ingredientRegistry.addIngredientsAtRuntime(ItemStack.class, new ArrayList<>(toWhitelist));
        }

        ItemStages.LOG.info("Finished JEI Sync, took " + (System.currentTimeMillis() - time) + "ms. " + toBlacklist.size() + " hidden, " + toWhitelist.size() + " shown.");
    }
}