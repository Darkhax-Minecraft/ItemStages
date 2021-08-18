package net.darkhax.itemstages;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.bookshelf.util.TextUtils;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.darkhax.gamestages.data.IStageData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("itemstages")
public class ItemStages {
    
    public static final Logger LOGGER = LogManager.getLogger("Item Stages");
    
    public ItemStages() {
        
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);
        MinecraftForge.EVENT_BUS.addListener(this::onItemPickup);
        MinecraftForge.EVENT_BUS.addListener(this::onItemUsed);
        MinecraftForge.EVENT_BUS.addListener(this::onEntityHurt);
        
        MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);
    }
    
    private void addReloadListeners (AddReloadListenerEvent event) {
        
        event.addListener(RestrictionManager.INSTANCE);
    }
    
    private void onEntityHurt (LivingAttackEvent event) {
        
        if (this.canAffectPlayer(event.getSource())) {
            
            final PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
            final ItemStack stack = player.getHeldItemMainhand();
            final Restriction restriction = RestrictionManager.INSTANCE.getRestriction(player, stack);
            
            if (restriction != null && restriction.shouldPreventAttacking()) {
                
                event.setCanceled(true);
                
                final ITextComponent message = restriction.getAttackMessage(stack);
                player.sendMessage(message, Util.DUMMY_UUID);
            }
        }
    }
    
    private void onItemUsed (PlayerInteractEvent event) {
        
        if (event.isCancelable() && this.canAffectPlayer(event.getPlayer())) {
            
            final ItemStack stack = event.getItemStack();
            final Restriction restriction = RestrictionManager.INSTANCE.getRestriction(event.getPlayer(), stack);
            
            if (restriction != null && restriction.shouldPreventUsing()) {
                
                event.setCanceled(true);
                
                final ITextComponent message = restriction.getUsageMessage(stack);
                event.getPlayer().sendMessage(message, Util.DUMMY_UUID);
            }
        }
    }
    
    private void onItemPickup (EntityItemPickupEvent event) {
        
        if (this.canAffectPlayer(event.getPlayer())) {
            
            final ItemStack stack = event.getItem().getItem();
            final Restriction restriction = RestrictionManager.INSTANCE.getRestriction(event.getPlayer(), stack);
            
            if (restriction != null && restriction.shouldPreventPickup()) {
                
                event.setCanceled(true);
                event.getItem().setPickupDelay(restriction.getPickupDelay());
                // TODO consider extending life span by default delay.
                
                final ITextComponent message = restriction.getPickupMessage(stack);
                event.getPlayer().sendMessage(message, Util.DUMMY_UUID);
            }
        }
    }
    
    private void onPlayerTick (TickEvent.PlayerTickEvent event) {
        
        if (event.phase == Phase.START && event.player != null && !event.player.world.isRemote && !(event.player instanceof FakePlayer)) {
            
            final PlayerEntity player = event.player;
            final IStageData stageData = GameStageHelper.getPlayerData(player);
            final PlayerInventory inv = player.inventory;
            
            for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
                
                final ItemStack slotContent = inv.getStackInSlot(slot);
                final Restriction restriction = RestrictionManager.INSTANCE.getRestriction(player, stageData, slotContent);
                
                if (restriction != null && restriction.shouldPreventInventory()) {
                    
                    inv.setInventorySlotContents(slot, ItemStack.EMPTY);
                    player.dropItem(slotContent, false);
                    
                    final ITextComponent message = restriction.getDropMessage(slotContent);
                    
                    if (message != null) {
                        
                        player.sendMessage(message, Util.DUMMY_UUID);
                    }
                }
            }
        }
    }
    
    private void onItemTooltip (ItemTooltipEvent event) {
        
        if (event.getPlayer() != null) {
            
            final PlayerEntity player = event.getPlayer();
            final IStageData data = GameStageSaveHandler.getClientData();
            final ItemStack stack = event.getItemStack();
            
            final Restriction restriction = RestrictionManager.INSTANCE.getRestriction(player, data, stack);
            
            if (restriction != null) {
                
                event.getToolTip().clear();
                final ITextComponent hiddenName = restriction.getHiddenName(stack);
                
                if (hiddenName != null) {
                    
                    event.getToolTip().add(hiddenName);
                }
                
                // Debug tooltip shows which stages the player doesn't have.
                if (event.getFlags().isAdvanced()) {
                    
                    final List<ITextComponent> stages = new ArrayList<>();
                    
                    final ITextComponent sep = new StringTextComponent(", ").mergeStyle(TextFormatting.GRAY);
                    
                    for (final String stage : restriction.getStages()) {
                        
                        stages.add(new StringTextComponent(stage).mergeStyle(data.hasStage(stage) ? TextFormatting.GREEN : TextFormatting.RED));
                    }
                    
                    final ITextComponent desc = new TranslationTextComponent("tooltip.itemstages.item.description", TextUtils.join(sep, stages)).mergeStyle(TextFormatting.GRAY);
                    event.getToolTip().add(desc);
                    
                    if (restriction.shouldPreventInventory()) {
                        
                        event.getToolTip().add(new TranslationTextComponent("tooltip.itemstages.debug.drop").mergeStyle(TextFormatting.RED));
                    }
                    
                    if (restriction.shouldPreventPickup()) {
                        
                        event.getToolTip().add(new TranslationTextComponent("tooltip.itemstages.debug.pickup").mergeStyle(TextFormatting.RED));
                    }
                    
                    if (restriction.shouldPreventUsing()) {
                        
                        event.getToolTip().add(new TranslationTextComponent("tooltip.itemstages.debug.use").mergeStyle(TextFormatting.RED));
                    }
                    
                    if (restriction.shouldPreventAttacking()) {
                        
                        event.getToolTip().add(new TranslationTextComponent("tooltip.itemstages.debug.attack").mergeStyle(TextFormatting.RED));
                    }
                    
                    if (restriction.shouldHideInJEI()) {
                        
                        event.getToolTip().add(new TranslationTextComponent("tooltip.itemstages.debug.jei").mergeStyle(TextFormatting.RED));
                    }
                }
            }
        }
    }
    
    private final boolean canAffectPlayer (DamageSource source) {
        
        return source != null && source.getTrueSource() instanceof PlayerEntity && this.canAffectPlayer((PlayerEntity) source.getTrueSource());
    }
    
    private final boolean canAffectPlayer (PlayerEntity player) {
        
        return player != null && !player.world.isRemote && !(player instanceof FakePlayer);
    }
}