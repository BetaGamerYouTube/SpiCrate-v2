package de.beta.spicrate.inv;

import de.beta.spicrate.config.Storage;
import de.beta.spicrate.entry.CrateEntry;
import de.beta.spicrate.entry.CrateItemEntry;
import de.beta.spicrate.util.ChatMessage;
import de.beta.spicrate.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class PreviewInventory {

    private static final String INVENTORY_TITLE = ChatMessage.color("&aCrate Preview");

    public static void openPreviewMenu(Player player, CrateEntry crateEntry) {
        int invSize = getInvSize(crateEntry.getItems().size());
        Inventory inv = Bukkit.createInventory(null, invSize, INVENTORY_TITLE);
        List<CrateItemEntry> entryList = crateEntry.getItems();

        for (CrateItemEntry itemEntry : entryList) {
            if (inv.firstEmpty() == -1) {
                player.sendMessage(ChatMessage.color(Storage.getMessage("crate-preview-out-of-array")));
                player.openInventory(inv);
                return;
            }

            ItemStack bukkitItem = itemEntry.getDisplayItem();
            if (itemEntry.shouldShowChance()) {
                bukkitItem = ItemBuilder.fromItem(bukkitItem)
                        .appendLore(Collections.singletonList(ChatMessage.color("§e§lChance: §a§l" + itemEntry.getChance() + "%")))
                        .build();
            }
            inv.addItem(bukkitItem);
        }

        int emptySlots = invSize - entryList.size();
        for (int i = 0; i < emptySlots; i++) {
            inv.setItem(inv.firstEmpty(), new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§a").build());
        }

        player.openInventory(inv);
    }

    private static int getInvSize(int length) {
        int rows = (length + 8) / 9;
        return Math.min(rows, 6) * 9;
    }
}
