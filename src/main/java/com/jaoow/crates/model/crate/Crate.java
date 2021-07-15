package com.jaoow.crates.model.crate;

import com.jaoow.crates.constants.Constants;
import com.jaoow.crates.settings.Config;
import com.jaoow.crates.utils.RandomCollection;
import com.jaoow.crates.utils.nbt.NBTItem;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class Crate {

    private final String identifier;
    private final ItemStack icon;
    private final List<Reward> rewards;

    public boolean contains(ItemStack itemStack) {
        return rewards.stream().anyMatch(reward -> reward.getItem().isSimilar(itemStack));
    }

    public void removeReward(Reward reward) {
        rewards.remove(reward);
    }

    public void addReward(Reward reward) {
        rewards.add(reward);
    }

    public boolean reachedMaxRewards() {
        return rewards.size() >= Config.CRATE_MAX_REWARDS;
    }

    public Reward randomize() {
        List<Reward> rewards = this.getRewards();
        RandomCollection<Reward> collection = new RandomCollection<>();

        for (Reward reward : rewards) {
            double chance = reward.getChance();
            collection.add(chance, reward);
        }

        return collection.random();
    }

    public ItemStack toItem() {
        return NBTItem.from(icon).setString(Constants.CRATE_NBT_TAG, getIdentifier()).build();
    }
}
