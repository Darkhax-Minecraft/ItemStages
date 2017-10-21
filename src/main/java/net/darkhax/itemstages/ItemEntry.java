package net.darkhax.itemstages;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemEntry {

    private final String stage;
    private final Item item;
    private final int meta;
    private final ItemStack stack;

    public ItemEntry (String stage, ItemStack stack) {

        this(stage, stack.getItem(), stack.getMetadata());
    }

    public ItemEntry (String stage, Item item, int meta) {

        this.stage = stage;
        this.item = item;
        this.meta = meta;
        this.stack = new ItemStack(item, 1, meta);
    }

    public String getStage () {

        return this.stage;
    }

    public Item getItem () {

        return this.item;
    }

    public int getMeta () {

        return this.meta;
    }

    public boolean matches (ItemStack stack) {

        return stack.getItem() == this.item && (stack.getMetadata() == this.meta || this.meta == OreDictionary.WILDCARD_VALUE) && !stack.isEmpty();
    }

    public ItemStack getStack () {

        return this.stack;
    }
}
