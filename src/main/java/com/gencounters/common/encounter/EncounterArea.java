package com.gencounters.common.encounter;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EncounterArea {
    private String name;
    private BlockPos firstCorner;
    private BlockPos secondCorner;
    private int minLevel;
    private int maxLevel;
    private double rarity;
    private final List<PokemonEntry> pokemonEntries = new ArrayList<>();

    public EncounterArea() {
    }

    public EncounterArea(String name, BlockPos firstCorner, BlockPos secondCorner, int minLevel, int maxLevel, double rarity) {
        this.name = name;
        setFirstCorner(firstCorner);
        setSecondCorner(secondCorner);
        setMinLevel(Math.min(minLevel, maxLevel));
        setMaxLevel(Math.max(minLevel, maxLevel));
        this.rarity = clampRarity(rarity);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BlockPos getFirstCorner() {
        return firstCorner;
    }

    public void setFirstCorner(BlockPos firstCorner) {
        this.firstCorner = firstCorner == null ? null : firstCorner.immutable();
    }

    public BlockPos getSecondCorner() {
        return secondCorner;
    }

    public void setSecondCorner(BlockPos secondCorner) {
        this.secondCorner = secondCorner == null ? null : secondCorner.immutable();
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = Math.max(1, minLevel);
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = Math.max(this.minLevel, maxLevel);
    }

    public double getRarity() {
        return rarity;
    }

    public void setRarity(double rarity) {
        this.rarity = clampRarity(rarity);
    }

    public List<PokemonEntry> getPokemonEntries() {
        return pokemonEntries;
    }

    private double clampRarity(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }

    public boolean contains(BlockPos pos) {
        if (firstCorner == null || secondCorner == null) {
            return false;
        }
        int minX = Math.min(firstCorner.getX(), secondCorner.getX());
        int maxX = Math.max(firstCorner.getX(), secondCorner.getX());
        int minY = Math.min(firstCorner.getY(), secondCorner.getY());
        int maxY = Math.max(firstCorner.getY(), secondCorner.getY());
        int minZ = Math.min(firstCorner.getZ(), secondCorner.getZ());
        int maxZ = Math.max(firstCorner.getZ(), secondCorner.getZ());
        return pos.getX() >= minX && pos.getX() <= maxX
                && pos.getY() >= minY && pos.getY() <= maxY
                && pos.getZ() >= minZ && pos.getZ() <= maxZ;
    }

    public int pickLevel(Random random) {
        if (minLevel >= maxLevel) {
            return Math.max(1, minLevel);
        }
        return minLevel + random.nextInt(maxLevel - minLevel + 1);
    }

    public PokemonEntry pickRandomEntry(Random random) {
        if (pokemonEntries.isEmpty()) {
            return null;
        }
        int totalWeight = pokemonEntries.stream().mapToInt(PokemonEntry::getWeight).sum();
        int choice = random.nextInt(Math.max(totalWeight, 1));
        int current = 0;
        for (PokemonEntry entry : pokemonEntries) {
            current += Math.max(1, entry.getWeight());
            if (choice < current) {
                return entry;
            }
        }
        return pokemonEntries.get(pokemonEntries.size() - 1);
    }
}
