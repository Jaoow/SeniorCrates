package com.jaoow.crates.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jaoow.crates.Crates;
import com.jaoow.crates.utils.gson.StaticGson;
import com.jaoow.crates.model.crate.Crate;
import com.jaoow.crates.model.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CrateRepository {

    private final Map<String, Crate> crateMap = Maps.newConcurrentMap();
    private final File folder = new File(Crates.getInstance().getDataFolder() + "/crates");

    public List<Crate> getCrates() {
        return Lists.newArrayList(crateMap.values());
    }

    public Optional<Crate> findById(String identifier) {
        return Optional.ofNullable(crateMap.get(identifier));
    }

    public boolean contains(String identifier) {
        return crateMap.containsKey(identifier);
    }

    public void add(Crate crate) {
        save(crate);
    }

    public void delete(Crate crate) {
        crateMap.remove(crate.getIdentifier());
        Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .filter(file -> file.getName().equals(crate.getIdentifier() + ".yml")).forEach(File::delete);
    }

    public void save(Crate crate) {
        crateMap.put(crate.getIdentifier(), crate);
        File file = new File(folder, crate.getIdentifier() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("value", StaticGson.getGsonPretty().toJson(crate));
            config.save(file);

        } catch (IOException e) {
            Crates.getInstance().getLogger().warning("Failed to save '" + crate.getIdentifier() + "' crate.");
        }
    }

    public void saveAll() {
        crateMap.values().forEach(this::save);
    }

    public void load(String fileName) {
        File file = new File(folder, fileName);
        if (file.exists()) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                add(StaticGson.getGsonPretty().fromJson(config.getString("value"), Crate.class));
            } catch (Exception e) {
                Crates.getInstance().getLogger().warning("Failed to load '" + fileName + "' crate.");
            }
        }
    }

    public void loadAll() {

        // create folder
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return;
            }
        }

        Arrays.stream(Objects.requireNonNull(folder.listFiles())).map(File::getName).forEach(this::load);
    }
}
