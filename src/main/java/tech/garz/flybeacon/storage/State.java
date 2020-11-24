package tech.garz.flybeacon.storage;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tech.garz.flybeacon.FlyBeacon;
import tech.garz.flybeacon.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class State {
    private final File stateFile;
    private FileConfiguration state;

    private ArrayList<FlyBeacon> flyBeacons = new ArrayList<>();

    public State() {
        stateFile = new File(Plugin.getInstance().getDataFolder(), "state.yml");
        if (!stateFile.exists()) {
            stateFile.getParentFile().mkdirs();
            try {
                stateFile.createNewFile();
            } catch (IOException e) {
                Plugin.crash("Could not create state file: " + stateFile.getPath());
                return;
            }
        }

        state = new YamlConfiguration();
        state.options().copyDefaults(true);
        state.addDefault("fly-beacons", Collections.emptyList());
        try {
            state.load(stateFile);

            // Load all beacons from the state
            List<String> sFlyBeacons = state.getStringList("fly-beacons");
            for (String sFlyBeacon : sFlyBeacons) {
                FlyBeacon flyBeacon = new FlyBeacon(sFlyBeacon);
                flyBeacons.add(flyBeacon);
            }
        } catch (InvalidConfigurationException | IllegalArgumentException | IOException e) {
            Plugin.crash("Invalid state configuration detected! Please fix it before restarting the plugin.");
        }
        save();
    }

    public void save() {
        try {
            // Save all beacons into the state
            ArrayList<String> sFlyBeacons = new ArrayList<>();
            for (FlyBeacon flyBeacon : flyBeacons) {
                sFlyBeacons.add(flyBeacon.toString());
            }
            state.set("fly-beacons", sFlyBeacons);

            state.save(stateFile);
        } catch (IOException e) {
            Plugin.warn("Could not save state: " + stateFile.getPath());
        }
    }

    public void addFlyBeacon(FlyBeacon flyBeacon) {
        flyBeacons.add(flyBeacon);
    }

    public void removeFlyBeaconAt(Location l) {
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        flyBeacons = flyBeacons.stream().filter(fb -> {
            Location l2 = fb.getLocation();
            return !(l2.getBlockX() == x && l2.getBlockY() == y && l2.getBlockZ() == z);
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<FlyBeacon> getFlyBeacons() {
        return flyBeacons;
    }
}
