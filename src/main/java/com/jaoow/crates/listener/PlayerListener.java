package com.jaoow.crates.listener;

import com.jaoow.crates.Crates;
import com.jaoow.crates.controller.UserDao;
import com.jaoow.crates.database.data.UserData;
import com.jaoow.crates.model.CrateUser;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final UserDao userDao;
    private final UserData<CrateUser> userData;

    public PlayerListener(Crates plugin) {
        this.userData = plugin.getUserData();
        this.userDao = plugin.getUserDao();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoinPlayer(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        userData.loadUser(player.getUniqueId()).whenCompleteAsync((member, throwable) -> {

            if (member == null) {
                val newMember = userData.createUser(player);
                userDao.loadUser(newMember);
                return;
            }

            userDao.loadUser(member);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        val uniqueId = event.getPlayer().getUniqueId();
        userDao.getUser(uniqueId).ifPresent(member -> {
            userData.updateUser(member);
            userDao.unloadUser(uniqueId);
        });
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        val uniqueId = event.getPlayer().getUniqueId();
        userDao.getUser(uniqueId).ifPresent(member -> {
            userData.updateUser(member);
            userDao.unloadUser(uniqueId);
        });
    }
}

