package com.jaoow.crates.commands.admin;

import com.jaoow.crates.Crates;
import com.jaoow.crates.constants.Constants;
import com.jaoow.crates.inventory.RewardInventory;
import com.jaoow.crates.repository.CrateRepository;
import com.jaoow.crates.settings.Messages;
import lombok.SneakyThrows;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditCommand implements SubCommand {

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

        if (!crateRepository.contains(identifier)) {
            player.sendMessage(Messages.EDIT_CREATE_NOT_FOUND.string);
            return;
        }

        crateRepository.findById(identifier).ifPresent(crate -> new RewardInventory(crate).open(player));
    }

    @Override
    public String name() {
        return "edit";
    }

    @Override
    public String info() {
        return "Edit crate rewards";
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