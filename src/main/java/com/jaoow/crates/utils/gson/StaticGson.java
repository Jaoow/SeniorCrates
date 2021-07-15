package com.jaoow.crates.utils.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaoow.crates.utils.gson.adapter.ItemStackAdapter;
import com.jaoow.crates.utils.gson.adapter.ItemStackArrayAdapter;
import org.bukkit.inventory.ItemStack;

public final class StaticGson {

    private static final GsonBuilder GSON_BUILDER = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(ItemStack[].class, new ItemStackArrayAdapter());


    // Raw Gson
    private static final Gson GSON = GSON_BUILDER.create();
    private static final Gson GSON_PRETTY = GSON_BUILDER
            .setPrettyPrinting()
            .create();

    public static Gson getGson() {
        return GSON;
    }

    public static Gson getGsonPretty() {
        return GSON_PRETTY;
    }

}