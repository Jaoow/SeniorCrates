package com.jaoow.crates.commands.admin;

import org.bukkit.command.CommandSender;

public interface SubCommand {

    /*
     * /command <sub-command> args[0] args[1]
     */

    void onCommand(CommandSender sender, String[] args);

    String name();

    String info();

    String usage();

    String[] aliases();

}
