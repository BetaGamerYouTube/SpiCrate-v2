package de.beta.spicrate.database;

import de.beta.spicrate.SpiCrate;
import de.beta.spicrate.config.Storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class DatabaseConnection {

    private String database;
    private Connection connection = null;
    private String table;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public DatabaseConnection(Connection connection) {
        this.connection = connection;
    }

    public DatabaseConnection(Connection connection, String database) {
        this(connection);
        this.database = database;
    }

    public DatabaseConnection(Connection connection, String database, String table) {
        this(connection);
        this.database = database;
        this.table = table;
    }

    public ResultSet prepareStatement(String sql, Object... parameters) {
        sql = replaceGlobalVariables(sql);

        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            if (parameters.length > 0) {
                IntStream.rangeClosed(1, parameters.length).forEach(i -> {
                    try {
                        statement.setObject(i, parameters[i - 1]);
                    } catch (SQLException exe) {
                        exe.printStackTrace();
                    }
                });
            }
            statement.execute();
            return statement.getResultSet();
        } catch (SQLException exe) {
            SpiCrate.getInstance().getLogger().severe(Storage.getMessage("database-statement-failed").replace("{statement}", sql));
            exe.printStackTrace();
        }
        return null;
    }

    public void updateStatement(String sql, Object... parameters) {
        sql = replaceGlobalVariables(sql);

        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            if ( parameters.length > 0 ) {
                IntStream.rangeClosed(1, parameters.length).forEach(i -> {
                    try {
                        statement.setObject(i, parameters[i - 1]);
                    } catch (SQLException exe) {
                        exe.printStackTrace();
                    }
                });
            }
            statement.executeUpdate();
        } catch (SQLException exe) {
            SpiCrate.getInstance().getLogger().severe(Storage.getMessage("database-statement-failed").replace("{statement}", sql));
            exe.printStackTrace();
        }
    }

    public void alterTable(String name, String after) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("ALTER TABLE `" + table + "` ADD `" + name + "` INT NOT NULL DEFAULT '0'");
        statement.executeUpdate();
    }

    public boolean hasNext(String sql, Object... parameters) {
        sql = replaceGlobalVariables(sql);
        try {
            return this.prepareStatement(sql, parameters).next();
        } catch (SQLException exe) {
            exe.printStackTrace();
        }
        return false;
    }

    public void runAsynchronously(Runnable runnable) {
        executorService.execute(runnable);
    }

    private String replaceGlobalVariables(String s) {
        return s.replaceAll("_database", this.database).replaceAll("_table", this.table);
    }

}
