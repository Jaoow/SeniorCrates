package com.jaoow.crates.inventory;

import com.jaoow.crates.Crates;
import com.jaoow.crates.model.crate.Crate;
import com.jaoow.crates.model.crate.Reward;
import com.jaoow.crates.model.inventory.InventoryGUI;
import com.jaoow.crates.repository.CrateRepository;
import com.jaoow.crates.settings.Config;
import com.jaoow.crates.settings.Messages;
import com.jaoow.crates.utils.ChatInteraction;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.IntStream;

public class RewardInventory implements InventoryGUI.InventoryProvider {

    private static final ChatInteraction CHAT_INTERACTION = Crates.getChatInteraction();
    private static final CrateRepository crateRepository = Crates.getInstance().getCrateRepository();

    private final Crate crate;
    private final InventoryGUI inventory;

    public RewardInventory(Crate crate) {
        this.crate = crate;
        this.inventory = InventoryGUI.builder()
                .name("Editing crate: " + StringUtils.capitalize(crate.getIdentifier()))
                .size(InventoryGUI.InventorySize.round(Config.CRATE_MAX_REWARDS))
                .defaultAction((inventory, event) -> {
                    Player player = (Player) event.getWhoClicked();
                    if (event.getClickedInventory().equals(player.getInventory())) {

                        ItemStack itemStack = event.getCurrentItem();
                        if (itemStack == null || itemStack.getType() == Material.AIR) return;

                        if (crate.contains(itemStack)) {
                            player.sendMessage(Messages.EDIT_REWARD_ALREADY_EXIST.string);
                            return;
                        }

                        if (crate.reachedMaxRewards()) {
                            player.sendMessage(Messages.EDIT_REWARD_REACH_MAX.string);
                            return;
                        }

                        // Add reward
                        crate.addReward(new Reward(itemStack));

                        // Save crate
                        crateRepository.save(crate);

                        // Rebuild inventory
                        inventory.rebuild(player);
                        player.sendMessage(Messages.EDIT_REWARD_ADDED.string);
                    }
                }).provider(this).build();
    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void open(Player player) {
        inventory.open(player);
    }

    @Override
    public void initialize(Player player, InventoryGUI builder) {

        List<Reward> rewards = crate.getRewards();

        IntStream.range(0, rewards.size()).forEach(value -> {

            Reward reward = rewards.get(value);
            builder.appendItem(value, InventoryGUI.ClickableItem.of(reward.getInfoItem(), event -> {

                ClickType click = event.getClick();

                if (click.isShiftClick()) {

                    // Remove reward from crate;
                    crate.removeReward(reward);

                    builder.rebuild(player);
                    player.sendMessage(Messages.EDIT_REWARD_REMOVE.string);
                    return;
                }

                if (click.isLeftClick()) {
                    CHAT_INTERACTION.of(player, Messages.EDIT_CHANCE_PROMPT.string, chat -> {
                        double chance = isDouble(chat.getMessage()) ? Double.parseDouble(chat.getMessage()) : -1;

                        if (chance <= 0 || chance > 100) {
                            player.sendMessage(Messages.EDIT_INVALID_CHANCE.string);
                            return;
                        }

                        reward.setChance(chance);
                        player.sendMessage(Messages.EDIT_ON_CHANGE.string.replace("{chance}", chance + ""));
                    });
                }
            }));
        });
    }
}
