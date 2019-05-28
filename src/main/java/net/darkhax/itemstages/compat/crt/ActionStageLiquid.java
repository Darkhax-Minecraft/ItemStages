package net.darkhax.itemstages.compat.crt;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.darkhax.itemstages.ItemStages;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;

public class ActionStageLiquid implements IAction {
    
    private final ILiquidStack stack;
    private final String stage;
    private final boolean stageBucket;
    
    public ActionStageLiquid(String stage, ILiquidStack stack, boolean stageBucket) {
        
        this.stage = stage;
        this.stack = stack;
        this.stageBucket = stageBucket;
    }
    
    @Override
    public void apply () {
        
        if (this.stack == null) {
            
            throw new IllegalArgumentException("Could not stage null liquid");
        }
        
        final FluidStack fluid = CraftTweakerMC.getLiquidStack(this.stack);
        ItemStages.FLUID_STAGES.put(this.stage, fluid);
        
        if (stageBucket) {
            
            ItemStack bucket = FluidUtil.getFilledBucket(CraftTweakerMC.getLiquidStack(stack));
            
            if (!bucket.isEmpty()) {
                
                ItemStages.ITEM_STAGES.put(bucket, this.stage);
            }
        }
    }
    
    @Override
    public String describe () {
        
        return "Staging fluid " + this.stack.getName() + " to " + this.stage;
    }
}