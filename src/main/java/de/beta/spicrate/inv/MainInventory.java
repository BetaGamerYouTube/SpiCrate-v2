package de.beta.spicrate.inv;

import de.beta.spicrate.SpiCrate;
import de.beta.spicrate.config.Storage;
import de.beta.spicrate.entry.CrateEntry;
import de.beta.spicrate.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class MainInventory {

    public static void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 3*9, "§aCrate Overview");
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§a").build());
        }
        List<CrateEntry> crates = Storage.getCrates();
        for (CrateEntry entry : crates) {
            inv.setItem(entry.getSlot(), entry.toItemStack(SpiCrate.getInstance().getCrateManager().getAbstractAmount(player.getUniqueId(), entry)));
        }
        player.openInventory(inv);
    }

}
