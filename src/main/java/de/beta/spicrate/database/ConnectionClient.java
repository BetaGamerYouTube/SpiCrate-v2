package de.beta.spicrate.database;

import de.beta.spicrate.config.Storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClient {

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private Connection mySQLInstance = null;

    public ConnectionClient() {
        this.host = Storage.getDatabaseVal("Hostname");
        this.port = Storage.getDatabaseVal("Port");
        this.database = Storage.getDatabaseVal("Database");
        this.username = Storage.getDatabaseVal("Username");
        this.password = Storage.getDatabaseVal("Password");
    }

    public Connection getMySQLInstance() {
        try {
            if ((this.mySQLInstance == null) || (this.mySQLInstance.isClosed())) {
                this.mySQLInstance = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);
            }
        } catch (SQLException exe) {
            exe.printStackTrace();
        }
        return this.mySQLInstance;
    }

    public void close() {
        try {
            if ((this.mySQLInstance != null) || (!this.mySQLInstance.isClosed())) {
                this.mySQLInstance.close();
            }
        } catch (SQLException exe) {
            exe.printStackTrace();
        }
    }

    public String getDatabase() {
        return this.database;
    }

}
