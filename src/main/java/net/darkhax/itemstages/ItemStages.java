package net.darkhax.itemstages;

import java.util.HashMap;
import java.util.Map;

import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.gamestages.capabilities.PlayerDataHandler;
import net.darkhax.gamestages.capabilities.PlayerDataHandler.IStageData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "itemstages", name = "Item Stages", version = "@VERSION@", dependencies = "required-after:bookshelf@[2.1.443,);required-after:gamestages@[1.0.63,);required-after:crafttweaker@[2.7.2.,)", certificateFingerprint = "@FINGERPRINT@")
public class ItemStages {

    public static final LoggingHelper LOG = new LoggingHelper("Item Stages");

    public static final Map<Item, ItemEntry> ITEM_STAGES = new HashMap<>();

    private boolean isRestricted (EntityPlayer player, ItemStack stack) {

        final IStageData stageData = PlayerDataHandler.getStageData(player);

        if (stageData != null && !stack.isEmpty()) {

            final ItemEntry entry = ITEM_STAGES.get(stack.getItem());

            // No restrictions
            if (entry == null || !entry.matches(stack)) {

                return false;
            }

            else {

                return !stageData.hasUnlockedStage(entry.getStage());
            }
        }

        // default to restricted
        return true;
    }

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingUpdate (LivingUpdateEvent event) {

        if (PlayerUtils.isPlayerReal(event.getEntityLiving())) {

            final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            final ItemStack stack = player.getHeldItemMainhand();

            if (!stack.isEmpty() && this.isRestricted(player, stack)) {

                player.sendMessage(new TextComponentString("You dropped the " + stack.getDisplayName() + "! Further progression is required."));
                player.dropItem(true);
                return;
            }
        }
    }

    @EventHandler
    public void onFingerprintViolation (FMLFingerprintViolationEvent event) {

        LOG.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}
