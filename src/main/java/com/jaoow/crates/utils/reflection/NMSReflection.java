package com.jaoow.crates.utils.reflection;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public final class NMSReflection {

    // Reflection cache
    private static final String NMS_PACKAGE_NAME;
    private static final String CB_PACKAGE_NAME = String.format("org.bukkit.craftbukkit.%s", getPackageVersion());

    private static String PACKAGE_VERSION;
    private static Integer PACKAGE_VERSION_INT;

    static {
        if (isOldPackageStructure()) {
            NMS_PACKAGE_NAME =  String.format("net.minecraft.server.%s", getPackageVersion());
        } else {
            NMS_PACKAGE_NAME = "net.minecraft";
        }
    }

    public static boolean isOldPackageStructure() {
        return getPackageVersionInt() < 17;
    }

    public static String getPackageVersion() {
        if (PACKAGE_VERSION == null) {
            PACKAGE_VERSION = Bukkit.getServer().getClass()
                    .getPackage()
                    .getName()
                    .split("\\.")[3];
        }

        return PACKAGE_VERSION;
    }

    public static int getPackageVersionInt() {
        if (PACKAGE_VERSION_INT == null) {
            Pattern pattern = Pattern.compile("[v]?(1_([0-9])+)");
            Matcher matcher = pattern.matcher(getPackageVersion());

            if (!matcher.find()) {
                throw new IllegalStateException(String.format("failed to match package version from '%s'", getPackageVersion()));
            }

            PACKAGE_VERSION_INT = Integer.parseInt(matcher.group(2));
        }

        return PACKAGE_VERSION_INT;
    }

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName(String.format(NMS_PACKAGE_NAME + ".%s", name));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> getCraftClass(String name) {
        try {
            return Class.forName(String.format(CB_PACKAGE_NAME + ".%s", name));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}