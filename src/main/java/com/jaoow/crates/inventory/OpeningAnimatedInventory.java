package com.jaoow.crates.inventory;

import com.jaoow.crates.Crates;
import com.jaoow.crates.model.enums.Color;
import com.jaoow.crates.model.inventory.InventoryGUI;
import com.jaoow.crates.model.inventory.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.jaoow.crates.model.inventory.InventoryGUI.InventorySize.FIVE_ROWS;

@RequiredArgsConstructor
public class OpeningAnimatedInventory<T> implements InventoryGUI.InventoryProvider {

    private static final Crates PLUGIN = Crates.getInstance();
    private static final ThreadLocalRandom R = ThreadLocalRandom.current();
    private static final Supplier<ItemStack> RANDOM_GLASS = () -> {
        Color color = Color.values()[R.nextInt(Color.values().length)];
        return ItemBuilder.of(Material.STAINED_GLASS_PANE, color.toGlass(), color.toChatColor() + "&kAAA");
    };

    private final Supplier<T> supplier;
    private final Function<T, ItemStack> function;

    private final Consumer<Void> onClose;
    private final Consumer<T> onFinish;

    private final InventoryGUI inventory = InventoryGUI.builder()
            .name("Drawing an item...").size(FIVE_ROWS)
            .provider(this).build();

    @Override
    public void open(Player player) {
        inventory.open(player);
    }

    @Override
    public void initialize(Player player, InventoryGUI builder) {

        new BukkitRunnable() {

            final Inventory inventory = builder.getInventory();
            final int inventoryCenter = (inventory.getSize() / 2);

            int currentTick = 0;

            @Override
            public void run() {

                // Checks if the has closed the inventory.
                if (!inventory.getViewers().contains(player)) {
                    // Cancel task.
                    this.cancel();

                    onClose.accept(null);
                }

                // Checks if the animation has reached the end
                if (currentTick == inventoryCenter) {

                    // Cancel task.
                    this.cancel();

                    T result = supplier.get();
                    inventory.setItem(inventoryCenter, function.apply(result));
                    Bukkit.getScheduler().runTaskLaterAsynchronously(PLUGIN, () -> {
                        onFinish.accept(result);
                        player.closeInventory();
                    }, 20L);
                    return;
                }

                int currentSlot = currentTick;
                int invertedSlot = (inventory.getSize() - 1) - currentTick;

                inventory.setItem(currentSlot, RANDOM_GLASS.get());
                inventory.setItem(invertedSlot, RANDOM_GLASS.get());

                currentTick += 1;
            }

        }.runTaskTimerAsynchronously(PLUGIN, 3L, 3L);
    }
}
