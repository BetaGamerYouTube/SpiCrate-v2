package de.beta.spicrate.spin;

import de.beta.spicrate.SpiCrate;
import de.beta.spicrate.config.Storage;
import de.beta.spicrate.entry.CrateEntry;
import de.beta.spicrate.entry.CrateItemEntry;
import de.beta.spicrate.util.CaseTask;
import de.beta.spicrate.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ChestInventory {

    private Inventory inventory;
    private CrateEntry crateEntry;
    private Player player;
    private Map<Integer, CrateItemEntry> items = new HashMap<>();
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    public ChestInventory(Player player, CrateEntry crateEntry) {
        super();
        inventory = Bukkit.createInventory(null, 3 * 9, "§aOpening Crate...");
        this.player = player;
        this.crateEntry = crateEntry;
        inventory.setItem( 4, new ItemBuilder(Material.HOPPER).setDisplayName(Storage.getMessage("crate-spin-hopper-name")).build());
        int a = 0;
        for (CrateItemEntry crateItemEntry : crateEntry.getItems()) {
            for (int i = 0; i < crateItemEntry.getChance(); i++)
                items.put(a++, crateItemEntry);
        }
        final CaseTask bukkitRunnable = new CaseTask(this, items.get(random.nextInt(0, items.size())));
        double crateSpeed = (double) Storage.getConfigValue("CrateSpeed");
        bukkitRunnable.runTaskTimer(SpiCrate.getInstance(), 0, (int) ( 20 * crateSpeed));
    }

    public CrateEntry getCrateEntry() {
        return crateEntry;
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

}
