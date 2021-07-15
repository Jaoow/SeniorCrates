package com.jaoow.crates;

import com.jaoow.crates.commands.CommandManager;
import com.jaoow.crates.commands.admin.CreateCommand;
import com.jaoow.crates.commands.admin.DeleteCommand;
import com.jaoow.crates.commands.admin.EditCommand;
import com.jaoow.crates.commands.admin.GiveCommand;
import com.jaoow.crates.constants.Constants;
import com.jaoow.crates.controller.UserDao;
import com.jaoow.crates.database.Database;
import com.jaoow.crates.database.data.UserData;
import com.jaoow.crates.database.data.mysql.UserDataSQL;
import com.jaoow.crates.database.sql.MySQLDatabase;
import com.jaoow.crates.database.sql.SQLDatabase;
import com.jaoow.crates.listener.CrateListener;
import com.jaoow.crates.listener.PlayerListener;
import com.jaoow.crates.model.CrateUser;
import com.jaoow.crates.model.inventory.InventoryGUI;
import com.jaoow.crates.repository.CrateRepository;
import com.jaoow.crates.settings.Messages;
import com.jaoow.crates.utils.ChatInteraction;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public final class Crates extends JavaPlugin {

    public @Getter
    static Crates instance;

    private @Getter
    static ChatInteraction chatInteraction;

    // Repository field
    private @Getter
    CrateRepository crateRepository;

    // Database fields
    private @Getter
    Database datable;

    private @Getter
    UserData<CrateUser> userData;

    private @Getter
    UserDao userDao;

    @Override
    public void onEnable() {
        // Plugin startup logic;
        instance = this;
        saveDefaultConfig();

        // Init ChatInteraction;
        chatInteraction = new ChatInteraction(this);

        // Init MySQL;
        getLogger().info("Connecting with database.");
        try {

            ConfigurationSection section = getConfig().getConfigurationSection("connection");
            if (section.getBoolean("mysql.enabled")) {
                this.datable = new MySQLDatabase(section.getConfigurationSection("mysql"));
            } else {
                this.datable = new SQLDatabase(section.getConfigurationSection("sqlite"));
            }

            this.datable.tryConnection();
        } catch (Exception ex) {
            getLogger().severe("Couldn't connect to the database: " + ex.getCause());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Init MYSQL things;
        this.userData = new UserDataSQL(datable);
        this.userDao = new UserDao();
        this.userDao.loadAll(userData);

        // Repository;
        crateRepository = new CrateRepository();
        crateRepository.loadAll();

        // Load messages;
        Messages.loadAll(this.getConfig());

        // Load listeners;
        new CrateListener(this);
        new PlayerListener(this);
        new InventoryGUI.InventoryListener(this);

        // Load command;
        new CommandManager(this, "crates", ((sender, subCommands) -> {

            if (!sender.hasPermission(Constants.CRATE_ADMIN_PERMISSION)) {
                sender.sendMessage(Messages.COMMAND_NO_PERMISSION.string);
                return;
            }

            sender.sendMessage("");
            sender.sendMessage("§b[Crates] §7Admin command list: ");
            sender.sendMessage("");
            subCommands.stream().map(command -> "  §7/crates §8" + command.name() + " " + command.usage() + " §7- " + command.info()).forEach(sender::sendMessage);
            sender.sendMessage("");

        })).register(CreateCommand.class, DeleteCommand.class, EditCommand.class, GiveCommand.class);
    }

    @Override
    public void onDisable() {

        // Save all crates;
        crateRepository.saveAll();

        // Save all users;
        userDao.getUsers().forEach(userData::updateUser);

    }
}
