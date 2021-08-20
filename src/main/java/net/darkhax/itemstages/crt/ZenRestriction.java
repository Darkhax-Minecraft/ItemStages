package net.darkhax.itemstages.crt;

import java.util.function.Function;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.item.MCItemStack;
import com.blamejared.crafttweaker.impl.util.text.MCTextComponent;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;

import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.itemstages.Restriction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

@ZenRegister
@NativeTypeRegistration(value = Restriction.class, zenCodeName = "mods.itemstages.Restriction")
public class ZenRestriction {
    
    private static Function<ItemStack, ITextComponent> wrapTextFunc (Function<IItemStack, MCTextComponent> crtFunc) {
        
        return stack -> crtFunc.apply(new MCItemStack(stack)).getInternal();
    }
    
    @ZenCodeType.Method
    public static Restriction defaults (Restriction self, boolean defaultValue) {
        
        return self.setPreventInventory(defaultValue).setPreventPickup(defaultValue).setPreventAttacking(defaultValue).setPreventUsing(defaultValue).setHideInJEI(defaultValue);
    }
    
    @ZenCodeType.Method
    public static boolean meetsRequirements (Restriction self, PlayerEntity player) {
        
        return self.meetsRequirements(player, GameStageHelper.getPlayerData(player));
    }
    
    @ZenCodeType.Method
    public static Restriction hiddenName (Restriction self, MCTextComponent text) {
        
        return hiddenName(self, stack -> text);
    }
    
    @ZenCodeType.Method
    public static Restriction hiddenName (Restriction self, Function<IItemStack, MCTextComponent> textFunc) {
        
        return self.setHiddenName(wrapTextFunc(textFunc));
    }
    
    @ZenCodeType.Method
    public static Restriction preventInventory (Restriction self) {
        
        return preventInventory(self, true);
    }
    
    @ZenCodeType.Method
    public static Restriction preventInventory (Restriction self, boolean preventInventory) {
        
        return self.setPreventInventory(preventInventory);
    }
    
    @ZenCodeType.Method
    public static Restriction dropMessage (Restriction self, MCTextComponent text) {
        
        return dropMessage(self, stack -> text);
    }
    
    @ZenCodeType.Method
    public static Restriction dropMessage (Restriction self, Function<IItemStack, MCTextComponent> textFunc) {
        
        return self.setDropMessage(wrapTextFunc(textFunc));
    }
    
    @ZenCodeType.Method
    public static Restriction preventAttacking (Restriction self) {
        
        return preventAttacking(self, true);
    }
    
    @ZenCodeType.Method
    public static Restriction preventAttacking (Restriction self, boolean preventAttacking) {
        
        return self.setPreventAttacking(preventAttacking);
    }
    
    @ZenCodeType.Method
    public static Restriction attackMessage (Restriction self, MCTextComponent text) {
        
        return attackMessage(self, stack -> text);
    }
    
    @ZenCodeType.Method
    public static Restriction attackMessage (Restriction self, Function<IItemStack, MCTextComponent> textFunc) {
        
        return self.setAttackMessage(wrapTextFunc(textFunc));
    }
    
    @ZenCodeType.Method
    public static Restriction preventPickup (Restriction self) {
        
        return preventPickup(self, true);
    }
    
    @ZenCodeType.Method
    public static Restriction preventPickup (Restriction self, boolean preventPickup) {
        
        return self.setPreventPickup(preventPickup);
    }
    
    @ZenCodeType.Method
    public static Restriction setPickupDelay (Restriction self, int ticks) {
        
        return self.setPickupDelay(ticks);
    }
    
    @ZenCodeType.Method
    public static Restriction pickupMessage (Restriction self, MCTextComponent text) {
        
        return pickupMessage(self, stack -> text);
    }
    
    @ZenCodeType.Method
    public static Restriction pickupMessage (Restriction self, Function<IItemStack, MCTextComponent> textFunc) {
        
        return self.setPickupMessage(wrapTextFunc(textFunc));
    }
    
    @ZenCodeType.Method
    public static Restriction preventUsing (Restriction self) {
        
        return preventUsing(self, true);
    }
    
    @ZenCodeType.Method
    public static Restriction preventUsing (Restriction self, boolean preventUsing) {
        
        return self.setPreventUsing(preventUsing);
    }
    
    @ZenCodeType.Method
    public static Restriction usageMessage (Restriction self, MCTextComponent text) {
        
        return usageMessage(self, stack -> text);
    }
    
    @ZenCodeType.Method
    public static Restriction usageMessage (Restriction self, Function<IItemStack, MCTextComponent> textFunc) {
        
        return self.setUsageMessage(wrapTextFunc(textFunc));
    }
    
    @ZenCodeType.Method
    public static Restriction setHiddenInJEI (Restriction self) {
        
        return setHiddenInJEI(self, true);
    }
    
    @ZenCodeType.Method
    public static Restriction setHiddenInJEI (Restriction self, boolean shouldHide) {
        
        return self.setHideInJEI(shouldHide);
    }
    
    @ZenCodeType.Method
    public static Restriction restrict (Restriction self, IIngredient ingredient) {
        
        return self.restrict(ingredient.asVanillaIngredient());
    }
    
    @ZenCodeType.Method
    public static boolean isRestricted (Restriction self, IItemStack stack) {
        
        return self.isRestricted(stack.getInternal());
    }
}