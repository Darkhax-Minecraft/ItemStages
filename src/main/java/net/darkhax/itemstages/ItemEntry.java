package net.darkhax.itemstages;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;

import net.darkhax.bookshelf.util.StackUtils;
import net.minecraft.item.ItemStack;

public class ItemEntry {

    public final Map<String, ItemStack[]> ENTRIES = new HashMap<>();

    public ItemEntry (String stage, ItemStack[] stacks) {

        this.ENTRIES.put(stage, stacks);
    }

    public String getStage (ItemStack stack) {

        for (final Entry<String, ItemStack[]> entry : this.ENTRIES.entrySet()) {

            for (final ItemStack entryStack : entry.getValue()) {

                if (StackUtils.areStacksSimilar(entryStack, stack)) {

                    return entry.getKey();
                }
            }
        }

        return "";
    }

    public boolean hasStack (ItemStack stack) {

        for (final Entry<String, ItemStack[]> entry : this.ENTRIES.entrySet()) {

            for (final ItemStack entryStack : entry.getValue()) {

                if (StackUtils.areStacksSimilar(entryStack, stack)) {

                    return true;
                }
            }
        }

        return false;
    }

    public void add (String stage, ItemStack[] entries) {

        if (this.ENTRIES.containsKey(stage)) {
            
            this.ENTRIES.put(stage, ArrayUtils.addAll(this.ENTRIES.get(stage), entries));
        }
        
        else {
            
            this.ENTRIES.put(stage, entries);
        }
    }
}
