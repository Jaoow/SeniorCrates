package com.jaoow.crates.utils.nbt;

import com.jaoow.crates.utils.reflection.NMSReflection;
import lombok.SneakyThrows;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;


public abstract class NBT {

    private static final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    private static Class<?> NMS_ITEM_STACK;
    private static Class<?> NBT_TAG_COMPOUND;

    private static Method AS_NMS_COPY;
    private static Method AS_CRAFT_MIRROR;
    private static Method NBT_SET_TAG;
    private static Method NBT_HAS_TAG;
    private static Method NBT_GET_TAG;

    private static Method HAS_KEY;
    private static Method REMOVE_KEY;

    private static Method NBT_GET_STRING;
    private static Method NBT_SET_STRING;

    static {
        try {

            // Item Classes
            if (NMSReflection.isOldPackageStructure()) {
                NBT_TAG_COMPOUND = NMSReflection.getNMSClass("NBTTagCompound");
                NMS_ITEM_STACK = NMSReflection.getNMSClass("ItemStack");
            } else {
                NBT_TAG_COMPOUND = NMSReflection.getNMSClass("nbt.NBTTagCompound");
                NMS_ITEM_STACK = NMSReflection.getNMSClass("world.item.ItemStack");
            }

            Class<?> CRAFT_ITEM_STACK = NMSReflection.getCraftClass("inventory.CraftItemStack");

            // Item Handle Methods
            assert CRAFT_ITEM_STACK != null;
            AS_NMS_COPY = CRAFT_ITEM_STACK.getDeclaredMethod("asNMSCopy", ItemStack.class);
            AS_CRAFT_MIRROR = CRAFT_ITEM_STACK.getDeclaredMethod("asCraftMirror", NMS_ITEM_STACK);

            // Item NBTTag Methods
            assert NMS_ITEM_STACK != null;
            NBT_GET_TAG = NMS_ITEM_STACK.getDeclaredMethod("getTag");
            NBT_HAS_TAG = NMS_ITEM_STACK.getDeclaredMethod("hasTag");
            NBT_SET_TAG = NMS_ITEM_STACK.getDeclaredMethod("setTag", NBT_TAG_COMPOUND);

            // Basic NBTTag Handle Methods
            REMOVE_KEY = NBT_TAG_COMPOUND.getDeclaredMethod("remove", String.class);
            HAS_KEY = NBT_TAG_COMPOUND.getDeclaredMethod("hasKey", String.class);

            // String Access Handle
            NBT_GET_STRING = NBT_TAG_COMPOUND.getDeclaredMethod("getString", String.class);
            NBT_SET_STRING = NBT_TAG_COMPOUND.getDeclaredMethod("setString", String.class, String.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Object nmsItem;

    @SneakyThrows
    public NBT(ItemStack item) {
        Validate.notNull(item, "ItemStack cannot be null.");
        this.nmsItem = AS_NMS_COPY.invoke(null, item);
    }

    @SneakyThrows
    public static String getItemName(ItemStack itemStack) {
        Object nmsItem = AS_NMS_COPY.invoke(null, itemStack);
        return (String) NMS_ITEM_STACK.getMethod("getName").invoke(nmsItem);
    }


    private boolean hasCompound() {
        try {
            return (boolean) NBT_HAS_TAG.invoke(this.nmsItem);
        } catch (Exception ignored) {
            return false;
        }
    }

    @SneakyThrows
    public String getString(String key) {
        if (hasCompound()) {
            Object NBTTagCompound = NBT_GET_TAG.invoke(this.nmsItem);
            return NBT_GET_STRING.invoke(NBTTagCompound, key).toString();
        }
        return "";
    }

    @SneakyThrows
    public NBT setString(String key, String value) {

        Validate.notNull(this.nmsItem, "nms item cannot be null");

        Object NBTTagCompound = hasCompound() ? NBT_GET_TAG.invoke(this.nmsItem) : NBT_TAG_COMPOUND.newInstance();
        NBT_SET_STRING.invoke(NBTTagCompound, key, value);
        NBT_SET_TAG.invoke(this.nmsItem, NBTTagCompound);
        return this;
    }

    @SneakyThrows
    public boolean hasTag(String key) {
        if (hasCompound()) {
            Object NBTTagCompound = NBT_GET_TAG.invoke(this.nmsItem);
            return (boolean) HAS_KEY.invoke(NBTTagCompound, key);
        }
        return false;
    }


    @SneakyThrows
    public void removeTag(String key) {
        Object NBTTagCompound = hasCompound() ? NBT_GET_TAG.invoke(this.nmsItem) : NBT_TAG_COMPOUND.newInstance();
        REMOVE_KEY.invoke(NBTTagCompound, key);
        NBT_SET_TAG.invoke(this.nmsItem, NBTTagCompound);
    }

    @SneakyThrows
    public ItemStack build() {
        return (ItemStack) AS_CRAFT_MIRROR.invoke(null, this.nmsItem);
    }
}