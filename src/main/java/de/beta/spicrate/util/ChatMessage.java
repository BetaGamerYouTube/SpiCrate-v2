package de.beta.spicrate.util;

import de.beta.spicrate.config.Storage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatMessage {

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String strip(String text) {
        return ChatColor.stripColor(text);
    }

    public static String colorByChar(char c) {
        return ChatColor.getByChar(c).toString();
    }

    public static String repl(String key, String value, String text) {
        return text.replace(key, value);
    }

    public static String append(String text, String... appends) {
        StringBuilder builder = new StringBuilder(text);
        for (String append : appends) {
            builder.append(append);
        }
        return builder.toString();
    }

    public static void sendColoredMessage(CommandSender sender, String id) {
        String msg = color(Storage.getMessage(id));
        sender.sendMessage(msg);
    }

    public static void sendColoredMessage(Player player, String id) {
        String msg = color(Storage.getMessage(id));
        player.sendMessage(msg);
    }

    public static void sendColoredMessage(String id) {
        String msg = color(Storage.getMessage(id));
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    public static void sendColoredMessage(String id, String key1, String val1) {
        String msg = color(Storage.getMessage(id));
        msg = msg.replace(key1, val1);
        Bukkit.getConsoleSender().sendMessage(msg);
    }

}
