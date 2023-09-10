package de.beta.spicrate.entry;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class StoreEntry {

    private ItemStack displayItem;
    private int chance;
    private boolean showChance;
    private boolean announce;
    private List<String> commands;

    public StoreEntry(ItemStack displayItem, int chance, boolean showChance, boolean announce, List<String> commands) {
        this.displayItem = displayItem;
        this.chance = chance;
        this.showChance = showChance;
        this.announce = announce;
        this.commands = commands;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public int getChance() {
        return chance;
    }

    public boolean isShowChance() {
        return showChance;
    }

    public boolean isAnnounce() {
        return announce;
    }

    public List<String> getCommands() {
        return commands;
    }

}
