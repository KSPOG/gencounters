package com.gencounters.common.encounter;

import com.gencounters.common.config.EncounterStorage;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class EncounterRegistry {
    private static final Map<String, EncounterArea> AREAS = new ConcurrentHashMap<>();

    private EncounterRegistry() {
    }

    public static Collection<EncounterArea> getAreas() {
        return Collections.unmodifiableCollection(AREAS.values());
    }

    public static void replaceAll(Collection<EncounterArea> areas) {
        AREAS.clear();
        areas.forEach(area -> AREAS.put(area.getName().toLowerCase(), area));
    }

    public static void addArea(EncounterArea area) {
        AREAS.put(area.getName().toLowerCase(), area);
        EncounterStorage.save();
    }

    public static void removeArea(String name) {
        AREAS.remove(name.toLowerCase());
        EncounterStorage.save();
    }

    public static Optional<EncounterArea> getArea(String name) {
        return Optional.ofNullable(AREAS.get(name.toLowerCase()));
    }

    @Nullable
    public static EncounterArea findAreaFor(BlockPos pos) {
        for (EncounterArea area : AREAS.values()) {
            if (area.contains(pos)) {
                return area;
            }
        }
        return null;
    }
}
