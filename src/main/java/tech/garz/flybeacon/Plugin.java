package tech.garz.flybeacon;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import tech.garz.flybeacon.listeners.BeaconBlockChangeListener;
import tech.garz.flybeacon.storage.NotificationType;
import tech.garz.flybeacon.storage.PluginConfig;
import tech.garz.flybeacon.storage.State;

public final class Plugin extends JavaPlugin {
    private static JavaPlugin plugin;
    private static boolean crashed = false;
    private static State state;
    private static BeaconUpdater beaconUpdater;
    private static BukkitTask beaconUpdaterTask;

    @Override
    public void onEnable() {
        plugin = this;

        PluginConfig.loadConfig(plugin);

        state = new State();
        if (crashed)
            return;

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BeaconBlockChangeListener(), plugin);

        beaconUpdater = new BeaconUpdater();
        beaconUpdaterTask = beaconUpdater.runTaskTimer(plugin, 0L, PluginConfig.UPDATE_INTERVAL);
    }

    @Override
    public void onDisable() {
        for (Player p : getServer().getOnlinePlayers()) {
            if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
                PluginConfig.sendPlayerNotification(NotificationType.INFO, p, PluginConfig.NOTIFICATION_FLY_DISABLED);
                p.setAllowFlight(false);
            }
        }

        if (!crashed) {
            state.save();
        }
    }

    public static void onConfigReloaded() {
        beaconUpdaterTask.cancel();
        beaconUpdaterTask = beaconUpdater.runTaskTimer(plugin, 0L, PluginConfig.UPDATE_INTERVAL);
    }

    public static void disablePlugin() {
        plugin.getPluginLoader().disablePlugin(plugin);
    }

    public static void warn(String message) {
        ConsoleCommandSender sender = plugin.getServer().getConsoleSender();
        sender.sendMessage(ChatColor.YELLOW + message);
    }

    public static void crash(String errorMsg) {
        ConsoleCommandSender sender = plugin.getServer().getConsoleSender();
        sender.sendMessage(ChatColor.RED + errorMsg);
        sender.sendMessage(ChatColor.RED + "Disabling plugin...");
        crashed = true;
        disablePlugin();
    }

    public static JavaPlugin getInstance() {
        return plugin;
    }

    public static State getState() {
        return state;
    }
}
