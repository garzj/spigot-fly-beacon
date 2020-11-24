package tech.garz.flybeacon.storage;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tech.garz.flybeacon.Plugin;

public class PluginConfig {
    private static JavaPlugin plugin;

    public static int BEACON_RANGE;
    public static int MINIMUM_TIER;
    public static Material MINERAL_BLOCK;
    public static long UPDATE_INTERVAL;

    public static void loadConfig(JavaPlugin plugin) {
        PluginConfig.plugin = plugin;
        reloadConfig();
    }

    private static void reloadConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        config.options().copyDefaults(true);
        config.addDefault("beacon-range", 42);
        config.addDefault("minimum-tier", 4);
        config.addDefault("mineral-block", "DIAMOND_BLOCK");
        config.addDefault("update-interval", 80);
        plugin.saveConfig();

        BEACON_RANGE = config.getInt("beacon-range");
        MINIMUM_TIER = config.getInt("minimum-tier");
        String mineralBlock = config.getString("mineral-block");
        MINERAL_BLOCK = Material.matchMaterial(mineralBlock);
        if (MINERAL_BLOCK == null) {
            Plugin.crash("Invalid configuration detected! Could not match the material " + mineralBlock + ".");
        }
        UPDATE_INTERVAL = config.getLong("update-interval");
    }

    public static void reload() {
        reloadConfig();
        Plugin.onConfigReloaded();
    }
}
