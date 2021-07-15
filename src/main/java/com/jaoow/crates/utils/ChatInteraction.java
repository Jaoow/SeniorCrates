package com.jaoow.crates.utils;

import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * @deprecated use {@link ConversationFactory} instead this;
 * @author Jaoow
 */
@Deprecated
public class ChatInteraction {
   
   private final WeakHashMap<UUID, Consumer<AsyncPlayerChatEvent>> players;
   
   public ChatInteraction(JavaPlugin plugin) {
      plugin.getServer().getPluginManager().registerEvents(new ChatListener(), plugin);
      players = new WeakHashMap<>();
   }
   
   public void of(Player player, String message, Consumer<AsyncPlayerChatEvent> consumer) {
      players.put(player.getUniqueId(), consumer);

      player.closeInventory();
      player.sendMessage(message);
   }

   public class ChatListener implements Listener {
      
      @EventHandler
      public void onChat(AsyncPlayerChatEvent event) {
         Player player = event.getPlayer();

         if (!players.containsKey(player.getUniqueId())) {
            return;
         }

         if (event.getMessage().equalsIgnoreCase("cancel")) {
            player.sendMessage("§aAction cancelled.");
            players.remove(player.getUniqueId());
            event.setCancelled(true);
            return;
         }

         event.setCancelled(true);
         players.get(player.getUniqueId()).accept(event);
         players.remove(player.getUniqueId());
      }
      
      @EventHandler
      public void onQuit(PlayerQuitEvent e) {
         players.remove(e.getPlayer().getUniqueId());
      }
   }
}
