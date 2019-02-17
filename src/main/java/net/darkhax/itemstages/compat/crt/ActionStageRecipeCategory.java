package net.darkhax.itemstages.compat.crt;

import crafttweaker.IAction;
import net.darkhax.itemstages.ItemStages;

public class ActionStageRecipeCategory implements IAction {
    
    private final String stage;
    private final String categoryId;
    
    public ActionStageRecipeCategory(String stage, String tooltip) {
        
        this.stage = stage;
        this.categoryId = tooltip;
    }
    
    @Override
    public void apply () {
        
        ItemStages.recipeCategoryStages.put(this.stage, this.categoryId);
    }
    
    @Override
    public String describe () {
        
        return "Restring recipe categorie with id \"" + this.categoryId + "\" to stage " + this.stage;
    }
}