package net.darkhax.itemstages;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.util.GameUtils;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.gamestages.capabilities.PlayerDataHandler;
import net.darkhax.gamestages.capabilities.PlayerDataHandler.IStageData;
import net.darkhax.gamestages.event.GameStageEvent;
import net.darkhax.gamestages.event.StageDataEvent;
import net.darkhax.itemstages.jei.PluginItemStages;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "itemstages", name = "Item Stages", version = "@VERSION@", dependencies = "after:jei@[4.8.0.110,);required-after:bookshelf@[2.2.467,);required-after:gamestages@[1.0.67,);required-after:crafttweaker@[2.7.2.,)", certificateFingerprint = "@FINGERPRINT@")
public class ItemStages {

    public static final LoggingHelper LOG = new LoggingHelper("Item Stages");

    public static final Map<Item, ItemEntry> ITEM_STAGES = new HashMap<>();

    public static ItemEntry getEntry (ItemStack stack) {

        final ItemEntry entry = ITEM_STAGES.get(stack.getItem());
        return entry != null && entry.hasStack(stack) ? entry : null;
    }

    public static void addEntry (Item item, ItemEntry entry) {

        if (ITEM_STAGES.containsKey(item)) {

            final ItemEntry existing = ITEM_STAGES.get(item);

            for (final Entry<String, ItemStack[]> entries : entry.entries.entrySet()) {

                existing.add(entries.getKey(), entries.getValue());
            }
        }

        else {

            ITEM_STAGES.put(item, entry);
        }
    }

    public static boolean isRestricted (EntityPlayer player, ItemStack stack) {

        // Air is not restricted
        if (stack.isEmpty()) {

            return false;
        }

        final IStageData stageData = PlayerDataHandler.getStageData(player);

        if (stageData != null) {

            final ItemEntry entry = getEntry(stack);

            // No restrictions
            if (entry == null) {

                return false;
            }

            else {

                return !stageData.hasUnlockedStage(entry.getStage(stack));
            }
        }

        // default to restricted
        return true;
    }

    private static void sendDropMessage (EntityPlayer player, ItemStack stack) {

        player.sendMessage(new TextComponentString("You dropped the " + stack.getDisplayName() + "! Further progression is required."));
    }

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingUpdate (LivingUpdateEvent event) {

        if (PlayerUtils.isPlayerReal(event.getEntityLiving())) {

            final EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            // Exit early if creative mode.
            if (player.isCreative()) {

                return;
            }

            for (final EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {

                final ItemStack stack = player.getItemStackFromSlot(slot);

                if (isRestricted(player, stack)) {

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

        if (!event.getItemStack().isEmpty() && isRestricted(event.getEntityPlayer(), event.getItemStack())) {

            final ItemEntry entry = getEntry(event.getItemStack());

            if (entry != null) {

                event.getToolTip().clear();
                event.getToolTip().add(TextFormatting.WHITE + "Restricted Item");
                event.getToolTip().add(TextFormatting.RED + "" + TextFormatting.ITALIC + "Further progression is required to access this item. You need stage " + entry.getStage(event.getItemStack()) + " first.");
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onGamestageSync (StageDataEvent.SyncRecieved event) {

        if (Loader.isModLoaded("jei") && GameUtils.isClient()) {

            PluginItemStages.syncHiddenItems(event.getPlayer());
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onStageAdded (GameStageEvent.Added event) {

        if (Loader.isModLoaded("jei") && GameUtils.isClient()) {

            PluginItemStages.syncHiddenItems(event.getPlayer());
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onStageRemoved (GameStageEvent.Removed event) {

        if (Loader.isModLoaded("jei") && GameUtils.isClient()) {

            PluginItemStages.syncHiddenItems(event.getPlayer());
        }
    }

    @EventHandler
    public void onFingerprintViolation (FMLFingerprintViolationEvent event) {

        LOG.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}
