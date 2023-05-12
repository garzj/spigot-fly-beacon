package tech.garz.flybeacon.storage;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import tech.garz.flybeacon.Plugin;

public class PluginConfig {
    private static JavaPlugin plugin;

    private static final String INVALID_CONFIG_MSG = "Invalid configuration detected! Could not match ";

    public static int BEACON_RANGE;
    public static int MINIMUM_TIER;
    public static Material MINERAL_BLOCK;
    public static long UPDATE_INTERVAL;
    public static NotificationForm NOTIFICATION_FORM;
    public static NotificationType NOTIFICATION_TYPE;
    public static String NOTIFICATION_FLY_ENABLED;
    public static String NOTIFICATION_OUT_OF_RANGE;
    public static String NOTIFICATION_FLY_DISABLED;

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
        config.addDefault("notification-type", "ALL");
        config.addDefault("notification-form", "ACTIONBAR");
        config.addDefault("notification-messages.fly-enabled", "§bFly enabled.");
        config.addDefault("notification-messages.out-of-range", "§cBeacon out of range. %t ticks left.");
        config.addDefault("notification-messages.fly-disabled", "§cFly disabled.");
        plugin.saveConfig();

        BEACON_RANGE = config.getInt("beacon-range");
        MINIMUM_TIER = config.getInt("minimum-tier");
        String mineralBlock = config.getString("mineral-block");
        MINERAL_BLOCK = Material.matchMaterial(mineralBlock);
        if (MINERAL_BLOCK == null) {
            Plugin.crash(INVALID_CONFIG_MSG + "the material " + mineralBlock + ".");
        }
        UPDATE_INTERVAL = config.getLong("update-interval");

        try {
            NOTIFICATION_TYPE = NotificationType.valueOf(NotificationType.class, config.getString("notification-type"));
        } catch (IllegalArgumentException e) {
            Plugin.crash(
                    INVALID_CONFIG_MSG + "notification-type with one of " + NotificationType.valuesToString() + ".");
        }
        try {
            NOTIFICATION_FORM = NotificationForm.valueOf(NotificationForm.class, config.getString("notification-form"));
        } catch (IllegalArgumentException e) {
            Plugin.crash(
                    INVALID_CONFIG_MSG + "notification-form with one of " + NotificationForm.valuesToString() + ".");
        }
        NOTIFICATION_FLY_ENABLED = config.getString("notification-messages.fly-enabled");
        NOTIFICATION_OUT_OF_RANGE = config.getString("notification-messages.out-of-range");
        NOTIFICATION_FLY_DISABLED = config.getString("notification-messages.fly-disabled");
    }

    public static void reload() {
        reloadConfig();
        Plugin.onConfigReloaded();
    }

    public static void sendPlayerNotification(NotificationType notificationType, Player player, String message) {
        if (NOTIFICATION_TYPE != notificationType && NOTIFICATION_TYPE != NotificationType.ALL)
            return;

        message = message.replace("%t", "" + UPDATE_INTERVAL);

        ChatMessageType cmt = PluginConfig.NOTIFICATION_FORM == NotificationForm.ACTIONBAR ? ChatMessageType.ACTION_BAR
                : ChatMessageType.CHAT;
        player.spigot()
                .sendMessage(cmt, new TextComponent(message));
    }
}
