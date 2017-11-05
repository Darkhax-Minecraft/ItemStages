package net.darkhax.itemstages;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;

import net.darkhax.bookshelf.util.StackUtils;
import net.minecraft.item.ItemStack;

public class ItemEntry {

    public final Map<String, ItemStack[]> entries = new HashMap<>();

    public ItemEntry (String stage, ItemStack[] stacks) {

        this.entries.put(stage, stacks);
    }

    public String getStage (ItemStack stack) {

        for (final Entry<String, ItemStack[]> entry : this.entries.entrySet()) {

            for (final ItemStack entryStack : entry.getValue()) {

                if (StackUtils.areStacksSimilar(entryStack, stack)) {

                    return entry.getKey();
                }
            }
        }

        return "";
    }

    public boolean hasStack (ItemStack stack) {

        for (final Entry<String, ItemStack[]> entry : this.entries.entrySet()) {

            for (final ItemStack entryStack : entry.getValue()) {

                if (StackUtils.areStacksSimilar(entryStack, stack)) {

                    return true;
                }
            }
        }

        return false;
    }

    public void add (String stage, ItemStack[] entries) {

        if (this.entries.containsKey(stage)) {

            this.entries.put(stage, ArrayUtils.addAll(this.entries.get(stage), entries));
        }

        else {

            this.entries.put(stage, entries);
        }
    }
}
