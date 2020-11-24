package tech.garz.flybeacon;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import tech.garz.flybeacon.storage.PluginConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BeaconUpdater extends BukkitRunnable {
    private HashMap<UUID, Boolean> playersFlying = new HashMap<>();

    @Override
    public void run() {
        ArrayList<FlyBeacon> flyBeacons = Plugin.getState().getFlyBeacons();
        for (FlyBeacon flyBeacon : flyBeacons) {
            flyBeacon.updateActiveState();
        }

        for (Player player : Plugin.getInstance().getServer().getOnlinePlayers()) {
            GameMode gm = player.getGameMode();
            if (gm.equals(GameMode.CREATIVE) || gm.equals(GameMode.SPECTATOR)) {
                continue;
            }

            boolean inBeaconRange = false;
            Location playerLocation = player.getLocation();
            for (FlyBeacon flyBeacon : flyBeacons) {
                Location beaconLocation = flyBeacon.getLocation();
                if (flyBeacon.isActive()) {
                    if (Math.abs(beaconLocation.getBlockX() - playerLocation.getBlockX()) <= PluginConfig.BEACON_RANGE) {
                        if (Math.abs(beaconLocation.getBlockZ() - playerLocation.getBlockZ()) <= PluginConfig.BEACON_RANGE) {
                            inBeaconRange = true;
                        }
                    }
                }
            }

            UUID uuid = player.getUniqueId();
            boolean wasInRange = playersFlying.containsKey(uuid);
            boolean playerFlying = wasInRange ? playersFlying.get(uuid) : false;
            if (inBeaconRange) {
                if (!playerFlying) {
                    player.sendMessage(ChatColor.AQUA + "Fly mode enabled!");
                    playersFlying.put(uuid, true);

                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1000, 2, false, false));
                }

                player.setAllowFlight(true);
            } else if (playerFlying) {
                player.sendMessage(ChatColor.RED + "Updating fly mode in " + (PluginConfig.UPDATE_INTERVAL) + " ticks...");
                playersFlying.put(uuid, false);
            } else {
                if (wasInRange) {
                    player.sendMessage(ChatColor.RED + "Fly mode disabled!");
                    playersFlying.remove(uuid);

                    player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                }

                player.setAllowFlight(false);
            }
        }
    }
}
