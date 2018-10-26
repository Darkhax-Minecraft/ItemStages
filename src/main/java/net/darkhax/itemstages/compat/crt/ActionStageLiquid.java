package net.darkhax.itemstages.compat.crt;

import crafttweaker.IAction;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.darkhax.itemstages.ItemStages;
import net.minecraftforge.fluids.FluidStack;

public class ActionStageLiquid implements IAction {
    
    private final ILiquidStack stack;
    private final String stage;
    
    public ActionStageLiquid(String stage, ILiquidStack stack) {
        
        this.stage = stage;
        this.stack = stack;
    }
    
    @Override
    public void apply () {
        
        if (this.stack == null) {
            
            throw new IllegalArgumentException("Could not stage null liquid");
        }
        
        final FluidStack fluid = CraftTweakerMC.getLiquidStack(this.stack);
        ItemStages.FLUID_STAGES.put(this.stage, fluid);
    }
    
    @Override
    public String describe () {
        
        return "Staging fluid " + this.stack.getName() + " to " + this.stage;
    }
}