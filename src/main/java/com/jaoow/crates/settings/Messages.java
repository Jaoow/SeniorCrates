package com.jaoow.crates.settings;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public enum Messages {


    COMMAND_PLAYER_NOT_FOUND("Command.Player-Not-Found"),
    COMMAND_PLAYER_ONLY("Command.Player-Only"),
    COMMAND_NO_PERMISSION("Command.No-Permission"),
    COMMAND_INVALID_SYNTAX("Command.Invalid-Syntax"),

    OPEN_ON_DELAY("Open.On-delay"),
    OPEN_MAX_BY_DAY("Open.Max-by-day"),
    OPEN_ON_QUIT("Open.On-quit"),
    OPEN_ON_CANCEL("Open.On-cancel"),
    OPEN_ON_FINISH("Open.On-finish"),

    CREATE_ALREADY_EXISTS("Create.Already-exist"),
    CREATE_INVALID_ITEM("Create.Invalid-item"),
    CREATE_ON_CONFIRM("Create.On-confirm"),
    CREATE_ON_CANCEL("Create.On-cancel"),

    DELETE_ON_CONFIRM("Delete.On-confirm"),
    DELETE_ON_CANCEL("Delete.On-cancel"),

    EDIT_CREATE_NOT_FOUND("Edit.Crate-not-found"),
    EDIT_CHANCE_PROMPT("Edit.Chance-prompt"),
    EDIT_INVALID_CHANCE("Edit.Invalid-chance"),
    EDIT_ON_CHANGE("Edit.On-change"),

    EDIT_REWARD_ALREADY_EXIST("Edit.Rewards.Already-exist"),
    EDIT_REWARD_REACH_MAX("Edit.Rewards.Reach-max"),
    EDIT_REWARD_ADDED("Edit.Rewards.Added"),
    EDIT_REWARD_REMOVE("Edit.Rewards.Remove"),

    GIVE_CREATE_NOT_FOUND("Give.Crate-not-found"),
    GIVE_INVALID_AMOUNT("Give.Invalid-amount"),
    GIVE_SENDER("Give.Sender"),
    GIVE_TARGET("Give.Target");

    private final String path;
    public String string;

    Messages(String path) {
        this.path = path;
    }

    @SuppressWarnings("unchecked")
    private static String toString(Object obj) {
        List<String> list = (List<String>) obj;
        return list.stream().map(s -> s + " \n").collect(Collectors.joining());
    }

    public static void loadAll(FileConfiguration file) {
        for (Messages message : Messages.values()) {
            Object object = file.get("Messages." + message.path);
            if (object instanceof List<?>) {
                message.setString(toString(object));
            } else if (object instanceof String) {
                message.setString(object.toString());
            } else if (object == null) {
                message.setString("");
            }
        }
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = ChatColor.translateAlternateColorCodes('&', string);
    }

    public String getString(String[] replacement, String[] args) {
        return StringUtils.replaceEach(string, replacement, args);
    }
}
