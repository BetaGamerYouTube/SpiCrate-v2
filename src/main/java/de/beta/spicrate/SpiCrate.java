package de.beta.spicrate;

import de.beta.spicrate.cmd.CrateCommand;
import de.beta.spicrate.config.Configuration;
import de.beta.spicrate.config.Storage;
import de.beta.spicrate.database.ConnectionClient;
import de.beta.spicrate.database.DatabaseConnectionHolder;
import de.beta.spicrate.listener.ConnectionHandler;
import de.beta.spicrate.listener.BlockInteractHandler;
import de.beta.spicrate.listener.InventoryClickHandler;
import de.beta.spicrate.util.ChatMessage;
import de.beta.spicrate.util.CrateManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public final class SpiCrate extends JavaPlugin {

    private static SpiCrate instance;
    private static String LANGUAGE;
    private ConnectionClient connectorClient;
    private CrateManager crateManager;
    private Configuration configuration;

    @Override
    public void onEnable() {
        instance = this;
        startUp();
        regListeners();
        regCommands();
    }

    @Override
    public void onDisable() {
        closeSQLClientConnection();
    }

    public static SpiCrate getInstance() {
        return instance;
    }

    private void loadConfigFiles() {
        String[] resourceFiles = {"config.yml", "crates.yml", "mysql.yml"};
        for (String fileName : resourceFiles) {
            File file = new File(getDataFolder(), fileName);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                saveResource(fileName, false);
            }
        }

        File languageDir = new File(getDataFolder(), "languages/");
        if (!languageDir.exists()) {
            languageDir.getParentFile().mkdirs();
            saveResource("languages/de-DE.yml", false);
            saveResource("languages/en-EN.yml", false);
        }
    }

    public YamlConfiguration newYaml(String name) {
        File file = new File(getDataFolder(), name);
        return YamlConfiguration.loadConfiguration(file);
    }

    private void loadDatabase() {
        try {
            connectorClient = new ConnectionClient();
            crateManager = new CrateManager(connectorClient.getMySQLInstance(), connectorClient.getDatabase(), "crates");
            DatabaseConnectionHolder.startRunnable();
            ChatMessage.sendColoredMessage("database-connect-successful");
        } catch (Exception ignore) {
            ChatMessage.sendColoredMessage("database-connect-failed");
        }
    }

    private String getLoadLanguage() {
        return YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml")).getString("Language");
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    private void closeSQLClientConnection() {
        try {
            if (connectorClient != null) {
                if (connectorClient.getMySQLInstance() != null) {
                    if(!connectorClient.getMySQLInstance().isClosed()) {
                        connectorClient.close();
                        DatabaseConnectionHolder.stopRunnable();
                        ChatMessage.sendColoredMessage("database-disconnect-successful");
                    }
                }
            }
        } catch (SQLException exe) {
            ChatMessage.sendColoredMessage("database-disconnect-failed");
            exe.printStackTrace();
        }
    }

    public void pluginReload() {
        closeSQLClientConnection();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
        }
        Storage.nullable();
        startUp();
    }

    private void regCommands() {
        getCommand("crate").setExecutor(new CrateCommand());
        getCommand("crate").setTabCompleter(new CrateCommand());
    }

    private void regListeners() {
        Bukkit.getPluginManager().registerEvents(new ConnectionHandler(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickHandler(), this);
        Bukkit.getPluginManager().registerEvents(new BlockInteractHandler(), this);
    }

    private void startUp() {
        loadConfigFiles();
        LANGUAGE = getLoadLanguage();
        Storage.load(newYaml("config.yml"), newYaml("mysql.yml"), newYaml("crates.yml"), newYaml("languages/" + LANGUAGE + ".yml"));
        configuration = new Configuration(new File(getDataFolder(), "config.yml"));
        loadDatabase();
    }
}
