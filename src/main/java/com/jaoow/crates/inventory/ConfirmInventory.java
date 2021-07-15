package com.jaoow.crates.inventory;

import com.jaoow.crates.model.inventory.InventoryGUI;
import com.jaoow.crates.model.inventory.ItemBuilder;
import lombok.Builder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

import static com.jaoow.crates.model.inventory.InventoryGUI.InventorySize.FIVE_ROWS;

@Builder
public class ConfirmInventory implements InventoryGUI.InventoryProvider {

    private final ItemStack icon;
    private final Consumer<Void> onConfirm;
    private final Consumer<Void> onDecline;

    private final InventoryGUI inventory = InventoryGUI.builder()
            .name("Want to confirm?").size(FIVE_ROWS)
            .provider(this).build();

    @Override
    public void open(Player player) {
        inventory.open(player);
    }

    @Override
    public void initialize(Player player, InventoryGUI builder) {
        builder.appendItem(13, InventoryGUI.ClickableItem.of(icon))
                .appendItem(30, InventoryGUI.ClickableItem.of(ItemBuilder.of(Material.WOOL, 5, "§aConfirm"), event -> {
                    player.closeInventory();
                    onConfirm.accept(null);
                }))
                .appendItem(32, InventoryGUI.ClickableItem.of(ItemBuilder.of(Material.WOOL, 14, "§cDecline"), event -> {
                    player.closeInventory();
                    onDecline.accept(null);
                }));

    }
}
