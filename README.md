# gencounters

A Forge server-side helper mod for Pixelmon Reforged that enables configurable wild encounters triggered by walking through grass.

## Features

- Uses the default `minecraft:wooden_axe` as a selection tool for defining rectangular encounter regions.
- `/pwand` command gives moderators a pre-named wooden axe wand and clears any existing selection.
- Selecting two corners prompts the player to configure the area with `/pencounter create <name> <minLevel> <maxLevel> <rarity>`.
- `/pencounter` command suite to add/remove Pokémon, adjust level ranges or rarity, delete and list areas.
- Encounter definitions are persisted to `config/gencounters/areas.json` so they survive restarts.
- Players walking through configured grass have encounters spawned via Pixelmon's `pokespawn` command with a 1/30,000 shiny chance.

## Commands

| Command | Description |
| --- | --- |
| `/pwand` | Gives the executing player the encounter wand (wooden axe). |
| `/pencounter create <name> <minLevel> <maxLevel> <rarity>` | Saves the current wand selection as an encounter area. Rarity should be a value between 0 and 1. |
| `/pencounter addpokemon <name> <species> <weight>` | Adds a Pixelmon species to the named area with the supplied weight. |
| `/pencounter removepokemon <name> <species>` | Removes a species from the named area. |
| `/pencounter setlevels <name> <minLevel> <maxLevel>` | Updates the level range for the named area. |
| `/pencounter setrarity <name> <rarity>` | Updates the encounter probability checked roughly once per second. |
| `/pencounter delete <name>` | Deletes the named area. |
| `/pencounter list` | Lists all configured encounter areas and the number of Pokémon entries configured. |

## Building

This project uses ForgeGradle for Minecraft 1.16.5. Install a JDK 8+ and then run:

```bash
./gradlew build
```

The compiled mod JAR will be located in `build/libs/`.
