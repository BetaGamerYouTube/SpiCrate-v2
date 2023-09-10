package de.beta.spicrate.entry;

import de.beta.spicrate.config.Storage;
import de.beta.spicrate.util.ChatMessage;
import de.beta.spicrate.util.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CrateEntry {

    private List<CrateItemEntry> items = new ArrayList<>();
    private String name;
    private String displayName;
    private ItemStack displayItem;
    private int slot;

    public CrateEntry (ConfigurationSection section) {
        name = section.getName();
        section.getValues(false).forEach((key, value) -> {
            switch (key) {
                case "displayName":
                    this.displayName = ChatMessage.color((String) value);
                    break;
                case "displayItem":
                    this.displayItem = (ItemStack) value;
                    break;
                case "slot":
                    this.slot = (int) value;
                    break;
                case "items":
                    section.getConfigurationSection(key).getValues(false).forEach((subKey, subValue) -> {
                        items.add(new CrateItemEntry(section.getConfigurationSection(key + "." + subKey), Integer.parseInt(subKey)));
                    });
                    break;
            }
        });
        this.displayItem = ItemBuilder.fromItem(displayItem).setDisplayName(displayName).setLocalizedName(name).build();
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public int getSlot() {
        return slot;
    }

    public List<CrateItemEntry> getItems() {
        return items;
    }

    public ItemStack toItemStack(int amount) {
        return ItemBuilder.fromItem(displayItem)
                .setDisplayName(ChatMessage.color(displayName)).setLore(ChatMessage.color(Storage.getMessage("crates-overview").replace("{amount}", String.valueOf(amount)))).setLocalizedName(name).build();
    }
}
