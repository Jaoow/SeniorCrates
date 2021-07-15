package com.jaoow.crates.database.data;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserData<T> {

    T createUser(Player var1);

    CompletableFuture<T> loadUser(UUID var1);

    void updateUser(T var1);
}

