package com.jaoow.crates.commands;


import com.jaoow.crates.commands.admin.SubCommand;
import com.jaoow.crates.settings.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

public class CommandManager implements CommandExecutor {

    private final JavaPlugin main;
    private final Set<SubCommand> commands = new HashSet<>();

    private final BiConsumer<CommandSender, Set<SubCommand>> defaultCommand;

    public CommandManager(JavaPlugin main, String command, BiConsumer<CommandSender, Set<SubCommand>> defaultCommand) {
        this.main = main;
        this.defaultCommand = defaultCommand;

        main.getCommand(command).setExecutor(this);
    }

    public CommandManager(JavaPlugin main, String command) {
        this.main = main;
        this.defaultCommand = null;

        main.getCommand(command).setExecutor(this);
    }


    private SubCommand get(String name) {
        for (SubCommand subCommand : this.commands) {
            if (subCommand.name().equalsIgnoreCase(name)) {
                return subCommand;
            }

            String[] aliases = subCommand.aliases();
            for (String alias : aliases) {
                if (name.equalsIgnoreCase(alias)) {
                    return subCommand;
                }
            }
        }
        return null;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            if(this.defaultCommand != null) this.defaultCommand.accept(sender, this.commands);
            else sender.sendMessage("§cInvalid syntax. Use /" + command.getName() + " <subcommand>");
            return true;
        }

        SubCommand subCommand = get(args[0]);
        if (subCommand == null) {
            sender.sendMessage("§cSubcommand not found!");
            return true;
        }

        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        try {
            Objects.requireNonNull(subCommand).onCommand(sender, newArgs);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("§cAn unexpected error occurred!");
        }
        return false;
    }

    public void register(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            register(clazz);
        }
    }

    private void register(Class<?> classes) {
        try {
            if (SubCommand.class.isAssignableFrom(classes) && !classes.equals(SubCommand.class)) {
                SubCommand subCommand = (SubCommand) classes.newInstance();
                this.commands.add(subCommand);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
