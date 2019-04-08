package net.darkhax.itemstages;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {
    
    public static Configuration config;
    
    public static boolean allowHoldingRestricted = false;

    public static boolean allowHoldingRestrictedEnchant = false;
    
    public static boolean allowEquipRestricted = false;
    
    public static boolean allowInteractRestricted = false;
    
    public static boolean hideRestrictionsInJEI = true;
    
    public static boolean changeRestrictionTooltip = true;
    
    public ConfigurationHandler(File file) {
        
        config = new Configuration(file);
        this.syncConfigData();
    }
    
    private void syncConfigData () {
        
        allowHoldingRestricted = config.getBoolean("allowHoldingRestricted", Configuration.CATEGORY_GENERAL, false, "Should players be allowed to hold items that are restricted to them.");
        allowHoldingRestrictedEnchant = config.getBoolean("allowHoldingRestrictedEnchant", Configuration.CATEGORY_GENERAL, true, "Should players be allowed to hold items that have an enchantment they is restricted to them.");
        allowEquipRestricted = config.getBoolean("allowEquipRestricted", Configuration.CATEGORY_GENERAL, false, "Should players be allowed to equip items that are restricted to them.");
        allowInteractRestricted = config.getBoolean("allowInteractRestricted", Configuration.CATEGORY_GENERAL, false, "Should players be allowed to interact (left/right click) with items that are restricted to them.");
        hideRestrictionsInJEI = config.getBoolean("hideRestrictionsInJEI", Configuration.CATEGORY_GENERAL, true, "Should restricted items be hidden in JEI?");
        changeRestrictionTooltip = config.getBoolean("changeRestrictionTooltip", Configuration.CATEGORY_GENERAL, true, "Should restricted items have their tooltips changed?");
        
        if (config.hasChanged()) {
            config.save();
        }
    }
}