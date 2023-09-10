package de.beta.spicrate.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Configuration {

    private YamlConfiguration configuration;
    private File file;

    public Configuration(File file) {
        this.file = file;
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    public String getString(String key) {
        return configuration.getString(key);
    }

    public int getInt(String key) {
        return configuration.getInt(key);
    }

    public boolean getBoolean(String key) {
        return configuration.getBoolean(key);
    }

    public List<Map<?, ?>> getMapList(String key) {
        return configuration.getMapList(key);
    }

    public void set(String key, Object obj) {
        configuration.set(key, obj);
    }

    public void save(boolean throwError) {
        try {
            configuration.save(file);
        } catch (Exception exe) {
            if (throwError) exe.printStackTrace();
        }
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (Exception ignore) {}
    }

}
