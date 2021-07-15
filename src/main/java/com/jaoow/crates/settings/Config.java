package com.jaoow.crates.settings;

import com.jaoow.crates.Crates;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private static final FileConfiguration CONFIG = Crates.getInstance().getConfig();

    public static int MAX_CRATES_PER_DAY = CONFIG.getInt("settings.crates.max-per-day");
    public static int CRATE_OPEN_DELAY = CONFIG.getInt("settings.crates.open-delay");
    public static int CRATE_MAX_REWARDS = CONFIG.getInt("settings.crates.max-rewards");

}
