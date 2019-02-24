package net.darkhax.itemstages.compat.crt;

import crafttweaker.IAction;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import net.darkhax.itemstages.ItemStages;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;

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
            ItemStages.ENCHANT_STAGES.put(this.stage, enchantment);
        }
    }

    @Override
    public String describe () {

        return "Staging enchant " + this.enchantment.getName() + " to " + this.stage;
    }
}
