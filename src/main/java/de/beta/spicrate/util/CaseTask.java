package de.beta.spicrate.util;

import de.beta.spicrate.SpiCrate;
import de.beta.spicrate.config.Storage;
import de.beta.spicrate.entry.CrateItemEntry;
import de.beta.spicrate.spin.ChestInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CaseTask extends BukkitRunnable {

    private int index = 0;
    private final ChestInventory inventory;
    private final CrateItemEntry winItem;
    private int itemId = 0;
    private final int indexMax = (int) Storage.getConfigValue("CrateGeneralTime");
    private final Sound chestSound = Sound.valueOf((String) Storage.getConfigValue("CrateSpinSound"));
    private final Sound winSound = Sound.valueOf((String) Storage.getConfigValue("CrateWinSound"));
    private final List<CrateItemEntry> itemList = new ArrayList<>();
    private final boolean discoMode = (boolean) Storage.getConfigValue("DiscoMode");
    private final List<ItemBuilder> builders = new ArrayList<>();

    public CaseTask(ChestInventory inventory, CrateItemEntry winItem) {
        this.inventory = inventory;
        this.winItem = winItem;
        inventory.getPlayer().setCanPickupItems(false);
        initializeItemList();
        initializeBuilders();
    }

    private void initializeItemList() {
        int i = 0;
        int a = 0;
        while (i <= indexMax) {
            i++;
            final List<CrateItemEntry> items = inventory.getCrateEntry().getItems();
            if (a >= items.size()) a = 0;
            itemList.add(items.get(a++));
        }
    }

    private void initializeBuilders() {
        builders.add(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.BROWN_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.MAGENTA_STAINED_GLASS_PANE).setDisplayName("§a"));
        builders.add(new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§a"));
    }

    @Override
    public void run() {
        if (discoMode) setColoredGlass();
        else setBlackGlass();
        final Player player = inventory.getPlayer();
        for (int i = 0; i < 9; i++) {
            if (itemId >= this.itemList.size()) itemId = 0;
            inventory.getInventory().setItem(9 + i, this.itemList.get(itemId).getDisplayItem());
            itemId++;
            if (indexMax - index == 4)
                inventory.getInventory().setItem(17, this.winItem.getDisplayItem());
            else if (indexMax - index == 3)
                inventory.getInventory().setItem(16, this.winItem.getDisplayItem());
            else if (indexMax - index == 2)
                inventory.getInventory().setItem(15, this.winItem.getDisplayItem());
            else if (indexMax - index == 1)
                inventory.getInventory().setItem(14, this.winItem.getDisplayItem());
            else if (indexMax - index == 0)
                inventory.getInventory().setItem(13, this.winItem.getDisplayItem());

            player.playSound(player.getLocation(), chestSound, 0.5f, 1f);
        }
        if (index >= indexMax) {
            this.cancel();
            player.playSound(player.getLocation(), winSound, 1, 1);
            player.getWorld().spawn(player.getLocation(), Firework.class);
            handleWinAnnouncement(player);
            player.getInventory().addItem(winItem.getDisplayItem());
            Bukkit.getScheduler().runTaskLater(SpiCrate.getInstance(), () -> {
                player.closeInventory();
                inventory.getPlayer().setCanPickupItems(true);
            }, 30);
        }
        index++;
    }

    private void setColoredGlass() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < inventory.getInventory().getSize(); i++) {
            if (i == 4) continue;
            inventory.getInventory().setItem(i, builders.get(random.nextInt(0, builders.size())).build());
        }
    }

    private void setBlackGlass() {
        for (int i = 0; i < inventory.getInventory().getSize(); i++) {
            if (i == 4) continue;
            inventory.getInventory().setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§a").build());
        }
    }

    private void handleWinAnnouncement(Player player) {
        if (winItem.shouldAnnounce()) {
            String itemName = winItem.getDisplayItem().getItemMeta().getDisplayName();
            if (!winItem.getDisplayItem().getItemMeta().hasDisplayName())
                itemName = winItem.getDisplayItem().getType().toString();
            List<String> msg = Storage.getMessageList("crate-spin-win-announce");
            for (String str : msg) {
                str = ChatMessage.color(str).replace("{player}", player.getName()).replace("{item}", itemName).replace("{crate}", inventory.getCrateEntry().getDisplayName());
                if (str.startsWith("{$center-text}")) str = center(str.replace("{$center-text}", ""));
                Bukkit.broadcastMessage(str);
            }
        }
    }

    private String center(String str) {
        if(str == null || str.equals("")) return str;
        str = ChatMessage.color(str);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : str.toCharArray()){
            if(c == '§'){
                previousCode = true;
                continue;
            }else if(previousCode == true){
                previousCode = false;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                }else isBold = false;
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int CENTER_PX = 154;

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString() + str;
    }

}
