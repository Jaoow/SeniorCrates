package com.jaoow.crates.model.inventory;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final ItemStack item;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public static ItemStack of(Material material, int durability, String name, String... lore) {
        return new ItemBuilder(material).setDurability((short) durability).setName(name).setLore(lore).build();
    }

    public static ItemStack of(Material material, String name, String... lore) {
        return new ItemBuilder(material).setName(name).setLore(lore).build();
    }

    public static ItemStack of(Material material, String name, List<String> lore) {
        return new ItemBuilder(material).setName(name).setLore(lore).build();
    }

    public static ItemBuilder of(ItemStack item) {
        return new ItemBuilder(item);
    }


    public String searchLore(String string) {
        return item.getItemMeta().getLore().stream().filter(index -> index.contains(string)).findFirst().orElse(null);
    }

    public int searchLoreIndex(String string) {
        return item.getItemMeta().getLore().indexOf(searchLore(string));
    }

    public ItemBuilder setName(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorized(name));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder appendName(String name) {
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) {
            meta.setDisplayName(StringUtils.capitalize(item.getType().name()));
        }
        meta.setDisplayName(meta.getDisplayName() + colorized(name));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta meta = item.getItemMeta();
        List<String> list = Arrays.stream(lore).map(this::colorized).collect(Collectors.toList());

        meta.setLore(list);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        List<String> list = lore.stream().map(this::colorized).collect(Collectors.toList());

        meta.setLore(list);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder appendLore(String lore) {
        ItemMeta meta = item.getItemMeta();
         List<String> list = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        list.add(colorized(lore));

        meta.setLore(list);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder appendLore(String... lore) {
        ItemMeta meta = item.getItemMeta();
        List<String> list = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        list.addAll(Arrays.stream(lore).map(this::colorized).collect(Collectors.toList()));

        meta.setLore(list);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder appendLore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        List<String> list = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        list.addAll(lore.stream().map(this::colorized).collect(Collectors.toList()));

        meta.setLore(list);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder editLore(int index, String string) {
        final ItemMeta meta = item.getItemMeta();
        final List<String> list = meta.getLore();
        list.set(index, string);
        meta.setLore(list);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        item.setDurability(durability);
        return this;
    }

    public ItemBuilder setOwner(String owner) {
        if (!(item.getItemMeta() instanceof SkullMeta)) return this;
        final SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(owner);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        final ItemMeta meta = item.getItemMeta();
        meta.spigot().setUnbreakable(unbreakable);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        final ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(flags);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantments, int level) {
        final ItemMeta meta = item.getItemMeta();
        meta.addEnchant(enchantments, level, true);
        item.setItemMeta(meta);
        return this;
    }


    public ItemStack build() {
        return this.item;
    }

    private String colorized(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
