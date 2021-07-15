package com.jaoow.crates.database.data.mysql;

import com.google.gson.Gson;
import com.jaoow.crates.utils.gson.StaticGson;
import com.jaoow.crates.model.CrateUser;
import com.jaoow.crates.database.Database;
import com.jaoow.crates.database.data.UserData;
import lombok.val;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UserDataSQL implements UserData<CrateUser> {

    private static final Supplier<Gson> GSON = StaticGson::getGson;

    private final Database database;

    public UserDataSQL(Database database) {
        this.database = database;
        try {
            this.database.tryConnection();
            this.database.executeUpdate("CREATE TABLE IF NOT EXISTS `crates_users` (`uniqueId` VARCHAR(64), `data` TEXT NOT NULL, PRIMARY KEY (`uniqueId`));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<CrateUser> loadUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = this.database.getConnection().prepareStatement("SELECT * FROM `crates_users` WHERE `uniqueId` = ?")) {

                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return GSON.get().fromJson(resultSet.getString(2), CrateUser.class);
                }

                return null;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public CrateUser createUser(Player player) {
        val user = new CrateUser(player.getName(), player.getUniqueId());
        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = this.database.getConnection().prepareStatement("INSERT INTO `crates_users`(`uniqueId`, `data`) VALUES (?, ?);")) {
                statement.setString(1, user.getUniqueId().toString());
                statement.setString(2, GSON.get().toJson(user));
                statement.execute();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return user;
    }

    @Override
    public void updateUser(CrateUser CrateUser) {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("UPDATE `crates_users` SET `data`= ? WHERE `uniqueId`= ?;")) {
            statement.setString(1, GSON.get().toJson(CrateUser));
            statement.setString(2, CrateUser.getUniqueId().toString());
            statement.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

