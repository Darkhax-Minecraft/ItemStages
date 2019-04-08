package net.darkhax.itemstages;

import net.darkhax.bookshelf.lib.ItemStackMap.IStackComparator;
import net.darkhax.bookshelf.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class StageCompare implements IStackComparator {
    
    public static final IStackComparator INSTANCE = new StageCompare();
    
    @Override
    public boolean isValid (ItemStack entryStack, Object second) {
        
        if (second instanceof ItemStack) {
            
            final ItemStack stack = (ItemStack) second;
            
            final NBTTagCompound first = StackUtils.getTagCleanly(entryStack);
            final NBTTagCompound two = StackUtils.getTagCleanly(stack);
            
            return ((!this.isTagEmpty(stack) && arePartiallySimilar(first, two)) || (this.isTagEmpty(stack) && this.isTagEmpty(entryStack))) && StackUtils.areStacksSimilar(stack, entryStack);
        }
        
        return false;
    }
    
    private boolean isTagEmpty (ItemStack stack) {
        
        return !stack.hasTagCompound() || stack.getTagCompound().getKeySet().isEmpty();
    }
    
    /**
     * Checks if two NBT types are partially similar. For two tags to be similar, tag one must
     * have every value that tag two has. Tag two is not required to have any of tag one's
     * values.
     * 
     * @param one The first tag to check.
     * @param two The second tag to check, this is the tag containing required NBT data.
     * @return Whether or not the tags are partially similar.
     */
    public static boolean arePartiallySimilar (NBTBase one, NBTBase two) {
        
        // First tag can not be null.
        if (one == null) {
            
            return false;
        }
        
        // If the comparison is null or empty, default to true.
        if (two == null || two.isEmpty()) {
            
            return true;
        }
        
        // If tag is a compound, check each key on the second tag.
        else if (one instanceof NBTTagCompound && two instanceof NBTTagCompound) {
            
            final NBTTagCompound tagOne = (NBTTagCompound) one;
            final NBTTagCompound tagTwo = (NBTTagCompound) two;
            
            for (final String key : tagTwo.getKeySet()) {
                
                // Recursively check all the tags on two for partial similarity.
                if (!arePartiallySimilar(tagOne.getTag(key), tagTwo.getTag(key))) {
                    
                    // Fail if any tag on two is not partially similar to the counterpart on
                    // one.
                    return false;
                }
            }
            
            // If all tags on two are partially similar with one, return true.
            return true;
        }
        
        // If tag is a list, check if one has all entries of two.
        else if (one instanceof NBTTagList && two instanceof NBTTagList) {
            
            final NBTTagList listOne = (NBTTagList) one;
            final NBTTagList listTwo = (NBTTagList) two;
            
            // Iterate the entries of list two
            for (int i = 0; i < listTwo.tagCount(); i++) {
                
                boolean similar = false;
                
                final NBTBase tagTwo = listTwo.get(i);
                
                // Iterate the entries of list one to check if any match.
                for (int j = 0; j < listOne.tagCount(); j++) {
                    
                    // If a similar tag is found, set to true and break.
                    if (arePartiallySimilar(listOne.get(j), tagTwo)) {
                        
                        similar = true;
                        break;
                    }
                }
                
                // Fail if no similar matches were found.
                if (!similar) {
                    
                    return false;
                }
            }
            
            return true;
        }
        
        // If not a special case, check if values are equal.
        return two.equals(one);
    }
}
