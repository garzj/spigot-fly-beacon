package tech.garz.flybeacon;

import org.bukkit.Location;
import org.bukkit.World;
import tech.garz.flybeacon.storage.PluginConfig;

public class FlyBeacon {
    private final Location location;
    private boolean active;

    public FlyBeacon(String s) throws IllegalArgumentException {
        String[] parts = s.split("(?<!\\\\)\\|");
        if (parts.length != 5) throw new IllegalArgumentException();

        World world = Plugin.getInstance().getServer().getWorld(parts[0].replace("\\|", "|"));
        int x, y, z;
        try {
            x = Integer.parseInt(parts[1]);
            y = Integer.parseInt(parts[2]);
            z = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
        this.location = new Location(world, x, y, z);

        this.active = Boolean.parseBoolean(parts[4]);
    }

    public FlyBeacon(Location location, boolean active) {
        this.location = location;
        this.active = active;
    }

    public String toString() {
        String world = location.getWorld().getName().replace("|", "\\|");
        return world + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ() + "|" + active;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isActive() {
        return active;
    }

    public void updateActiveState() {
        World world = location.getWorld();
        if (world == null) return;
        if (!world.isChunkLoaded(location.getBlockX() / 16, location.getBlockZ() / 16)) return;

        active = true;
        for (int i = 0; active && i < PluginConfig.MINIMUM_TIER; i++) {
            for (int x = location.getBlockX() - (i + 1); active && x <= location.getBlockX() + (i + 1); x++) {
                for (int z = location.getBlockZ() - (i + 1); active && z <= location.getBlockZ() + (i + 1); z++) {
                    if (world.getBlockAt(x, location.getBlockY() - (i + 1), z).getType() != PluginConfig.MINERAL_BLOCK) {
                        active = false;
                    }
                }
            }
        }
    }
}
