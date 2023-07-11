package net.darkhax.itemstages.crt.actions;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.base.IRuntimeAction;
import net.darkhax.itemstages.Restriction;
import net.darkhax.itemstages.RestrictionManager;

public final class ActionCreateRestriction implements IRuntimeAction {
    
    private final Restriction toRegister;
    
    private ActionCreateRestriction(Restriction toRegister) {
        
        this.toRegister = toRegister;
    }
    
    @Override
    public void apply () {
        
        RestrictionManager.INSTANCE.addRestriction(this.toRegister);
    }
    
    @Override
    public String describe () {
        
        return "[ItemStages] Creating new restriction for stages " + this.toRegister.getStages() + ".";
    }

    @Override
    public String systemName() {
        return "Item Stages Restrictions";
    }

    public static void apply (Restriction toRegister) {
        
        CraftTweakerAPI.apply(new ActionCreateRestriction(toRegister));
    }
}