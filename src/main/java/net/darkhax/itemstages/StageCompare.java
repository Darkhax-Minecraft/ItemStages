package net.darkhax.itemstages;

import net.darkhax.bookshelf.lib.ItemStackMap.IStackComparator;
import net.darkhax.bookshelf.util.StackUtils;
import net.minecraft.item.ItemStack;

public class StageCompare implements IStackComparator {
    
    public static final IStackComparator INSTANCE = new StageCompare();
    
    @Override
    public boolean isValid (ItemStack entryStack, Object second) {
        
        if (second instanceof ItemStack) {
            
            final ItemStack stack = (ItemStack) second;
            return !this.isTagEmpty(stack) && StackUtils.areStacksSimilarWithPartialNBT(entryStack, stack) || this.isTagEmpty(stack) && this.isTagEmpty(entryStack) && StackUtils.areStacksSimilar(stack, entryStack);
        }
        
        return false;
    }
    
    private boolean isTagEmpty (ItemStack stack) {
        
        return !stack.hasTagCompound() || stack.getTagCompound().getKeySet().isEmpty();
    }
}
