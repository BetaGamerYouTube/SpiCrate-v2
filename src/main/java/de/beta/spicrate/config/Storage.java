package de.beta.spicrate.config;

import de.beta.spicrate.entry.CrateEntry;
import de.beta.spicrate.util.ChatMessage;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Storage {

    private static HashMap<String, Object> mainMap = new HashMap<>();
    private static List<Location> locationList = new ArrayList<>();
    private static HashMap<String, Object> language = new HashMap<>();
    private static List<CrateEntry> crateEntries = new ArrayList<>();
    private static HashMap<String, String> database = new HashMap<>();

    public static void load(YamlConfiguration config, YamlConfiguration mysql, YamlConfiguration crates, YamlConfiguration currentLanguage) {
        mainMap.put("Language", config.getString("Language"));
        mainMap.put("CrateSpinSound", config.getString("CrateSpinSound"));
        mainMap.put("CrateWinSound", config.getString("CrateWinSound"));
        mainMap.put("DiscoMode", config.getBoolean("DiscoMode"));
        mainMap.put("CrateSpeed", config.getDouble("CrateSpeed"));
        mainMap.put("CrateGeneralTime", config.getInt("CrateGeneralTime"));
        locationList = (List<Location>) config.getList("CrateLocations");
        crates.getValues(false).forEach((key, value) -> crateEntries.add(new CrateEntry(crates.getConfigurationSection(key))));
        for (String key : currentLanguage.getKeys(false)) {
            Object value = currentLanguage.get(key);
            language.put(key, value);
        }
        for (String key : mysql.getKeys(false)) {
            Object value = mysql.get(key);
            database.put(key, (String) value);
        }
    }

    public static void nullable() {
        mainMap.clear();
        locationList.clear();
        language.clear();
        crateEntries.clear();
        database.clear();
    }

    public static Object getConfigValue(String key) {
        return mainMap.get(key);
    }

    public static List<Location> getLocationList() {
        return locationList;
    }

    public static String getMessage(String key) {
        return ChatMessage.color((String) language.get(key));
    }

    public static List<String> getMessageList(String key) {
        return (List<String>) language.get(key);
    }

    public static List<CrateEntry> getCrates() {
        return crateEntries;
    }

    public static String getDatabaseVal(String key) {
        return database.get(key);
    }

}
