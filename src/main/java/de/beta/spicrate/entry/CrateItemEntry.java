package de.beta.spicrate.entry;

import de.beta.spicrate.util.ChatMessage;
import de.beta.spicrate.util.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class CrateItemEntry {

    private ItemStack displayItem;
    private boolean announce;
    private boolean showChance;
    private int chance;
    private List<String> commands;
    private int configId;

    public CrateItemEntry(ConfigurationSection section, int configId) {
        section.getValues(true).forEach((key, value) -> {
            switch (key) {
                case "displayItem":
                    this.displayItem = (ItemStack) value;
                    break;
                case "announce":
                    this.announce = (boolean) value;
                    break;
                case "showChance":
                    this.showChance = (boolean) value;
                    break;
                case "chance":
                    this.chance = (int) value;
                    break;
                case "commands":
                    this.commands = (List<String>) value;
                    break;
            }
        });
        this.configId = configId;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public ItemStack asDebugItem() {
        return ItemBuilder
                .fromItem(getDisplayItem())
                .appendLore(Collections.singletonList(ChatMessage.color("&c&l&nChance: " + chance)))
                .appendLore(Collections.singletonList(ChatMessage.color("&c&l&nShowChance: " + showChance)))
                .appendLore(Collections.singletonList(ChatMessage.color("&c&l&nAnnounce: " + announce)))
                .build();
    }

    public boolean shouldAnnounce() {
        return announce;
    }

    public boolean shouldShowChance() {
        return showChance;
    }

    public int getChance() {
        return chance;
    }

    public List<String> getCommands() {
        return commands;
    }

    public int getConfigId() {
        return configId;
    }

}
