package de.beta.spicrate.util;

import de.beta.spicrate.SpiCrate;
import de.beta.spicrate.config.Storage;
import de.beta.spicrate.database.DatabaseConnection;
import de.beta.spicrate.entry.CrateEntry;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CrateManager extends DatabaseConnection {

    private Map<UUID, Map<String, Integer>> chestCache = new HashMap<>();

    public CrateManager(Connection connection, String database, String table) {
        super(connection, database, table);
        prepareStatement( "CREATE TABLE IF NOT EXISTS _database._table (`uuid` varchar(36) NOT NULL,`defaultCrate` int(11) NOT NULL DEFAULT '0')" );
        String lastChest = "";
        for (CrateEntry crateEntry : Storage.getCrates()) {
            try {
                alterTable(crateEntry.getName(), lastChest);
            } catch (Exception ignore) {
            }
            lastChest = crateEntry.getName();
        }
    }

    /*
    |                       |
    |   Player Management   |
    |                       |
    */

    public void addCrate(UUID uuid, CrateEntry crateEntry, int amount) {
        loadChestAmount(uuid, crateEntry.getName(), integer -> {
            setCrate(uuid, crateEntry, integer + amount);
        });
    }

    public void removeCrate(UUID uuid, CrateEntry crateEntry, int amount) {
        loadChestAmount(uuid, crateEntry.getName(), integer -> {
            setCrate(uuid, crateEntry, integer - amount);
        });
    }

    public void setCrate(UUID uuid, CrateEntry crateEntry, int amount) {
        runAsynchronously(() -> {
            updateStatement("UPDATE _database._table SET " + crateEntry.getName() + "=? WHERE uuid = ?", amount, uuid.toString());
            chestCache.getOrDefault(uuid, new HashMap<>()).put(crateEntry.getName(), amount);
        });
    }

    public HashMap<CrateEntry, Integer> getAmount(UUID uuid) {
        HashMap<CrateEntry, Integer> map = new HashMap<>();
        return map;
    }

    public int getAmount(UUID uuid, CrateEntry crateEntry) {
        AtomicInteger amount = new AtomicInteger();
        loadChestAmount(uuid, crateEntry.getName(), val -> {

        });
        return amount.get();
    }

    public void createPlayerIfNotExists(UUID uuid) {
        if (!hasNext("SELECT uuid FROM _database._table WHERE uuid = ?", uuid.toString())) {
            for (CrateEntry crateEntry : Storage.getCrates()) {
                updateStatement("INSERT INTO _database._table(`uuid`, `" + crateEntry.getName() + "`) VALUES (?, ?)", uuid.toString(), 0);
            }
        }
    }

    public void loadChestAmount(UUID uuid, String name, Callback<Integer> callback) {
        if (chestCache.containsKey(uuid)) {
            if (chestCache.get(uuid).containsKey(name)) {
                callback.onDone(chestCache.get(uuid).get(name));
                return;
            }
        }
        runAsynchronously(() -> {
            final ResultSet resultSet = prepareStatement("SELECT " + name + " FROM _database._table WHERE uuid = ?", uuid.toString());
            try {
                if ( resultSet.next() ) {
                    final int anInt = resultSet.getInt(name);
                    callback.onDone(anInt);
                    final Map<String, Integer> orDefault = chestCache.getOrDefault(uuid, new HashMap<>());
                    orDefault.put(name, anInt);
                } else {
                    callback.onDone(0);
                }
            } catch (SQLException exe) {
                exe.printStackTrace();
            }
        });
    }

    @Deprecated
    public int getAbstractAmount(UUID uuid, CrateEntry crateEntry) {
        final ResultSet resultSet = prepareStatement("SELECT " + crateEntry.getName() + " FROM _database._table WHERE uuid = ?", uuid.toString());
        try {
            if ( resultSet.next() ) {
                final int anInt = resultSet.getInt(crateEntry.getName());
                final Map<String, Integer> orDefault = chestCache.getOrDefault(uuid, new HashMap<>());
                orDefault.put(crateEntry.getName(), anInt);
                return anInt;
            }
        } catch (SQLException exe) {
            exe.printStackTrace();
        }
        return 0;
    }

    /*
    |                      |
    |   Crate Management   |
    |                      |
    */

    public CrateEntry getCrateByName(String crate) {
        List<CrateEntry> crates = Storage.getCrates();
        for (CrateEntry entry : crates) {
            if (entry.getName().equals(crate)) return entry;
        }
        return null;
    }

    public boolean doesCrateExist(CrateEntry crateEntry) {
        List<CrateEntry> crates = Storage.getCrates();
        for (CrateEntry entry : crates) {
            if (entry.equals(crateEntry)) return true;
        }
        return false;
    }

    public boolean doesCrateExist(String crate) {
        List<CrateEntry> crates = Storage.getCrates();
        for (CrateEntry entry : crates) {
            if (entry.getName().equals(crate)) return true;
        }
        return false;
    }

}
