package net.darkhax.itemstages.compat.crt;

import crafttweaker.api.item.IIngredient;
import net.darkhax.itemstages.ItemStages;
import net.minecraft.item.ItemStack;

public class ActionRemoveRestriction extends ActionItemStage {
    
    public ActionRemoveRestriction(IIngredient restricted) {
        
        super(restricted);
    }
    
    @Override
    public void apply () {
        
        this.validate();
        
        for (final ItemStack stack : this.getRestrictedItems()) {
            
            ItemStages.ITEM_STAGES.remove(stack);
        }
    }
    
    @Override
    public String describe () {
        
        return "Removing item stage for " + this.describeRestrictedStacks();
    }
}
