package com.jaoow.crates.utils.nbt;

import org.bukkit.inventory.ItemStack;

public class NBTItem extends NBT {

    private NBTItem(ItemStack item) {
        super(item.clone());
    }

    public static NBTItem from(ItemStack item) {
        return new NBTItem(item);
    }

    @Override
    public ItemStack build() {
        return super.build();
    }
}
