package com.jaoow.crates.commands.admin;

import com.google.common.collect.Lists;
import com.jaoow.crates.Crates;
import com.jaoow.crates.constants.Constants;
import com.jaoow.crates.inventory.ConfirmInventory;
import com.jaoow.crates.model.crate.Crate;
import com.jaoow.crates.model.inventory.ItemBuilder;
import com.jaoow.crates.repository.CrateRepository;
import com.jaoow.crates.settings.Messages;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateCommand implements SubCommand {

    private final CrateRepository crateRepository = Crates.getInstance().getCrateRepository();

    @SneakyThrows
    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.COMMAND_PLAYER_ONLY.string);
            return;
        }

        if (!sender.hasPermission(Constants.CRATE_ADMIN_PERMISSION)) {
            sender.sendMessage(Messages.COMMAND_NO_PERMISSION.string);
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(Messages.COMMAND_INVALID_SYNTAX.string.replace("{usage}", (name() + " " + usage())));
            return;
        }

        Player player = (Player) sender;
        String identifier = args[0];

        if (crateRepository.contains(identifier)) {
            player.sendMessage(Messages.CREATE_ALREADY_EXISTS.string);
            return;
        }

        ItemStack itemStack = player.getItemInHand().clone();
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            player.sendMessage(Messages.CREATE_INVALID_ITEM.string);
            return;
        }

        // Remove item in hand;
        player.setItemInHand(new ItemStack(Material.AIR));

        ConfirmInventory.builder().icon(ItemBuilder.of(itemStack.getType(), "&aCreate this crate?"))
                .onConfirm(unused -> {
                    crateRepository.add(new Crate(identifier, itemStack, Lists.newArrayList()));
                    player.sendMessage(Messages.CREATE_ON_CONFIRM.string);
                })
                .onDecline(unused -> {
                    player.setItemInHand(itemStack);
                    player.sendMessage(Messages.CREATE_ON_CANCEL.string);
                })
                .build().open(player);
    }

    @Override
    public String name() {
        return "create";
    }

    @Override
    public String info() {
        return "Create a crate";
    }

    @Override
    public String usage() {
        return "<name>";
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }
}