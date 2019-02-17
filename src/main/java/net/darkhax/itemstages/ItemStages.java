package net.darkhax.itemstages;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import net.darkhax.bookshelf.lib.ItemStackMap;
import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.util.GameUtils;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.event.StagesSyncedEvent;
import net.darkhax.itemstages.compat.jei.PluginItemStages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "itemstages", name = "Item Stages", version = "@VERSION@", dependencies = "after:jei@[4.14.4.264,);required-after:bookshelf;required-after:gamestages@[2.0.114,);required-after:crafttweaker", certificateFingerprint = "@FINGERPRINT@")
public class ItemStages {
    
    public static final LoggingHelper LOG = new LoggingHelper("Item Stages");
    
    public static final ItemStackMap<String> ITEM_STAGES = new ItemStackMap<>(StageCompare.INSTANCE);
    public static final ItemStackMap<String> CUSTOM_NAMES = new ItemStackMap<>(StageCompare.INSTANCE);
    
    public static final ListMultimap<String, ItemStack> SORTED_STAGES = ArrayListMultimap.create();
    public static final SetMultimap<Item, Tuple<ItemStack, String>> SORTED_ITEM_STAGES = Multimaps.newSetMultimap(Maps.newIdentityHashMap(), Sets::newIdentityHashSet);
    public static final ListMultimap<String, FluidStack> FLUID_STAGES = ArrayListMultimap.create();
    public static final ListMultimap<String, String> tooltipStages = ArrayListMultimap.create();
    public static final ListMultimap<String, String> recipeCategoryStages = ArrayListMultimap.create();
    
    public static String getStage (ItemStack stack) {
        
        if (!stack.isEmpty()) {
            
            for (final Tuple<ItemStack, String> entry : SORTED_ITEM_STAGES.get(stack.getItem())) {
                
                if (StageCompare.INSTANCE.isValid(stack, entry.getFirst())) {
                    
                    return entry.getSecond();
                }
            }
        }
        
        return null;
    }
    
    private static String getUnfamiliarName (ItemStack stack) {
        
        return CUSTOM_NAMES.containsKey(stack) ? CUSTOM_NAMES.get(stack) : "Unfamiliar Item";
    }
    
    private static void sendDropMessage (EntityPlayer player, ItemStack stack) {
        
        player.sendStatusMessage(new TextComponentTranslation(TRANSLATE_DROP, getUnfamiliarName(stack)), false);
    }
    
    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        
        new ConfigurationHandler(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onPlayerDig (BreakSpeed event) {
        
        if (!ConfigurationHandler.allowInteractRestricted && !event.getEntityPlayer().isCreative()) {
            
            final String stage = getStage(event.getEntityPlayer().getHeldItemMainhand());
            
            if (stage != null && !GameStageHelper.hasStage(event.getEntityPlayer(), stage)) {
                
                event.setNewSpeed(-1f);
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerInteract (PlayerInteractEvent event) {
        
        if (event.isCancelable() && !ConfigurationHandler.allowInteractRestricted && !event.getEntityPlayer().isCreative()) {
            
            final String stage = getStage(event.getItemStack());
            
            if (stage != null && !GameStageHelper.hasStage(event.getEntityPlayer(), stage)) {
                
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void onLivingUpdate (LivingUpdateEvent event) {
        
        if (event.getEntity() instanceof EntityPlayer && !event.getEntityLiving().world.isRemote) {
            
            final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            
            // Exit early if creative mode.
            if (player.isCreative()) {
                
                return;
            }
            
            for (final EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                
                // Skips the item if the slot type was configured to be ignored.
                if (ConfigurationHandler.allowHoldingRestricted && slot.getSlotType() == Type.HAND || ConfigurationHandler.allowEquipRestricted && slot.getSlotType() == Type.ARMOR) {
                    
                    continue;
                }
                
                final ItemStack stack = player.getItemStackFromSlot(slot);
                final String stage = getStage(stack);
                
                if (stage != null && !GameStageHelper.hasStage(player, stage)) {
                    
                    player.setItemStackToSlot(slot, ItemStack.EMPTY);
                    player.dropItem(stack, false);
                    sendDropMessage(player, stack);
                }
            }
        }
    }
    
    private static final String TRANSLATE_DESCRIPTION = "tooltip.itemstages.description";
    private static final String TRANSLATE_INFO = "tooltip.itemstages.info";
    private static final String TRANSLATE_STAGE = "tooltip.itemstages.stage";
    private static final String TRANSLATE_DROP = "message.itemstages.drop";
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltip (ItemTooltipEvent event) {
        
        final EntityPlayerSP player = PlayerUtils.getClientPlayerSP();
        
        if (player != null) {
            
            
            final String itemsStage = getStage(event.getItemStack());
            
            // Add message to items when the player doesn't have access to it.
            if (itemsStage != null && !GameStageHelper.hasStage(player, itemsStage) && ConfigurationHandler.changeRestrictionTooltip) {
                
                event.getToolTip().clear();
                event.getToolTip().add(TextFormatting.WHITE + getUnfamiliarName(event.getItemStack()));
                event.getToolTip().add(" ");
                event.getToolTip().add(TextFormatting.RED + "" + TextFormatting.ITALIC + I18n.format(TRANSLATE_DESCRIPTION));
                event.getToolTip().add(TextFormatting.RED + I18n.format(TRANSLATE_INFO, itemsStage));
            }
            
            // Adds info about which stage the item is added to. This is more of a debug thing.
            else if (itemsStage != null && (event.getEntityPlayer() != null && event.getEntityPlayer().isCreative() || event.getFlags() == ITooltipFlag.TooltipFlags.ADVANCED)) {
                
                event.getToolTip().add(TextFormatting.BLUE + I18n.format(TRANSLATE_STAGE) + " " + TextFormatting.WHITE + itemsStage);
            }
            
            // Removes tooltip info that has been restricted.
            for (String tipStage : tooltipStages.keySet()) {
                
                if (!GameStageHelper.hasStage(player, tipStage)) {
                    
                    for (final Iterator<String> iterator = event.getToolTip().iterator(); iterator.hasNext();) {
                        
                        final String tooltipLine = iterator.next();
                        
                        for (String restricted : tooltipStages.get(tipStage)) {
                            
                            if (tooltipLine.startsWith(restricted)) {
                                
                                iterator.remove();
                            }
                        }
                    }
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientSync (StagesSyncedEvent event) {
        
        if (Loader.isModLoaded("jei") && GameUtils.isClient()) {
            
            PluginItemStages.syncHiddenItems(event.getEntityPlayer());
        }
    }
    
    @EventHandler
    public void onLoadComplete (FMLLoadCompleteEvent event) {
        
        LOG.info("Sorting {} staged items.", ITEM_STAGES.size());
        final long time = System.currentTimeMillis();
        
        for (final Entry<ItemStack, String> entry : ITEM_STAGES.entrySet()) {
            
            SORTED_STAGES.put(entry.getValue(), entry.getKey());
            SORTED_ITEM_STAGES.put(entry.getKey().getItem(), new Tuple<>(entry.getKey(), entry.getValue()));
        }
        
        LOG.info("Sorting complete. Found {} stages. Took {}ms", SORTED_STAGES.keySet().size(), System.currentTimeMillis() - time);
    }
    
    @EventHandler
    @SideOnly(Side.CLIENT)
    public void onClientLoadComplete (FMLLoadCompleteEvent event) {
        
        // Add a resource reload listener to keep up to sync with JEI.
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(listener -> {
            
            if (Loader.isModLoaded("jei") && GameUtils.isClient()) {
                
                LOG.info("Resyncing JEI info.");
                PluginItemStages.syncHiddenItems(PlayerUtils.getClientPlayerSP());
            }
        });
    }
}