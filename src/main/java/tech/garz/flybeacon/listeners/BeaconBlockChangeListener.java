package tech.garz.flybeacon.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import tech.garz.flybeacon.FlyBeacon;
import tech.garz.flybeacon.Plugin;

public class BeaconBlockChangeListener implements Listener {
    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (block.getType() == Material.BEACON) {
            Plugin.getState().removeFlyBeaconAt(block.getLocation());
        }
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlockPlaced();
        if (block.getType() == Material.BEACON) {
            Plugin.getState().addFlyBeacon(new FlyBeacon(block.getLocation(), false));
        }
    }
}
