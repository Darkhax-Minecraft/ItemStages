package net.darkhax.itemstages.compat.crt;

import crafttweaker.IAction;
import net.darkhax.itemstages.ItemStages;

public class ActionStageTooltip implements IAction {
    
    private final String stage;
    private final String tooltip;
    
    public ActionStageTooltip(String stage, String tooltip) {
        
        this.stage = stage;
        this.tooltip = tooltip;
    }
    
    @Override
    public void apply () {
        
        ItemStages.tooltipStages.put(this.stage, this.tooltip);
    }
    
    @Override
    public String describe () {
        
        return "Restring tooltips that start with \"" + this.tooltip + "\" to stage " + this.stage;
    }
}