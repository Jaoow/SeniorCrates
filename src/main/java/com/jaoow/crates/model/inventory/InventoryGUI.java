package com.jaoow.crates.model.inventory;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * class to facilitate the construction of inventories.
 *
 * @author Jaoow
 * @version 1.0.0
 */
@Getter
@Builder
public class InventoryGUI implements InventoryHolder {

    private final String name;
    private final InventoryGUI.InventorySize size;

    private final InventoryProvider provider;
    private final Map<Integer, ClickableItem> itemMap = Maps.newHashMap();

    /**
     * Action to be execute if player click on non-mapped item
     */
    private final BiConsumer<InventoryGUI, InventoryClickEvent> defaultAction;

    /**
     * Inventory (may be null if not built)
     */
    private Inventory inventory;


    /**
     * Set the item of inventory
     *
     * @param slot the slot
     * @param item the clickable item
     * @return the inventory gui
     */
    public InventoryGUI appendItem(int slot, ClickableItem item) {
        itemMap.put(slot, item);
        return this;
    }

    /**
     * Get raw inventory size
     * @return the raw inventory size
     */
    public int getSize() {
        return size.getSize();
    }

    /**
     * Get clickable item by slot
     *
     * @param slot slot to search the item
     * @return the clickable item {@link ClickableItem)}
     */
    @Nullable
    public ClickableItem getBySlot(int slot) {
        return itemMap.getOrDefault(slot, null);
    }

    /**
     * Format the inventory and add the items.
     *
     * @param player player to initialize inventory.
     * @return the built inventory
     */
    public Inventory build(Player player) {

        // Create inventory.
        this.inventory = Bukkit.createInventory(this, size.getSize(), name);

        // Initialize items;
        this.provider.initialize(player, this);

        for (Map.Entry<Integer, ClickableItem> items : itemMap.entrySet()) {
            inventory.setItem(items.getKey(), items.getValue().item);
        }

        return inventory;
    }

    /**
     * Rebuild the inventory.
     * @param player player to initialize inventory
     */
    public void rebuild(Player player) {
        Validate.notNull(inventory, "inventory cannot be null");

        // Clear all contents from inventory.
        inventory.clear();
        inventory.setContents(new ItemStack[0]);

        // Clear all items from mapping.
        itemMap.clear();

        // Re-initialize inventory;
        provider.initialize(player, this);

        for (Map.Entry<Integer, ClickableItem> items : itemMap.entrySet()) {
            inventory.setItem(items.getKey(), items.getValue().item);
        }
    }

    /**
     * Open inventory to player.
     *
     * @param player player to open inventory.
     */
    public void open(Player player) {
        player.openInventory(this.build(player));
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Consumer method to use as inventory listener
     *
     * @param event the event
     */
    private void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        if (event.getClickedInventory() == player.getOpenInventory().getTopInventory()) {
            if (event.getRawSlot() > getSize()) {
                return;
            }

            ClickableItem item = this.getBySlot(event.getRawSlot());
            if (item != null) item.run(event);
            return;
        }

        if (defaultAction != null) defaultAction.accept(this, event);
    }

    public interface InventoryProvider {

        void open(Player player);

        void initialize(Player player, InventoryGUI builder);

    }

    @Getter
    @AllArgsConstructor
    public enum InventorySize {

        ONE_ROW(9),
        TWO_ROWS(18),
        THREE_ROWS(27),
        FOUR_ROWS(36),
        FIVE_ROWS(45),
        SIX_ROWS(54);

        private final int size;

        public static InventorySize round(int items) {
            int value = Math.min(6, (int) Math.ceil(items / 9f));
            switch (value) {
                case 1: return ONE_ROW;
                case 2: return TWO_ROWS;
                case 3: return THREE_ROWS;
                case 4: return FOUR_ROWS;
                case 5: return FIVE_ROWS;
                default: return SIX_ROWS;
            }
        }
    }

    @Getter
    @AllArgsConstructor(staticName = "of")
    public static class ClickableItem {

        private final ItemStack item;
        private final Consumer<InventoryClickEvent> consumer;

        public static ClickableItem of(ItemStack item) {
            return new ClickableItem(item, event -> {
            });
        }

        public void run(InventoryClickEvent event) {
            this.consumer.accept(event);
        }
    }

    public static class InventoryListener implements Listener {

        public InventoryListener(JavaPlugin plugin) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (event.getInventory().getHolder() instanceof InventoryGUI) {
                InventoryGUI inventory = (InventoryGUI) event.getInventory().getHolder();
                inventory.onClick(event);
            }
        }
    }
}
