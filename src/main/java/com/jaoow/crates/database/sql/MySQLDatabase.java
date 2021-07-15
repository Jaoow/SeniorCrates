package com.jaoow.crates.database.sql;

import com.jaoow.crates.Crates;
import com.jaoow.crates.database.Database;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.logging.Logger;

public class MySQLDatabase extends Database {

    private final String address;
    private final String username;
    private final String password;

    private final String databaseName;
    private Connection connection;

    public MySQLDatabase(ConfigurationSection section) {

        this.address = section.getString("address");
        this.username =  section.getString("username");
        this.password =  section.getString("password");
        this.databaseName =  section.getString("database");

        // Init Driver
        Logger logger = Crates.getInstance().getLogger();
        try {
            // Added support for new version of the MYSQL Driver.
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("Using the new MySQL driver (post 1.16)");
        } catch (ClassNotFoundException e) {
            // Initialize old Driver in case don't find the new driver.
            try {
                Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
                logger.info("Using the default MySQL Driver (pre 1.16)");
            } catch (Exception exception) {
                logger.severe("No MySQL driver could be found.");
            }
        }
    }

    @Override
    public void tryConnection() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:mysql://" + this.address, this.username, this.password);
        if (this.connection != null) {
            this.connection.createStatement().executeUpdate("CREATE SCHEMA IF NOT EXISTS `" + this.databaseName + "` DEFAULT CHARACTER SET utf8mb4;");
            this.connection.createStatement().executeQuery("USE `" + this.databaseName + "`;");
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (!this.getConnection().isClosed()) {
                this.getConnection().close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        try {
            if (this.connection != null && this.connection.isClosed()) {
                this.tryConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (this.connection == null) {
            try {
                this.tryConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this.connection;
    }
}

