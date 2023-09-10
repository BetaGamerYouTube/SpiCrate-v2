package de.beta.spicrate.inv.edit;

import de.beta.spicrate.SpiCrate;
import de.beta.spicrate.config.Storage;
import de.beta.spicrate.entry.CrateEntry;
import de.beta.spicrate.entry.CrateItemEntry;
import de.beta.spicrate.entry.StoreEntry;
import de.beta.spicrate.util.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ItemEditInventory implements Listener {

    private Inventory inv;
    private CrateEntry crateEntry;
    private Player player;
    private List<StoreEntry> stores = new ArrayList<>();

    public ItemEditInventory(Player player, CrateEntry crateEntry) {
        this.crateEntry = crateEntry;
        this.player = player;
        this.inv = Bukkit.createInventory(null, 54, "§aEditing Crate...");
        Bukkit.getPluginManager().registerEvents(this, SpiCrate.getInstance());
        prepareInv();
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inv)) return;
        List<ItemStack> itemStacks = saveItems(event.getInventory());
        exec1(itemStacks);
        HandlerList.unregisterAll(this);
        event.getPlayer().sendMessage(ChatMessage.color(Storage.getMessage("crate-edit-successfully-saved")));
    }

    private void prepareInv() {
        for (CrateItemEntry crateItemEntry : crateEntry.getItems()) {
            inv.setItem(inv.firstEmpty(), crateItemEntry.asDebugItem());
        }
    }

    private List<ItemStack> saveItems(Inventory inventory) {
        List<ItemStack> sessionList = new ArrayList<>();
        Iterator<ItemStack> it = inventory.iterator();
        while (it.hasNext()) {
            ItemStack is = it.next();
            if (is == null) continue;
            if (is.getType() == Material.AIR) continue;
            sessionList.add(is);
        }
        return sessionList;
    }

    private void exec1(List<ItemStack> sessionList) {
        List<ItemStack> removal = new ArrayList<>();
        for (ItemStack is : sessionList) {
            if (!is.hasItemMeta()) continue;
            if (!is.getItemMeta().hasLore()) continue;
            List<String> lore = is.getItemMeta().getLore();
            String chance = "1";
            String showChance = "true";
            String announce = "true";
            for (String str : lore) {
                if (str.startsWith(ChatMessage.color("&c&l&nChance: "))) {
                    chance = str.replace(ChatMessage.color("&c&l&nChance: "), "");
                    continue;
                }
                if (str.startsWith(ChatMessage.color("&c&l&nShowChance: "))) {
                    showChance = str.replace(ChatMessage.color("&c&l&nShowChance: "), "");
                    continue;
                }
                if (str.startsWith(ChatMessage.color("&c&l&nAnnounce: "))) {
                    announce = str.replace(ChatMessage.color("&c&l&nAnnounce: "), "");
                    continue;
                }
            }
            boolean announceBool = Boolean.parseBoolean(announce);
            int chanceInt = Integer.parseInt(chance);
            boolean showChanceBool = Boolean.parseBoolean(showChance);
            ItemStack rIs = is;
            ItemMeta im = rIs.getItemMeta();
            List<String> lr = im.getLore();
            List<Integer> toRem = new ArrayList<>();
            for (int i = 0; i < lr.size(); i++) {
                String str = lr.get(i);
                if (str.startsWith(ChatMessage.color("&c&l&nChance: "))) {
                    toRem.add(i);
                }
                if (str.startsWith(ChatMessage.color("&c&l&nShowChance: "))) {
                    toRem.add(i);
                }
                if (str.startsWith(ChatMessage.color("&c&l&nAnnounce: "))) {
                    toRem.add(i);
                }
            }
            toRem.sort(Collections.reverseOrder());
            for (int i : toRem)
                lr.remove(i);
            im.setLore(lr);
            rIs.setItemMeta(im);
            stores.add(new StoreEntry(rIs, chanceInt, showChanceBool, announceBool, Arrays.asList("spi giveitem %player% ##id##")));
            removal.add(is);
        }
        for (ItemStack iss : removal) {
            sessionList.remove(iss);
        }
        removal.clear();
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (ItemStack is : sessionList) {
            stores.add(new StoreEntry(is, 1, true, true, Arrays.asList("spi giveitem %player% ##id##")));
            removal.add(is);
        }
        for (ItemStack iss : removal) {
            sessionList.remove(iss);
        }
        removal.clear();
        exec2();
    }

    private void exec2() {
        clearConfigEntries();
        int index = 1;
        for (StoreEntry entry : stores) {
            storeItem(entry, index);
            index++;
        }
    }

    private void storeItem(StoreEntry storeEntry, int storeAs) {
        YamlConfiguration conf = SpiCrate.getInstance().newYaml("crates.yml");
        conf.set(crateEntry.getName() + ".items." + storeAs + ".displayItem", storeEntry.getDisplayItem());
        conf.set(crateEntry.getName() + ".items." + storeAs + ".announce", storeEntry.isAnnounce());
        conf.set(crateEntry.getName() + ".items." + storeAs + ".chance", storeEntry.getChance());
        conf.set(crateEntry.getName() + ".items." + storeAs + ".showChance", storeEntry.isShowChance());
        conf.set(crateEntry.getName() + ".items." + storeAs + ".commands", storeEntry.getCommands());
        try {
            conf.save(new File(SpiCrate.getInstance().getDataFolder(), "crates.yml"));
        } catch (Exception ignore) {}
    }

    private void clearConfigEntries() {
        YamlConfiguration conf = SpiCrate.getInstance().newYaml("crates.yml");
        conf.set(crateEntry.getName() + ".items", null);
        try {
            conf.save(new File(SpiCrate.getInstance().getDataFolder(), "crates.yml"));
        } catch (Exception ignore) {}
    }

}
