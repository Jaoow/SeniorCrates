package com.jaoow.crates.utils.gson.adapter;

import com.google.gson.*;
import com.jaoow.crates.utils.gson.GsonAdapter;
import com.jaoow.crates.utils.gson.serializer.ItemSerializer;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemStackArrayAdapter implements GsonAdapter<ItemStack[]> {

    @Override
    public JsonElement serialize(ItemStack[] stacks, Type type, JsonSerializationContext context) {
        String serialized = ItemSerializer.serializeItems(stacks);

        if (serialized == null) {
            return JsonNull.INSTANCE;
        }

        return new JsonPrimitive(serialized);
    }

    @Override
    public ItemStack[] deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        return ItemSerializer.deserializeItems(element.getAsString());
    }
}