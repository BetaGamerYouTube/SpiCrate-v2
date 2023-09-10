package de.beta.spicrate.listener;

import de.beta.spicrate.SpiCrate;
import de.beta.spicrate.config.Storage;
import de.beta.spicrate.entry.CrateEntry;
import de.beta.spicrate.inv.PreviewInventory;
import de.beta.spicrate.spin.ChestInventory;
import de.beta.spicrate.util.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryClickHandler implements Listener {

    private HashMap<Player, Long> timeout = new HashMap<>();

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory clickedInventory = event.getClickedInventory();
        final ItemStack currentItem = event.getCurrentItem();
        final String inventoryName = event.getView().getTitle();
        if (clickedInventory == null) return;
        if (!inventoryName.equalsIgnoreCase("§aCrate Overview") && !inventoryName.equalsIgnoreCase("§aCrate Preview") && !inventoryName.equalsIgnoreCase("§aOpening Crate...")) return;
        event.setResult(Event.Result.DENY);
        event.setCancelled(true);
        if (timeout.containsKey(player)) {
            if (System.currentTimeMillis() >= timeout.get(player)) {
                timeout.remove(player);
            } else {
                player.closeInventory();
                player.sendMessage(ChatMessage.color(Storage.getMessage("crates-cooldown")));
                return;
            }
        }
        if (currentItem == null || currentItem.getType() == Material.AIR) return;
        if (!currentItem.getItemMeta().hasLocalizedName()) return;
        String crateName = currentItem.getItemMeta().getLocalizedName();
        if (crateName.isEmpty()) return;
        CrateEntry crateEntry = SpiCrate.getInstance().getCrateManager().getCrateByName(crateName);
        if (crateEntry == null) return;
        // Right Click
        if (event.isRightClick()) {
            PreviewInventory.openPreviewMenu(player, crateEntry);
            return;
        }
        // Left Click
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatMessage.color(Storage.getMessage("crate-full-inventory")));
            player.closeInventory();
            return;
        }
        SpiCrate.getInstance().getCrateManager().loadChestAmount(player.getUniqueId(), crateName, integer -> {
            if (integer >= 1) {
                SpiCrate.getInstance().getCrateManager().setCrate(player.getUniqueId(), crateEntry, --integer);
                Bukkit.getScheduler().runTask(SpiCrate.getInstance(), () -> {
                    player.openInventory(new ChestInventory(player, crateEntry).getInventory());
                });
            } else {
                player.sendMessage(ChatMessage.color(Storage.getMessage("crates-not-enough")));
                return;
            }
        });

        timeout.put(player, System.currentTimeMillis() + 1500);
    }

}
