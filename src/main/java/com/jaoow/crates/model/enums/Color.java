package com.jaoow.crates.model.enums;

import lombok.Getter;
import org.bukkit.ChatColor;


public enum Color {

    BLACK(0),
    RED(1),
    GREEN(2),
    BROWN(3),
    BLUE(4),
    PURPLE(5),
    CYAN(6),
//    LIGHT_GRAY(7),
//    GRAY(8),
    PINK(9),
    LIME(10),
    YELLOW(11),
    LIGHT_BLUE(12),
    MAGENTA(13),
    ORANGE(14),
    WHITE(15);


    @Getter
    private final short data;

    Color(final int data) {
        this.data = (short) data;
    }

    public short toGlass() {
        return (short) (15 - this.data);
    }

    public ChatColor toChatColor() {
        switch (this.data) {
            case 0:
                return ChatColor.BLACK;
            case 1:
                return ChatColor.RED;
            case 2:
                return ChatColor.DARK_GREEN;
            case 3:
            case 14:
                return ChatColor.GOLD;
            case 4:
                return ChatColor.BLUE;
            case 5:
                return ChatColor.DARK_PURPLE;
            case 6:
            case 12:
                return ChatColor.AQUA;
            case 7:
                return ChatColor.GRAY;
            case 8:
                return ChatColor.DARK_GRAY;
            case 9:
                return ChatColor.LIGHT_PURPLE;
            case 10:
                return ChatColor.GREEN;
            case 11:
                return ChatColor.YELLOW;
            case 13:
                return ChatColor.DARK_PURPLE;
            default:
                return ChatColor.WHITE;
        }
    }
}
