package net.darkhax.itemstages.compat.jei;

import mezz.jei.api.recipe.IIngredientType;
import net.minecraft.enchantment.EnchantmentData;

public class EnchantmentType {
    public static final IIngredientType<EnchantmentData> ENCHANT = () -> {
        return EnchantmentData.class;
    };
}
