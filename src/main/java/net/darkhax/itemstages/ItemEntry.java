package net.darkhax.itemstages;

import net.darkhax.bookshelf.util.StackUtils;
import net.minecraft.item.ItemStack;

public class ItemEntry {

    private final String stage;
    private final ItemStack[] stacks;

    public ItemEntry (String stage, ItemStack[] stacks) {

        this.stage = stage;
        this.stacks = stacks;
    }

    public String getStage () {

        return this.stage;
    }

    public boolean matches (ItemStack stack) {

        for (final ItemStack restricted : this.stacks) {

            if (StackUtils.areStacksSimilar(restricted, stack)) {

                return true;
            }
        }

        return false;
    }

    public ItemStack[] getStacks () {

        return this.stacks;
    }
}
