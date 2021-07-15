package com.jaoow.crates.database.sql;

import com.jaoow.crates.Crates;
import com.jaoow.crates.database.Database;
import org.bukkit.configuration.ConfigurationSection;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLDatabase extends Database {

    private final DataSource source;
    private Connection connection;

    public SQLDatabase(ConfigurationSection section) {

        String folder = Crates.getInstance().getDataFolder() + File.separator + "database";
        File file = new File(folder, section.getString("file"));
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        SQLiteConfig config = new SQLiteConfig();
        config.setCacheSize(10000);

        SQLiteDataSource source = new SQLiteDataSource();
        source.setConfig(config);
        source.setUrl("jdbc:sqlite:" + file);

        this.source = source;
    }

    @Override
    public void tryConnection() {
        try {
            this.connection = source.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
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
            this.tryConnection();
        }
        return this.connection;
    }
}


