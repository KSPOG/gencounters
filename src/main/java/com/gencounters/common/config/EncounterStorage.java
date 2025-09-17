package com.gencounters.common.config;

import com.gencounters.common.encounter.EncounterArea;
import com.gencounters.common.encounter.EncounterRegistry;
import com.gencounters.common.util.ModConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class EncounterStorage {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(EncounterArea.class, new EncounterTypeAdapter())
            .create();
    private static final Type COLLECTION_TYPE = new TypeToken<List<EncounterArea>>() {
    }.getType();
    private static Path storagePath;

    private EncounterStorage() {
    }

    public static void load(MinecraftServer server) {
        Path basePath = server.getServerDirectory().toPath().resolve("config");
        storagePath = basePath.resolve(ModConstants.MOD_ID).resolve("areas.json");
        try {
            Files.createDirectories(storagePath.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create config directory", e);
        }

        if (!Files.exists(storagePath)) {
            EncounterRegistry.replaceAll(new ArrayList<>());
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(storagePath)) {
            List<EncounterArea> areas = GSON.fromJson(reader, COLLECTION_TYPE);
            if (areas == null) {
                areas = new ArrayList<>();
            }
            EncounterRegistry.replaceAll(areas);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read encounter storage", e);
        }
    }

    public static void save() {
        if (storagePath == null) {
            return;
        }
        Collection<EncounterArea> areas = EncounterRegistry.getAreas();
        try (Writer writer = Files.newBufferedWriter(storagePath)) {
            GSON.toJson(areas, COLLECTION_TYPE, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save encounter storage", e);
        }
    }
}
