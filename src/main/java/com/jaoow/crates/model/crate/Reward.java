package com.jaoow.crates.model.crate;

import com.jaoow.crates.model.inventory.ItemBuilder;
import com.jaoow.crates.utils.nbt.NBTItem;
import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Data
public class Reward {

    private final ItemStack item;
    private double chance = 0.0;

    public Reward(ItemStack itemStack) {
        this.item = itemStack.clone();
    }

    public String getItemName() {

        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }

        return NBTItem.getItemName(item);
    }

    public ItemStack getInfoItem() {
        ItemBuilder builder = ItemBuilder.of(item.clone());
        builder.appendLore(
                "",
                "&7You can change",
                "&7this reward.",
                "",
                "&aChance: &f" + chance + "%",
                "",
                "&7Left Button: &fEdit the chance.",
                "&7Left Button + SHIFT: &fRemove the block",
                "");

        return builder.build();
    }
}
