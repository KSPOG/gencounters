package com.gencounters.common.encounter;

public class PokemonEntry {
    private String species;
    private int weight;

    public PokemonEntry() {
    }

    public PokemonEntry(String species, int weight) {
        this.species = species;
        this.weight = Math.max(1, weight);
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = Math.max(1, weight);
    }
}
