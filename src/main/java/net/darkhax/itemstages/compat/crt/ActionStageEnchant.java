package net.darkhax.itemstages.compat.crt;

import crafttweaker.IAction;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import net.darkhax.itemstages.ItemStages;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;

public class ActionStageEnchant implements IAction {

    private final IEnchantmentDefinition enchantment;
    private final String stage;

    public ActionStageEnchant(String stage, IEnchantmentDefinition enchantment) {

        this.stage = stage;
        this.enchantment = enchantment;
    }

    @Override
    public void apply () {

        if (this.enchantment == null) {

            throw new IllegalArgumentException("Could not stage null enchantment");
        }

        for (int lvl = enchantment.getMinLevel(); lvl <= enchantment.getMaxLevel(); lvl++) {

            final EnchantmentData enchantment = new EnchantmentData((Enchantment) this.enchantment.getInternal(), lvl);

            final ItemStack enchantedBook = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:enchanted_book")));
            EnchantmentHelper.setEnchantments(new HashMap<Enchantment, Integer>(){{put(enchantment.enchantment, enchantment.enchantmentLevel);}}, enchantedBook);

            ItemStages.ENCHANT_STAGES.put(enchantment, this.stage);
            ItemStages.ITEM_STAGES.put(enchantedBook, this.stage);
        }
    }

    @Override
    public String describe () {

        return "Staging enchant " + this.enchantment.getName() + " to " + this.stage;
    }
}
