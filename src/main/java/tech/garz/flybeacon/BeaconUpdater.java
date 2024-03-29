package tech.garz.flybeacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import tech.garz.flybeacon.storage.NotificationType;
import tech.garz.flybeacon.storage.PluginConfig;

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
                    if (Math.abs(
                            beaconLocation.getBlockX() - playerLocation.getBlockX()) <= PluginConfig.BEACON_RANGE) {
                        if (Math.abs(
                                beaconLocation.getBlockZ() - playerLocation.getBlockZ()) <= PluginConfig.BEACON_RANGE) {
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
                    PluginConfig.sendPlayerNotification(NotificationType.INFO, player,
                            PluginConfig.NOTIFICATION_FLY_ENABLED);
                    playersFlying.put(uuid, true);

                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1000, 2, false, false));
                }

                player.setAllowFlight(true);
            } else if (playerFlying) {
                PluginConfig.sendPlayerNotification(NotificationType.WARN, player,
                        PluginConfig.NOTIFICATION_OUT_OF_RANGE);
                playersFlying.put(uuid, false);
            } else {
                if (wasInRange) {
                    PluginConfig.sendPlayerNotification(NotificationType.INFO, player,
                            PluginConfig.NOTIFICATION_FLY_DISABLED);
                    playersFlying.remove(uuid);

                    player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                }

                player.setAllowFlight(false);
            }
        }
    }
}
