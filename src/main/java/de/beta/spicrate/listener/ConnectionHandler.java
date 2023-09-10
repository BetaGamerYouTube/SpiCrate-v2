package de.beta.spicrate.listener;

import de.beta.spicrate.SpiCrate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectionHandler implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SpiCrate.getInstance().getCrateManager().createPlayerIfNotExists(player.getUniqueId());
    }

}
