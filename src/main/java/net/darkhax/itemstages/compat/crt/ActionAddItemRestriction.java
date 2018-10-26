package net.darkhax.itemstages.compat.crt;

import crafttweaker.api.item.IIngredient;
import net.darkhax.itemstages.ItemStages;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ActionAddItemRestriction extends ActionItemStage {
    
    private final String stage;
    
    public ActionAddItemRestriction(String stage, Item item) {
        
        super(item);
        this.stage = stage;
    }
    
    public ActionAddItemRestriction(String stage, IIngredient restricted) {
        
        super(restricted);
        this.stage = stage;
    }
    
    @Override
    public void apply () {
        
        if (this.stage.isEmpty()) {
            
            throw new IllegalArgumentException("Empty stage name for this entry!");
        }
        
        this.validate();
        
        for (final ItemStack stack : this.getRestrictedItems()) {
            
            ItemStages.ITEM_STAGES.put(stack, this.stage);
        }
    }
    
    @Override
    public String describe () {
        
        return "Adding to item stage " + this.stage + ". " + this.describeRestrictedStacks();
    }
}
