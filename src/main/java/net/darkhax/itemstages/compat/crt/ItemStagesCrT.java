package net.darkhax.itemstages.compat.crt;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.ItemStages")
public class ItemStagesCrT {

    @ZenMethod
    public static void addItemStage (String stage, IIngredient input) {

        CraftTweakerAPI.apply(new ActionAddItemRestriction(stage, input));
    }
}