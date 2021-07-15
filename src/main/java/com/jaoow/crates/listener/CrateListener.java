package com.jaoow.crates.listener;

import com.jaoow.crates.Crates;
import com.jaoow.crates.constants.Constants;
import com.jaoow.crates.controller.UserDao;
import com.jaoow.crates.inventory.ConfirmInventory;
import com.jaoow.crates.inventory.OpeningAnimatedInventory;
import com.jaoow.crates.model.crate.Reward;
import com.jaoow.crates.repository.CrateRepository;
import com.jaoow.crates.settings.Config;
import com.jaoow.crates.settings.Messages;
import com.jaoow.crates.utils.nbt.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class CrateListener implements Listener {

    private final UserDao userDao;
    private final CrateRepository crateRepository;

    public CrateListener(Crates plugin) {
        this.userDao = plugin.getUserDao();
        this.crateRepository = plugin.getCrateRepository();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getItem() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        NBTItem item = NBTItem.from(event.getItem());
        if (!item.hasTag(Constants.CRATE_NBT_TAG)) return;

        String crateId = item.getString(Constants.CRATE_NBT_TAG);
        crateRepository.findById(crateId).ifPresent(crate -> {

            Player player = event.getPlayer();
            event.setCancelled(true);

            userDao.getUser(player.getUniqueId()).ifPresent(crateUser -> {

                if (crateUser.isDelayed() && !player.hasPermission(Constants.DELAY_BYPASS_PERMISSION)) {
                    player.sendMessage(Messages.OPEN_ON_DELAY.string);
                    return;
                }

                if (crateUser.reachedLimit() && !player.hasPermission(Constants.LIMIT_BYPASS_PERMISSION)) {
                    player.sendMessage(Messages.OPEN_MAX_BY_DAY.string);
                    return;
                }

                ConfirmInventory.builder().icon(crate.getIcon()).onConfirm(unused -> {

                    ItemStack itemStack = player.getItemInHand();

                    if (itemStack.getAmount() > 1) {
                        itemStack.setAmount(itemStack.getAmount() - 1);
                    } else {
                        player.setItemInHand(new ItemStack(Material.AIR));
                    }

                    crateUser.setDelayTime(TimeUnit.SECONDS.toMillis(Config.CRATE_OPEN_DELAY));
                    crateUser.open();

                    new OpeningAnimatedInventory<>(crate::randomize, Reward::getItem,
                            none -> player.sendMessage(Messages.OPEN_ON_QUIT.string),
                            reward -> {
                                for (ItemStack drop : player.getInventory().addItem(reward.getItem()).values()) {
                                    player.getWorld().dropItem(player.getLocation(), drop);
                                }
                                player.sendMessage(Messages.OPEN_ON_FINISH.getString(
                                        new String[]{"{item_name}", "{item_chance}"},
                                        new String[]{reward.getItemName(), reward.getChance() + ""}));
                            }).open(player);

                }).onDecline(unused -> player.sendMessage(Messages.OPEN_ON_CANCEL.string)).build().open(player);
            });
        });
    }
}
