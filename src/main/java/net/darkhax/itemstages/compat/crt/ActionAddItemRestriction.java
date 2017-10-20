package net.darkhax.itemstages.compat.crt;

import java.util.List;
import java.util.StringJoiner;

import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.darkhax.itemstages.ItemEntry;
import net.darkhax.itemstages.ItemStages;
import net.minecraft.item.ItemStack;

public class ActionAddItemRestriction implements IAction {

    private final String stage;
    private final ItemStack[] restrictions;

    public ActionAddItemRestriction (String stage, List<IItemStack> restrictions) {

        this.stage = stage;
        this.restrictions = CraftTweakerMC.getItemStacks(restrictions);
    }

    @Override
    public void apply () {

        if (this.stage.isEmpty()) {

            throw new IllegalArgumentException("Empty stage name for this entry!");
        }

        if (this.restrictions.length == 0) {

            throw new IllegalArgumentException("No items or blocks found for this entry!");
        }

        for (final ItemStack stack : this.restrictions) {

            if (stack.isEmpty()) {

                throw new IllegalArgumentException("Entry contains an empty/air stack!");
            }

            ItemStages.ITEM_STAGES.put(stack.getItem(), new ItemEntry(this.stage, stack));
        }
    }

    @Override
    public String describe () {

        final StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");

        for (final ItemStack stack : this.restrictions) {

            joiner.add(stack.getDisplayName());
        }

        return "Added " + this.restrictions.length + " entries to stage " + this.stage + " - " + joiner.toString();
    }
}
