package net.darkhax.itemstages;

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
import net.darkhax.gamestages.capabilities.PlayerDataHandler;
import net.darkhax.gamestages.capabilities.PlayerDataHandler.IStageData;
import net.darkhax.gamestages.event.GameStageEvent;
import net.darkhax.gamestages.event.StageDataEvent;
import net.darkhax.itemstages.jei.PluginItemStages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextComponentString;
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
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "itemstages", name = "Item Stages", version = "@VERSION@", dependencies = "after:jei@[4.8.2.123,);required-after:bookshelf@[2.2.525,);required-after:gamestages@[1.0.75,);required-after:crafttweaker@[4.1.4.,)", certificateFingerprint = "@FINGERPRINT@")
public class ItemStages {

    public static final LoggingHelper LOG = new LoggingHelper("Item Stages");

    public static final ItemStackMap<String> ITEM_STAGES = new ItemStackMap<>(StageCompare.INSTANCE);
    public static final ListMultimap<String, ItemStack> SORTED_STAGES = ArrayListMultimap.create();
    public static final SetMultimap<Item, Tuple<ItemStack, String>> SORTED_ITEM_STAGES = Multimaps.newSetMultimap(Maps.newIdentityHashMap(), Sets::newIdentityHashSet);
    public static final ListMultimap<String, FluidStack> FLUID_STAGES = ArrayListMultimap.create();

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

    private static void sendDropMessage (EntityPlayer player, ItemStack stack) {

        player.sendMessage(new TextComponentString("You dropped the " + stack.getDisplayName() + "! Further progression is required."));
    }

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        new ConfigurationHandler(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerDig (BreakSpeed event) {

        if (!ConfigurationHandler.allowInteractRestricted && !event.getEntityPlayer().isCreative()) {

            final IStageData data = PlayerDataHandler.getStageData(event.getEntityPlayer());
            final String stage = getStage(event.getEntityPlayer().getHeldItemMainhand());

            if (stage != null && !data.hasUnlockedStage(stage)) {

                event.setNewSpeed(-1f);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract (PlayerInteractEvent event) {

        if (event.isCancelable() && !ConfigurationHandler.allowInteractRestricted && !event.getEntityPlayer().isCreative()) {

            final IStageData data = PlayerDataHandler.getStageData(event.getEntityPlayer());
            final String stage = getStage(event.getItemStack());

            if (stage != null && !data.hasUnlockedStage(stage)) {

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate (LivingUpdateEvent event) {

        if (PlayerUtils.isPlayerReal(event.getEntityLiving())) {

            final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            final IStageData data = PlayerDataHandler.getStageData(player);

            // Exit early if creative mode.
            if (player.isCreative() || data == null) {

                return;
            }

            for (final EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {

                // Skips the item if the slot type was configured to be ignored.
                if (ConfigurationHandler.allowHoldingRestricted && slot.getSlotType() == Type.HAND || ConfigurationHandler.allowEquipRestricted && slot.getSlotType() == Type.ARMOR) {

                    continue;
                }

                final ItemStack stack = player.getItemStackFromSlot(slot);
                final String stage = getStage(stack);

                if (stage != null && !data.hasUnlockedStage(stage)) {

                    player.setItemStackToSlot(slot, ItemStack.EMPTY);
                    player.dropItem(stack, false);
                    sendDropMessage(player, stack);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltip (ItemTooltipEvent event) {

        final String stage = getStage(event.getItemStack());
        final IStageData data = PlayerDataHandler.getStageData(event.getEntityPlayer());

        if (stage != null && data != null && !data.hasUnlockedStage(stage)) {

            event.getToolTip().clear();
            event.getToolTip().add(TextFormatting.WHITE + "Restricted Item");
            event.getToolTip().add(" ");
            event.getToolTip().add(TextFormatting.RED + "" + TextFormatting.ITALIC + "You can not access this item yet.");
            event.getToolTip().add(TextFormatting.RED + "You need stage " + stage + " first.");
        }

        else if (stage != null && (event.getEntityPlayer() != null && event.getEntityPlayer().isCreative() || event.getFlags() == ITooltipFlag.TooltipFlags.ADVANCED)) {

            event.getToolTip().add(TextFormatting.BLUE + "Stage: " + TextFormatting.WHITE + stage);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onGamestageSync (StageDataEvent.SyncRecieved event) {

        if (Loader.isModLoaded("jei") && GameUtils.isClient()) {

            PluginItemStages.syncHiddenItems(event.getEntityPlayer());
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientSync (GameStageEvent.ClientSync event) {

        if (Loader.isModLoaded("jei") && GameUtils.isClient()) {

            PluginItemStages.syncHiddenItems(event.getEntityPlayer());
        }
    }

    @EventHandler()
    public void onLoadComplete (FMLLoadCompleteEvent event) {

        LOG.info("Sorting {} staged items.", ITEM_STAGES.size());
        final long time = System.currentTimeMillis();

        for (final Entry<ItemStack, String> entry : ITEM_STAGES.entrySet()) {

            SORTED_STAGES.put(entry.getValue(), entry.getKey());
            SORTED_ITEM_STAGES.put(entry.getKey().getItem(), new Tuple<>(entry.getKey(), entry.getValue()));
        }

        LOG.info("Sorting complete. Found {} stages. Took {}ms", SORTED_STAGES.keySet().size(), System.currentTimeMillis() - time);
    }

    @EventHandler()
    @SideOnly(Side.CLIENT)
    public void onClientLoadComplete (FMLLoadCompleteEvent event) {

        // Add a resource reload listener to keep up to sync with JEI.
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(listener -> {

            if (Loader.isModLoaded("jei") && GameUtils.isClient()) {

                PluginItemStages.syncHiddenItems(PlayerUtils.getClientPlayer());
            }
        });
    }

    @EventHandler
    public void onFingerprintViolation (FMLFingerprintViolationEvent event) {

        LOG.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}
