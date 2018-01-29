package net.darkhax.itemstages;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {

    public static Configuration config;

    public static boolean allowHoldingRestricted = false;

    public static boolean allowEquipRestricted = false;

    public ConfigurationHandler (File file) {

        config = new Configuration(file);
        this.syncConfigData();
    }

    private void syncConfigData () {

        allowHoldingRestricted = config.getBoolean("allowHoldingRestricted", Configuration.CATEGORY_GENERAL, false, "Should players be allowed to hold items that are restricted to them.");
        allowEquipRestricted = config.getBoolean("allowEquipRestricted", Configuration.CATEGORY_GENERAL, false, "Should players be allowed to equip items that are restricted to them.");

        if (config.hasChanged()) {
            config.save();
        }
    }
}