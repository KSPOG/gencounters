package com.gencounters.common.config;

import com.gencounters.common.encounter.EncounterArea;
import com.gencounters.common.encounter.PokemonEntry;
import com.google.gson.*;
import net.minecraft.core.BlockPos;

import java.lang.reflect.Type;

public class EncounterTypeAdapter implements JsonSerializer<EncounterArea>, JsonDeserializer<EncounterArea> {
    @Override
    public EncounterArea deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        EncounterArea area = new EncounterArea();
        area.setName(object.get("name").getAsString());
        area.setFirstCorner(deserializeBlockPos(object.getAsJsonObject("first")));
        area.setSecondCorner(deserializeBlockPos(object.getAsJsonObject("second")));
        area.setMinLevel(object.get("minLevel").getAsInt());
        area.setMaxLevel(object.get("maxLevel").getAsInt());
        area.setRarity(object.get("rarity").getAsDouble());
        JsonArray entries = object.getAsJsonArray("entries");
        if (entries != null) {
            for (JsonElement element : entries) {
                area.getPokemonEntries().add(context.deserialize(element, PokemonEntry.class));
            }
        }
        return area;
    }

    @Override
    public JsonElement serialize(EncounterArea src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("name", src.getName());
        object.add("first", serializeBlockPos(src.getFirstCorner()));
        object.add("second", serializeBlockPos(src.getSecondCorner()));
        object.addProperty("minLevel", src.getMinLevel());
        object.addProperty("maxLevel", src.getMaxLevel());
        object.addProperty("rarity", src.getRarity());
        JsonArray entries = new JsonArray();
        for (PokemonEntry entry : src.getPokemonEntries()) {
            entries.add(context.serialize(entry, PokemonEntry.class));
        }
        object.add("entries", entries);
        return object;
    }

    private JsonObject serializeBlockPos(BlockPos pos) {
        JsonObject object = new JsonObject();
        if (pos != null) {
            object.addProperty("x", pos.getX());
            object.addProperty("y", pos.getY());
            object.addProperty("z", pos.getZ());
        }
        return object;
    }

    private BlockPos deserializeBlockPos(JsonObject object) {
        if (object == null || !object.has("x") || !object.has("y") || !object.has("z")) {
            return null;
        }
        return new BlockPos(object.get("x").getAsInt(), object.get("y").getAsInt(), object.get("z").getAsInt());
    }
}
