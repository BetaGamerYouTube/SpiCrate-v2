package de.beta.spicrate.listener;

import de.beta.spicrate.config.Storage;
import de.beta.spicrate.inv.MainInventory;
import de.beta.spicrate.util.ChatMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.List;

public class BlockInteractHandler implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        Location clickedBlockLocation = event.getClickedBlock().getLocation();
        List<Location> locationList = Storage.getLocationList();

        for (Location storedLocation : locationList) {
            if (isSameLocation(clickedBlockLocation, storedLocation)) {
                event.setCancelled(true);
                MainInventory.openMenu(player);
                break;
            }
        }
    }

    private boolean isSameLocation(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX() &&
                loc1.getBlockY() == loc2.getBlockY() &&
                loc1.getBlockZ() == loc2.getBlockZ() &&
                loc1.getWorld().equals(loc2.getWorld());
    }
}