package net.darkhax.itemstages.compat.crt;

import crafttweaker.IAction;
import crafttweaker.api.enchantments.IEnchantment;
import net.darkhax.itemstages.ItemStages;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;

public class ActionStageEnchantByLevel implements IAction {

    private final IEnchantment enchantment;
    private final String stage;

    public ActionStageEnchantByLevel(String stage, IEnchantment enchantment) {

        this.stage = stage;
        this.enchantment = enchantment;
    }

    @Override
    public void apply () {

        if (this.enchantment == null) {

            throw new IllegalArgumentException("Could not stage null enchantment");
        }

        final EnchantmentData enchantment = new EnchantmentData((Enchantment) this.enchantment.getDefinition().getInternal(), this.enchantment.getLevel());
        ItemStages.ENCHANT_STAGES.put(this.stage, enchantment);
    }

    @Override
    public String describe () {

        return "Staging enchant by level " + this.enchantment.displayName() + " to " + this.stage;
    }
}