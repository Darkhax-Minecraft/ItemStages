package net.darkhax.itemstages.compat.crt;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.liquid.ILiquidStack;
import net.darkhax.bookshelf.util.ModUtils;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.ItemStages")
public class ItemStagesCrT {

    @ZenMethod
    public static void addItemStage (String stage, IIngredient input) {

        CraftTweakerAPI.apply(new ActionAddItemRestriction(stage, input));
    }

    @ZenMethod
    public static void removeItemStage (IIngredient input) {

        CraftTweakerAPI.apply(new ActionRemoveRestriction(input));
    }

    @ZenMethod
    public static void stageModItems (String stage, String modid) {

        for (final Item item : ModUtils.getSortedEntries(ForgeRegistries.ITEMS).get(modid)) {

            if (item != null && item != Items.AIR) {

                CraftTweakerAPI.apply(new ActionAddItemRestriction(stage, item));
            }
        }
    }

    @ZenMethod
    public static void stageLiquid (String stage, ILiquidStack stack) {

        CraftTweakerAPI.apply(new ActionStageLiquid(stage, stack));
    }
}