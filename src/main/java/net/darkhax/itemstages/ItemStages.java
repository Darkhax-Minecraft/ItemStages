package net.darkhax.itemstages;

import java.util.IdentityHashMap;
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

@Mod(modid = "itemstages", name = "Item Stages", version = "@VERSION@", dependencies = "after:jei@[4.8.2.123,);required-after:bookshelf@[2.2.489,);required-after:gamestages@[1.0.67,);required-after:crafttweaker@[2.7.2.,)", certificateFingerprint = "@FINGERPRINT@")
public class ItemStages {

    public static final LoggingHelper LOG = new LoggingHelper("Item Stages");

    public static final Map<Item, ItemEntry> ITEM_STAGES = new IdentityHashMap<>();

    public static String getStage (ItemStack stack) {

        final ItemEntry entry = ITEM_STAGES.get(stack.getItem());

        if (entry == null) {

            return null;
        }

        return entry.getStage(stack);
    }

    public static void addEntry (Item item, ItemEntry entry) {

        final ItemEntry existing = ITEM_STAGES.get(item);

        if (existing != null) {

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

            final String stage = getStage(stack);

            // No restrictions
            if (stage == null) {

                return false;
            }

            else {

                return !stageData.hasUnlockedStage(stage);
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

            final String stage = getStage(event.getItemStack());

            if (stage != null) {

                event.getToolTip().clear();
                event.getToolTip().add(TextFormatting.WHITE + "Restricted Item");
                event.getToolTip().add(" ");
                event.getToolTip().add(TextFormatting.RED + "" + TextFormatting.ITALIC + "You can not access this item yet.");
                event.getToolTip().add(TextFormatting.RED + "You need stage " + stage + " first.");
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
