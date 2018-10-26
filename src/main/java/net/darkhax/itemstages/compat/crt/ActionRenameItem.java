package net.darkhax.itemstages.compat.crt;

import crafttweaker.api.item.IIngredient;
import net.darkhax.itemstages.ItemStages;
import net.minecraft.item.ItemStack;

public class ActionRenameItem extends ActionItemStage {
    
    private final String name;
    
    public ActionRenameItem(String name, IIngredient items) {
        
        super(items);
        this.name = name;
    }
    
    @Override
    public void apply () {
        
        for (final ItemStack stack : this.getRestrictedItems()) {
            
            ItemStages.CUSTOM_NAMES.put(stack, this.name);
        }
    }
    
    @Override
    public String describe () {
        
        return "Renaming staged items " + this.describeRestrictedStacks();
    }
}