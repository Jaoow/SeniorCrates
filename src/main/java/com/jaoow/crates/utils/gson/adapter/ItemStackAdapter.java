package com.jaoow.crates.utils.gson.adapter;

import com.google.gson.*;
import com.jaoow.crates.utils.gson.GsonAdapter;
import com.jaoow.crates.utils.gson.serializer.ItemSerializer;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemStackAdapter implements GsonAdapter<ItemStack> {

    @Override
    public JsonElement serialize(ItemStack stack, Type type, JsonSerializationContext context) {
        String serialized = ItemSerializer.serializeItem(stack);
        if (serialized == null) {
            return JsonNull.INSTANCE;
        }

        return new JsonPrimitive(serialized);
    }

    @Override
    public ItemStack deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        return ItemSerializer.deserializeItem(element.getAsString());
    }
}