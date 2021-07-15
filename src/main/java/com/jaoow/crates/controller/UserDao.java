package com.jaoow.crates.controller;

import com.jaoow.crates.database.data.UserData;
import com.jaoow.crates.model.CrateUser;
import lombok.val;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UserDao {

    private final Map<UUID, CrateUser> memberMap = new ConcurrentHashMap<>();

    public List<CrateUser> getUsers() {
        return new ArrayList<>(memberMap.values());
    }

    public void loadUser(CrateUser socialUser) {
        this.memberMap.putIfAbsent(socialUser.getUniqueId(), socialUser);
    }

    public Optional<CrateUser> getUser(UUID uniqueId) {
        return Optional.ofNullable(this.memberMap.get(uniqueId));
    }

    public CrateUser getOrCreate(UUID uniqueId) {
        return getUser(uniqueId).orElseGet(() -> {
            String playerName = Bukkit.getOfflinePlayer(uniqueId).getName();
            return this.memberMap.put(uniqueId, new CrateUser(playerName, uniqueId));
        });
    }

    public Optional<CrateUser> unloadUser(UUID uniqueId) {
        return Optional.ofNullable(this.memberMap.remove(uniqueId));
    }

    public void loadAll(UserData<CrateUser> userData) {
        CompletableFuture.runAsync(() -> Bukkit.getOnlinePlayers().forEach(player -> {

            // Unload and update old player;
            unloadUser(player.getUniqueId()).ifPresent(userData::updateUser);

            val loadUser = userData.loadUser(player.getUniqueId());
            loadUser.whenCompleteAsync((user, throwable) -> {

                if (user == null) {
                    val newUser = userData.createUser(player);
                    loadUser(newUser);
                    return;
                }

                loadUser(user);
            });
        }));
    }
}

