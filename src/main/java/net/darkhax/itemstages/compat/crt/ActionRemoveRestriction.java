package net.darkhax.itemstages.compat.crt;

import java.util.StringJoiner;

import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.itemstages.ItemStages;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ActionRemoveRestriction implements IAction {

    private final IIngredient restricted;
    private ItemStack[] restrictions;

    public ActionRemoveRestriction (IIngredient restricted) {

        this.restricted = restricted;

        if (this.restricted instanceof IItemStack && ((IItemStack) this.restricted).getDamage() == OreDictionary.WILDCARD_VALUE) {

            this.restrictions = StackUtils.getAllItems(CraftTweakerMC.getItemStack(this.restricted).getItem());
        }

        else {

            this.restrictions = CraftTweakerMC.getItemStacks(this.restricted.getItems());
        }
    }

    @Override
    public void apply () {

        if (this.restrictions.length == 0) {

            throw new IllegalArgumentException("No items or blocks found for this entry!");
        }

        for (final ItemStack stack : this.restrictions) {

            if (stack.isEmpty()) {

                throw new IllegalArgumentException("Entry contains an empty/air stack!");
            }

            ItemStages.ITEM_STAGES.remove(stack);
        }
    }

    @Override
    public String describe () {

        final StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");

        for (final ItemStack stack : this.restrictions) {

            joiner.add(stack.getDisplayName());
        }

        return "Removed " + this.restrictions.length + " entries: " + joiner.toString();
    }
}
