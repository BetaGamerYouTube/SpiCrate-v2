package de.beta.spicrate.inv.edit.settings;

import de.beta.spicrate.SpiCrate;
import de.beta.spicrate.entry.CrateEntry;
import de.beta.spicrate.entry.CrateItemEntry;
import de.beta.spicrate.entry.StoreEntry;
import de.beta.spicrate.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MainSettingsEditInventory implements Listener {

    private Inventory inv;
    private CrateEntry crateEntry;
    private Player player;
    private List<StoreEntry> stores = new ArrayList<>();

    public MainSettingsEditInventory(Player player, CrateEntry crateEntry) {
        this.crateEntry = crateEntry;
        this.player = player;
        this.inv = Bukkit.createInventory(null, 54, "§aSelect Crate Item to Edit");
        Bukkit.getPluginManager().registerEvents(this, SpiCrate.getInstance());
        prepareInv();
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryClickEvent event) {
        if (!event.getInventory().equals(inv)) return;
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;
        int configId = -1;
        CrateItemEntry crateItemEntry = null;
        for (CrateItemEntry itemEntry : crateEntry.getItems()) {
            ItemStack is = itemEntry.asDebugItem();
            if (clickedItem.equals(is)) {
                crateItemEntry = itemEntry;
                configId = itemEntry.getConfigId();
                break;
            }
        }
        if (configId == -1) return;
        new SettingsEditInventory(player, crateEntry, crateItemEntry, configId);
        HandlerList.unregisterAll(this);
    }

    private void prepareInv() {
        for (CrateItemEntry crateItemEntry : crateEntry.getItems()) {
            inv.setItem(inv.firstEmpty(), crateItemEntry.asDebugItem());
        }
        while (inv.firstEmpty() != -1) {
            inv.setItem(inv.firstEmpty(), new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§a").build());
        }
    }
}
