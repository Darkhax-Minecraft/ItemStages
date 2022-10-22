package net.darkhax.itemstages;

import java.util.ArrayList;
import java.util.List;

import net.darkhax.bookshelf.api.util.TextHelper;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.darkhax.gamestages.data.IStageData;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraftforge.fml.loading.FMLEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("itemstages")
public class ItemStages {
    
    public static final Logger LOGGER = LoggerFactory.getLogger("Item Stages");
    
    public ItemStages() {
        
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(this::onItemPickup);
        MinecraftForge.EVENT_BUS.addListener(this::onItemUsed);
        MinecraftForge.EVENT_BUS.addListener(this::onEntityHurt);
        
        MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);
        
        if (FMLEnvironment.dist.isClient()) {
            
            MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);
        }
    }
    
    private void addReloadListeners (AddReloadListenerEvent event) {
        
        event.addListener(RestrictionManager.INSTANCE);
    }
    
    private void onEntityHurt (LivingAttackEvent event) {
        
        if (this.canAffectPlayer(event.getSource()) && event.getSource().getEntity() instanceof final Player player) {

            final ItemStack stack = player.getMainHandItem();
            final Restriction restriction = RestrictionManager.INSTANCE.getRestriction(player, stack);
            
            if (restriction != null && restriction.shouldPreventAttacking()) {
                
                event.setCanceled(true);
                
                final Component message = restriction.getAttackMessage(stack);
                player.sendMessage(message, Util.NIL_UUID);
            }
        }
    }
    
    private void onItemUsed (PlayerInteractEvent event) {
        
        if (event.isCancelable() && this.canAffectPlayer(event.getPlayer())) {
            
            final ItemStack stack = event.getItemStack();
            final Restriction restriction = RestrictionManager.INSTANCE.getRestriction(event.getPlayer(), stack);
            
            if (restriction != null && restriction.shouldPreventUsing()) {
                
                event.setCanceled(true);
                
                final Component message = restriction.getUsageMessage(stack);
                event.getPlayer().sendMessage(message, Util.NIL_UUID);
            }
        }
    }
    
    private void onItemPickup (EntityItemPickupEvent event) {
        
        if (this.canAffectPlayer(event.getPlayer())) {
            
            final ItemStack stack = event.getItem().getItem();
            final Restriction restriction = RestrictionManager.INSTANCE.getRestriction(event.getPlayer(), stack);
            
            if (restriction != null && restriction.shouldPreventPickup()) {
                
                event.setCanceled(true);
                event.getItem().setPickUpDelay(restriction.getPickupDelay());
                // TODO consider extending life span by default delay.
                
                final Component message = restriction.getPickupMessage(stack);
                event.getPlayer().sendMessage(message, Util.NIL_UUID);
            }
        }
    }
    
    private void onPlayerTick (TickEvent.PlayerTickEvent event) {
        
        if (event.phase == Phase.START && event.player != null && !event.player.level.isClientSide && !(event.player instanceof FakePlayer)) {
            
            final Player player = event.player;
            final IStageData stageData = GameStageHelper.getPlayerData(player);
            final Inventory inv = player.getInventory();
            
            final int armorStart = inv.items.size();
            final int armorEnd = armorStart + inv.armor.size();
            
            for (int slot = 0; slot < inv.getContainerSize(); slot++) {
                
                final ItemStack slotContent = inv.getItem(slot);
                
                if (!slotContent.isEmpty()) {
                    
                    // Armor
                    if (slot >= armorStart && slot <= armorEnd) {
                        
                        final Restriction restriction = RestrictionManager.INSTANCE.getEquipmentRestriction(player, stageData, slotContent);
                        
                        if (restriction != null && restriction.shouldPreventEquipment()) {
                            
                            inv.setItem(slot, ItemStack.EMPTY);
                            player.drop(slotContent, false);
                            
                            final Component message = restriction.getDropMessage(slotContent);
                            
                            if (message != null) {
                                
                                player.sendMessage(message, Util.NIL_UUID);
                            }
                        }
                    }
                    
                    // Inventory
                    else {
                        
                        final Restriction restriction = RestrictionManager.INSTANCE.getInventoryRestriction(player, stageData, slotContent);
                        
                        if (restriction != null && restriction.shouldPreventInventory()) {
                            
                            inv.setItem(slot, ItemStack.EMPTY);
                            player.drop(slotContent, false);
                            
                            final Component message = restriction.getDropMessage(slotContent);
                            
                            if (message != null) {
                                
                                player.sendMessage(message, Util.NIL_UUID);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void onItemTooltip (ItemTooltipEvent event) {
        
        if (event.getPlayer() != null) {
            
            final Player player = event.getPlayer();
            final IStageData data = GameStageSaveHandler.getClientData();
            final ItemStack stack = event.getItemStack();
            
            final Restriction restriction = RestrictionManager.INSTANCE.getRestriction(player, data, stack);
            
            if (restriction != null) {
                
                event.getToolTip().clear();
                final Component hiddenName = restriction.getHiddenName(stack);
                
                if (hiddenName != null) {
                    
                    event.getToolTip().add(hiddenName);
                }
                
                // Debug tooltip shows which stages the player doesn't have.
                if (event.getFlags().isAdvanced()) {
                    
                    final List<Component> stages = new ArrayList<>();
                    
                    final Component sep = new TextComponent(", ").withStyle(ChatFormatting.GRAY);
                    
                    for (final String stage : restriction.getStages()) {
                        
                        stages.add(new TextComponent(stage).withStyle(data.hasStage(stage) ? ChatFormatting.GREEN : ChatFormatting.RED));
                    }

                    //TODO TextUtils.join(sep, stages)
                    final Component desc = new TranslatableComponent("tooltip.itemstages.item.description", TextHelper.lookupTranslation(String.valueOf(sep), stages)).withStyle(ChatFormatting.GRAY);
                    event.getToolTip().add(desc);
                    
                    if (restriction.shouldPreventInventory()) {
                        
                        event.getToolTip().add(new TranslatableComponent("tooltip.itemstages.debug.drop").withStyle(ChatFormatting.RED));
                    }
                    
                    if (restriction.shouldPreventPickup()) {
                        
                        event.getToolTip().add(new TranslatableComponent("tooltip.itemstages.debug.pickup").withStyle(ChatFormatting.RED));
                    }
                    
                    if (restriction.shouldPreventUsing()) {
                        
                        event.getToolTip().add(new TranslatableComponent("tooltip.itemstages.debug.use").withStyle(ChatFormatting.RED));
                    }
                    
                    if (restriction.shouldPreventAttacking()) {
                        
                        event.getToolTip().add(new TranslatableComponent("tooltip.itemstages.debug.attack").withStyle(ChatFormatting.RED));
                    }
                    
                    if (restriction.shouldHideInJEI()) {
                        
                        event.getToolTip().add(new TranslatableComponent("tooltip.itemstages.debug.jei").withStyle(ChatFormatting.RED));
                    }
                }
            }
        }
    }
    
    private boolean canAffectPlayer (DamageSource source) {
        
        return source != null && source.getEntity() instanceof Player && this.canAffectPlayer((Player) source.getEntity());
    }
    
    private boolean canAffectPlayer (Player player) {
        
        return player != null && !player.level.isClientSide && !(player instanceof FakePlayer);
    }
}