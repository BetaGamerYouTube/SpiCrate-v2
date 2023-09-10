package de.beta.spicrate.inv.edit.settings;

import de.beta.spicrate.SpiCrate;
import de.beta.spicrate.config.Storage;
import de.beta.spicrate.entry.CrateEntry;
import de.beta.spicrate.entry.CrateItemEntry;
import de.beta.spicrate.entry.StoreEntry;
import de.beta.spicrate.util.ChatMessage;
import de.beta.spicrate.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class SettingsEditInventory implements Listener {

    private Inventory inv;
    private CrateEntry crateEntry;
    private CrateItemEntry itemEntry;
    private Player player;
    private int configId;
    private int newChance;
    private boolean newAnnounce;
    private boolean newShowChance;

    public SettingsEditInventory(Player player, CrateEntry crateEntry, CrateItemEntry itemEntry, int configId) {
        this.crateEntry = crateEntry;
        this.player = player;
        this.itemEntry = itemEntry;
        this.configId = configId;
        this.inv = Bukkit.createInventory(null, 54, "§aCrate-Item Settings");
        Bukkit.getPluginManager().registerEvents(this, SpiCrate.getInstance());
        newChance = itemEntry.getChance();
        newShowChance = itemEntry.shouldShowChance();
        newAnnounce = itemEntry.shouldAnnounce();
        prepareInv();
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (!event.getInventory().equals(inv)) return;
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;
        if (!clickedItem.hasItemMeta()) return;
        if (!clickedItem.getItemMeta().hasLocalizedName()) return;
        String localizedName = clickedItem.getItemMeta().getLocalizedName();
        if (localizedName.equalsIgnoreCase(ItemIdentifier.AnnounceItem.toString())) {
            newAnnounce = reverseBool(newAnnounce);
        } else if (localizedName.equalsIgnoreCase(ItemIdentifier.ChanceItem.toString())) {
            newShowChance = reverseBool(newShowChance);
        } else if (localizedName.equalsIgnoreCase(ItemIdentifier.Minus1.toString())) {
            if ((newChance - 1) < 1) return;
            newChance -= 1;
        } else if (localizedName.equalsIgnoreCase(ItemIdentifier.Minus10.toString())) {
            if ((newChance - 10) < 1) return;
            newChance -= 10;
        } else if (localizedName.equalsIgnoreCase(ItemIdentifier.Add1.toString())) {
            if ((newChance + 1) > 10000) return;
            newChance += 1;
        } else if (localizedName.equalsIgnoreCase(ItemIdentifier.Add10.toString())) {
            if ((newChance + 10) > 10000) return;
            newChance += 10;
        }
        sendInventoryUpdate();
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inv)) return;
        StoreEntry storeEntry = new StoreEntry(itemEntry.getDisplayItem(), newChance, newShowChance, newAnnounce, itemEntry.getCommands());
        storeItem(storeEntry);
        HandlerList.unregisterAll(this);
        event.getPlayer().sendMessage(ChatMessage.color(Storage.getMessage("crate-edit-successfully-saved")));
    }

    private void prepareInv() {
        while (inv.firstEmpty() != -1) {
            inv.setItem(inv.firstEmpty(), new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§a").build());
        }
        inv.setItem(13, itemEntry.getDisplayItem());
        inv.setItem(19, makeAnnounceInfoItem());
        inv.setItem(25, makeShowChanceInfoItem());
        inv.setItem(40, makeChanceInfoItem());
        inv.setItem(39, finalMinusItem());
        inv.setItem(38, finalMinusItem10());
        inv.setItem(41, finalPlusItem());
        inv.setItem(42, finalPlusItem10());
    }

    private void sendInventoryUpdate() {
        inv.setItem(19, makeAnnounceInfoItem());
        inv.setItem(25, makeShowChanceInfoItem());
        inv.setItem(40, makeChanceInfoItem());
        inv.setItem(39, finalMinusItem());
        inv.setItem(38, finalMinusItem10());
        inv.setItem(41, finalPlusItem());
        inv.setItem(42, finalPlusItem10());
        player.updateInventory();
    }

    private void storeItem(StoreEntry storeEntry) {
        YamlConfiguration conf = SpiCrate.getInstance().newYaml("crates.yml");
        conf.set(crateEntry.getName() + ".items." + configId + ".displayItem", storeEntry.getDisplayItem());
        conf.set(crateEntry.getName() + ".items." + configId + ".announce", storeEntry.isAnnounce());
        conf.set(crateEntry.getName() + ".items." + configId + ".chance", storeEntry.getChance());
        conf.set(crateEntry.getName() + ".items." + configId + ".showChance", storeEntry.isShowChance());
        try {
            conf.save(new File(SpiCrate.getInstance().getDataFolder(), "crates.yml"));
        } catch (Exception ignore) {}
    }

    private enum ItemIdentifier {
        AnnounceItem("should_announce"),
        ChanceItem("show_chance"),
        Add1("plus1"),
        Add10("plus10"),
        Minus1("minus1"),
        Minus10("minus10");

        private final String localizedName;

        ItemIdentifier(String localizedName) {
            this.localizedName = localizedName;
        }

        @Override
        public String toString() {
            return localizedName;
        }
    }

    private ItemStack makeChanceInfoItem() {
        return new ItemBuilder(Material.KNOWLEDGE_BOOK)
                .setDisplayName(ChatMessage.color("&d&lChance: " + newChance))
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .enchant(Enchantment.DAMAGE_ALL, 2)
                .build();
    }

    private ItemStack makeAnnounceInfoItem() {
        return new ItemBuilder(Material.MAP)
                .setDisplayName(ChatMessage.color("&d&lAnnounce?: " + colorBool(newAnnounce)))
                .setLocalizedName("should_announce")
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .enchant(Enchantment.DAMAGE_ALL, 2)
                .build();
    }

    private ItemStack makeShowChanceInfoItem() {
        return new ItemBuilder(Material.MAP)
                .setDisplayName(ChatMessage.color("&d&lShow Chance?: " + colorBool(newShowChance)))
                .setLocalizedName("show_chance")
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .enchant(Enchantment.DAMAGE_ALL, 2)
                .build();
    }

    private ItemStack finalPlusItem() {
        return new ItemBuilder(Material.GREEN_CONCRETE_POWDER)
                .setDisplayName(ChatMessage.color("&a&l+ 1%"))
                .setLocalizedName("plus1")
                .build();
    }

    private ItemStack finalMinusItem() {
        return new ItemBuilder(Material.RED_CONCRETE_POWDER)
                .setDisplayName(ChatMessage.color("&c&l- 1%"))
                .setLocalizedName("minus1")
                .build();
    }

    private ItemStack finalPlusItem10() {
        return new ItemBuilder(Material.GREEN_TERRACOTTA)
                .setDisplayName(ChatMessage.color("&a&l+ 10%"))
                .setLocalizedName("plus10")
                .build();
    }

    private ItemStack finalMinusItem10() {
        return new ItemBuilder(Material.REDSTONE_BLOCK)
                .setDisplayName(ChatMessage.color("&c&l- 10%"))
                .setLocalizedName("minus10")
                .build();
    }

    private String colorBool(boolean bool) {
        String color = "&c";
        if (bool) color = "&a";
        return ChatMessage.color(color + bool);
    }

    private boolean reverseBool(boolean bool) {
        return !bool;
    }

}
