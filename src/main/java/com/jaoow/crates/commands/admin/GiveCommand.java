package com.jaoow.crates.commands.admin;

import com.jaoow.crates.Crates;
import com.jaoow.crates.constants.Constants;
import com.jaoow.crates.inventory.RewardInventory;
import com.jaoow.crates.repository.CrateRepository;
import com.jaoow.crates.settings.Messages;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCommand implements SubCommand {

    private final CrateRepository crateRepository = Crates.getInstance().getCrateRepository();

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SneakyThrows
    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Constants.CRATE_ADMIN_PERMISSION)) {
            sender.sendMessage(Messages.COMMAND_NO_PERMISSION.string);
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Messages.COMMAND_INVALID_SYNTAX.string.replace("{usage}", (name() + " " + usage())));
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(Messages.COMMAND_PLAYER_NOT_FOUND.string);
            return;
        }

        String identifier = args[1];

        if (!crateRepository.contains(identifier)) {
            sender.sendMessage(Messages.GIVE_CREATE_NOT_FOUND.string);
            return;
        }

        int amount = 1;
        if (args.length >= 3) {
            amount = isInteger(args[2]) ? Integer.parseInt(args[2]) : -1;
            if (amount == -1) {
                sender.sendMessage(Messages.GIVE_INVALID_AMOUNT.string);
                return;
            }
        }

        int finalAmount = amount;
        crateRepository.findById(identifier).ifPresent(crate -> {
            for (int i = 0; i < finalAmount; i++) {
                targetPlayer.getInventory().addItem(crate.toItem());
            }
        });
    }

    @Override
    public String name() {
        return "give";
    }

    @Override
    public String info() {
        return "Send a crate to player";
    }

    @Override
    public String usage() {
        return "<player> <name> [amount]";
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }
}