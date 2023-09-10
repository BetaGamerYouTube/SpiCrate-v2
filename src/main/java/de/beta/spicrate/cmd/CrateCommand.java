package de.beta.spicrate.cmd;

import de.beta.spicrate.SpiCrate;
import de.beta.spicrate.config.Storage;
import de.beta.spicrate.entry.CrateEntry;
import de.beta.spicrate.entry.CrateItemEntry;
import de.beta.spicrate.inv.edit.ItemEditInventory;
import de.beta.spicrate.inv.edit.settings.MainSettingsEditInventory;
import de.beta.spicrate.util.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class CrateCommand implements CommandExecutor, TabCompleter {

    private final int MAX_AMOUNT = 1000000; // 1,000,000

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sendUsage(sender);
            return false;
        }
        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "addcrate", "add":
                addRemoveSetCrate(sender, args, true);
                break;
            case "removecrate", "remove":
                addRemoveSetCrate(sender, args, false);
                break;
            case "setcrate", "set":
                setCrate(sender, args);
                break;
            case "getcrates", "get":
                getCrates(sender, args);
                break;
            case "edit":
                edit(sender, args);
                break;
            case "setlocation", "setloc":
                setLocation(sender);
                break;
            case "giveitem":
                giveItem(sender, args);
                break;
            case "reload", "rl":
                reloadPlugin(sender);
                break;
            case "plugin":
                sendPluginInfo(sender);
                break;
            default:
                sendUsage(sender);
                break;
        }
        return true;
    }

    private void addRemoveSetCrate(CommandSender sender, String[] args, boolean add) {
        if (args.length != 4) {
            sendUsage(sender);
            return;
        }
        String player = args[1];
        String crate = args[2];
        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-invalid-number")));
            return;
        }
        if (amount <= 0 || amount > MAX_AMOUNT) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-amount-invalid").replace("{max}", String.valueOf(MAX_AMOUNT))));
            return;
        }
        if (!SpiCrate.getInstance().getCrateManager().doesCrateExist(crate)) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-crate-not-found")));
            return;
        }
        CrateEntry crateEntry = SpiCrate.getInstance().getCrateManager().getCrateByName(crate);
        if (add) {
            SpiCrate.getInstance().getCrateManager().addCrate(Bukkit.getOfflinePlayer(player).getUniqueId(), crateEntry, amount);
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-crate-added")
                    .replace("{player}", Bukkit.getOfflinePlayer(player).getName())
                    .replace("{amount}", String.valueOf(amount))
                    .replace("{crate}", crateEntry.getName())));
        } else {
            SpiCrate.getInstance().getCrateManager().removeCrate(Bukkit.getOfflinePlayer(player).getUniqueId(), crateEntry, amount);
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-crate-removed")
                    .replace("{player}", Bukkit.getOfflinePlayer(player).getName())
                    .replace("{amount}", String.valueOf(amount))
                    .replace("{crate}", crateEntry.getName())));
        }
    }

    private void setCrate(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sendUsage(sender);
            return;
        }
        String player = args[1];
        String crate = args[2];
        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-invalid-number")));
            return;
        }
        if (amount <= 0 || amount > MAX_AMOUNT) {
            sender.sendMessage(ChatMessage.color("&cAmount can not be bigger than &e" + MAX_AMOUNT + "&c!"));
            return;
        }
        if (!SpiCrate.getInstance().getCrateManager().doesCrateExist(crate)) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-crate-not-found")));
            return;
        }
        CrateEntry crateEntry = SpiCrate.getInstance().getCrateManager().getCrateByName(crate);
        SpiCrate.getInstance().getCrateManager().setCrate(Bukkit.getOfflinePlayer(player).getUniqueId(), crateEntry, amount);
        sender.sendMessage(ChatMessage.color(Storage.getMessage("command-crate-set")
                .replace("{player}", Bukkit.getOfflinePlayer(player).getName())
                .replace("{amount}", String.valueOf(amount))
                .replace("{crate}", crateEntry.getName())));
    }

    private void getCrates(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sendUsage(sender);
            return;
        }
        String player = args[1];
        String crate = args[2];
        if (!SpiCrate.getInstance().getCrateManager().doesCrateExist(crate)) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-crate-not-found")));
            return;
        }
        CrateEntry crateEntry = SpiCrate.getInstance().getCrateManager().getCrateByName(crate);
        UUID uuid = Bukkit.getOfflinePlayer(player).getUniqueId();
        int amount = SpiCrate.getInstance().getCrateManager().getAmount(uuid, crateEntry);
        sender.sendMessage(ChatMessage.color(Storage.getMessage("command-crate-info")
                .replace("{player}", Bukkit.getOfflinePlayer(player).getName())
                .replace("{amount}", String.valueOf(amount))
                .replace("{crate}", crateEntry.getName())));
    }

    private void edit(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sendUsage(sender);
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-only-player-edit")));
            return;
        }
        String crate = args[1];
        String type = args[2].toLowerCase();
        if (!SpiCrate.getInstance().getCrateManager().doesCrateExist(crate)) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-crate-not-found")));
            return;
        }
        CrateEntry crateEntry = SpiCrate.getInstance().getCrateManager().getCrateByName(crate);
        if (type.equals("items")) {
            new ItemEditInventory((Player) sender, crateEntry);
        } else if (type.equals("settings")) {
            new MainSettingsEditInventory((Player) sender, crateEntry);
        } else {
            sendUsage(sender);
        }
    }

    private void setLocation(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-only-player-setlocation")));
            return;
        }
        Location location = ((Player) sender).getLocation();
        List<Location> locationList = Storage.getLocationList();
        locationList.add(location);
        SpiCrate.getInstance().getConfiguration().set("CrateLocations", locationList);
        SpiCrate.getInstance().getConfiguration().save(true);
        sender.sendMessage(ChatMessage.color(Storage.getMessage("command-location-added")));
    }

    private void giveItem(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sendUsage(sender);
            return;
        }
        String crate = args[1];
        String player = args[2];
        String id = args[3];
        Player targetPlayer = Bukkit.getPlayerExact(player);
        if (targetPlayer == null) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-player-not-found")));
            return;
        }
        if (!SpiCrate.getInstance().getCrateManager().doesCrateExist(crate)) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-crate-not-found")));
            return;
        }
        int intId;
        try {
            intId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-invalid-number-id")));
            return;
        }
        CrateEntry crateEntry = SpiCrate.getInstance().getCrateManager().getCrateByName(crate);
        CrateItemEntry crateItemEntry = crateEntry.getItems().stream()
                .filter(item -> item.getConfigId() == intId)
                .findFirst()
                .orElse(null);
        if (crateItemEntry == null) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-item-id-not-found")));
            return;
        }
        int firstEmptySlot = targetPlayer.getInventory().firstEmpty();
        if (firstEmptySlot == -1) {
            sender.sendMessage(ChatMessage.color(Storage.getMessage("command-player-inventory-full")));
            return;
        }
        targetPlayer.getInventory().addItem(crateItemEntry.getDisplayItem());
        sender.sendMessage(ChatMessage.color(Storage.getMessage("command-giveitem")));
    }

    private void reloadPlugin(CommandSender sender) {
        SpiCrate.getInstance().pluginReload();
        sender.sendMessage(ChatMessage.color(Storage.getMessage("command-plugin-reloaded")));
    }

    private void sendPluginInfo(CommandSender sender) {
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &7PluginName: &e" + SpiCrate.getInstance().getDescription().getName()));
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &7Version: &e" + SpiCrate.getInstance().getDescription().getVersion()));
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &7API-Version: &e" + SpiCrate.getInstance().getDescription().getAPIVersion()));
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &7Author: &e" + String.join(", ", SpiCrate.getInstance().getDescription().getAuthors())));
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &7Main: &e" + SpiCrate.getInstance().getDescription().getMain()));
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &a/spi addcrate <Player> <Crate> <Amount>"));
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &a/spi removecrate <Player> <Crate> <Amount>"));
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &a/spi setcrate <Player> <Crate> <Amount>"));
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &a/spi getcrates <Player> <Crate>"));
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &a/spi edit <Crate> <Items/Settings>"));
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &a/spi giveitem <Crate> <Player> <ConfigId>"));
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &a/spi setlocation"));
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &a/spi reload"));
        sender.sendMessage(ChatMessage.color(" &7● &6Spi&eCrates &8| &a/spi plugin"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (!sender.hasPermission("spicrate.admin")) {
            return Collections.singletonList("No-Permission");
        }

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList("addcrate", "removecrate", "setcrate", "getcrates", "edit", "setlocation", "giveitem", "reload", "plugin"), completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("edit")) {
                Storage.getCrates().forEach(c -> commands.add(c.getName()));
                StringUtil.copyPartialMatches(args[1], commands, completions);
            } else if (Arrays.asList("addcrate", "removecrate", "setcrate", "getcrates").contains(args[0].toLowerCase())) {
                Bukkit.getOnlinePlayers().forEach(p -> commands.add(p.getName()));
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
        } else if (args.length == 3) {
            if (Arrays.asList("addcrate", "removecrate", "setcrate", "getcrates").contains(args[0].toLowerCase())) {
                Storage.getCrates().forEach(c -> commands.add(c.getName()));
                StringUtil.copyPartialMatches(args[2], commands, completions);
            } else if (args[0].equalsIgnoreCase("edit")) {
                StringUtil.copyPartialMatches(args[2], Arrays.asList("items", "settings"), completions);
            }
        }

        Collections.sort(completions);
        return completions;
    }
}
