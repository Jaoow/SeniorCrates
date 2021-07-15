package com.jaoow.crates.database;

import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public abstract class Database {

    public abstract void tryConnection() throws SQLException;

    public abstract Connection getConnection();

    public abstract void closeConnection();

    public ResultSet executeQuery(String query) {
        try (PreparedStatement statement = this.getConnection().prepareStatement(query)) {
            return statement.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean executeUpdate(String update) {
        try (PreparedStatement statement = this.getConnection().prepareStatement(update)) {
            statement.execute();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}

