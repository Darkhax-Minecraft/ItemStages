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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class Restriction {
    
    private final Collection<Predicate<ItemStack>> restricted = new ArrayList<>();
    private final Set<String> stages = new HashSet<>();
    
    private Function<ItemStack, ITextComponent> hiddenName = stack -> new TranslationTextComponent("tooltip.itemstages.hidden_name", stack.getDisplayName()).mergeStyle(TextFormatting.RED);
    
    private boolean preventInventory = true;
    private Function<ItemStack, ITextComponent> dropMessage = stack -> new TranslationTextComponent("message.itemstages.drop", stack.getDisplayName()).mergeStyle(TextFormatting.RED);
    
    private boolean preventAttacking = true;
    private Function<ItemStack, ITextComponent> attackMessage = stack -> new TranslationTextComponent("message.itemstages.attack", stack.getDisplayName()).mergeStyle(TextFormatting.RED);
    
    private boolean preventPickup = true;
    private int pickupDelay = 60;
    private Function<ItemStack, ITextComponent> pickupMessage = stack -> new TranslationTextComponent("message.itemstages.pickup", stack.getDisplayName()).mergeStyle(TextFormatting.RED);
    
    private boolean preventUsing = true;
    private Function<ItemStack, ITextComponent> usageMessage = stack -> new TranslationTextComponent("message.itemstages.use", stack.getDisplayName()).mergeStyle(TextFormatting.RED);
    
    private boolean hideInJEI = true;
    
    public Restriction(String... stages) {
        
        Arrays.stream(stages).forEach(this.stages::add);
    }
    
    public boolean meetsRequirements (PlayerEntity player, IStageData data) {
        
        return GameStageHelper.hasAllOf(player, data, this.stages);
    }
    
    public ITextComponent getHiddenName (ItemStack stack) {
        
        return this.hiddenName != null ? this.hiddenName.apply(stack) : null;
    }
    
    public Restriction setHiddenName (Function<ItemStack, ITextComponent> hiddenName) {
        
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
    
    public ITextComponent getDropMessage (ItemStack stack) {
        
        return this.dropMessage != null ? this.dropMessage.apply(stack) : null;
    }
    
    public Restriction setDropMessage (Function<ItemStack, ITextComponent> dropMessage) {
        
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
    
    public ITextComponent getAttackMessage (ItemStack stack) {
        
        return this.attackMessage != null ? this.attackMessage.apply(stack) : null;
    }
    
    public Restriction setAttackMessage (Function<ItemStack, ITextComponent> attackMessage) {
        
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
    
    public ITextComponent getPickupMessage (ItemStack stack) {
        
        return this.pickupMessage != null ? this.pickupMessage.apply(stack) : null;
    }
    
    public Restriction setPickupMessage (Function<ItemStack, ITextComponent> pickupMessage) {
        
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
    
    public ITextComponent getUsageMessage (ItemStack stack) {
        
        return this.usageMessage != null ? this.usageMessage.apply(stack) : null;
    }
    
    public Restriction setUsageMessage (Function<ItemStack, ITextComponent> usageMessage) {
        
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
        
        return this.restricted.stream().anyMatch(r -> r.test(stack));
    }
}