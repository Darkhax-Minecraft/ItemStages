package net.darkhax.itemstages;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class TempUtils {

    public static ItemStack[] getAllItems (Item item) {

        final NonNullList<ItemStack> items = NonNullList.create();
        item.getSubItems(item.getCreativeTab(), items);
        return items.toArray(new ItemStack[0]);
    }
}