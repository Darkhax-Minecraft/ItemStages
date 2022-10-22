package net.darkhax.itemstages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;

@SuppressWarnings("unused")
public class Restriction {
    
    private final Collection<Predicate<ItemStack>> restricted = new ArrayList<>();
    private final Set<String> stages = new HashSet<>();
    
    private Function<ItemStack, Component> hiddenName = stack -> new TranslatableComponent("tooltip.itemstages.hidden_name", stack.getHoverName()).withStyle(ChatFormatting.RED);
    
    private boolean preventInventory = true;
    private boolean preventEquipment = true;
    private Function<ItemStack, Component> dropMessage = stack -> new TranslatableComponent("message.itemstages.drop", stack.getHoverName()).withStyle(ChatFormatting.RED);
    
    private boolean preventAttacking = true;
    private Function<ItemStack, Component> attackMessage = stack -> new TranslatableComponent("message.itemstages.attack", stack.getHoverName()).withStyle(ChatFormatting.RED);
    
    private boolean preventPickup = true;
    private int pickupDelay = 60;
    private Function<ItemStack, Component> pickupMessage = stack -> new TranslatableComponent("message.itemstages.pickup", stack.getHoverName()).withStyle(ChatFormatting.RED);
    
    private boolean preventUsing = true;
    private Function<ItemStack, Component> usageMessage = stack -> new TranslatableComponent("message.itemstages.use", stack.getHoverName()).withStyle(ChatFormatting.RED);
    
    private boolean hideInJEI = true;
    
    public Restriction(String... stages) {

        this.stages.addAll(Arrays.asList(stages));
    }
    
    public boolean meetsRequirements (Player player, IStageData data) {
        
        return GameStageHelper.hasAllOf(player, data, this.stages);
    }
    
    public Component getHiddenName (ItemStack stack) {
        
        return this.hiddenName != null ? this.hiddenName.apply(stack) : null;
    }
    
    public Restriction setHiddenName (Function<ItemStack, Component> hiddenName) {
        
        this.hiddenName = hiddenName;
        return this;
    }
    
    public boolean shouldPreventInventory () {
        
        return this.preventInventory;
    }
    
    public Restriction setPreventInventory (boolean preventInventory) {
        
        this.preventInventory = preventInventory;
        return this;
    }
    
    public boolean shouldPreventEquipment () {
        
        return this.preventEquipment;
    }
    
    public Restriction setPreventEquipment (boolean preventEquipment) {
        
        this.preventEquipment = preventEquipment;
        return this;
    }
    
    public Component getDropMessage (ItemStack stack) {
        
        return this.dropMessage != null ? this.dropMessage.apply(stack) : null;
    }
    
    public Restriction setDropMessage (Function<ItemStack, Component> dropMessage) {
        
        this.dropMessage = dropMessage;
        return this;
    }
    
    public boolean shouldPreventAttacking () {
        
        return this.preventAttacking;
    }
    
    public Restriction setPreventAttacking (boolean preventAttacking) {
        
        this.preventAttacking = preventAttacking;
        return this;
    }
    
    public Component getAttackMessage (ItemStack stack) {
        
        return this.attackMessage != null ? this.attackMessage.apply(stack) : null;
    }
    
    public Restriction setAttackMessage (Function<ItemStack, Component> attackMessage) {
        
        this.attackMessage = attackMessage;
        return this;
    }
    
    public boolean shouldPreventPickup () {
        
        return this.preventPickup;
    }
    
    public Restriction setPreventPickup (boolean preventPickup) {
        
        this.preventPickup = preventPickup;
        return this;
    }
    
    public Restriction setPickupDelay (int ticks) {
        
        this.pickupDelay = ticks;
        return this;
    }
    
    public int getPickupDelay () {
        
        return this.pickupDelay;
    }
    
    public Component getPickupMessage (ItemStack stack) {
        
        return this.pickupMessage != null ? this.pickupMessage.apply(stack) : null;
    }
    
    public Restriction setPickupMessage (Function<ItemStack, Component> pickupMessage) {
        
        this.pickupMessage = pickupMessage;
        return this;
    }
    
    public boolean shouldPreventUsing () {
        
        return this.preventUsing;
    }
    
    public Restriction setPreventUsing (boolean preventUsing) {
        
        this.preventUsing = preventUsing;
        return this;
    }
    
    public Component getUsageMessage (ItemStack stack) {
        
        return this.usageMessage != null ? this.usageMessage.apply(stack) : null;
    }
    
    public Restriction setUsageMessage (Function<ItemStack, Component> usageMessage) {
        
        this.usageMessage = usageMessage;
        return this;
    }
    
    public boolean shouldHideInJEI () {
        
        return this.hideInJEI;
    }
    
    public Restriction setHideInJEI (boolean shouldHide) {
        
        this.hideInJEI = shouldHide;
        return this;
    }
    
    public Collection<Predicate<ItemStack>> getRestricted () {
        
        return this.restricted;
    }
    
    public Restriction restrict (Predicate<ItemStack> ingredient) {
        
        this.restricted.add(ingredient);
        return this;
    }
    
    public Set<String> getStages () {
        
        return this.stages;
    }
    
    public boolean isRestricted (ItemStack stack) {
        
        for (final Predicate<ItemStack> condition : this.restricted) {
            
            if (condition.test(stack)) {
                
                return true;
            }
        }
        
        return false;
    }
}