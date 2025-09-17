package com.gencounters.common.command;

import com.gencounters.common.encounter.EncounterArea;
import com.gencounters.common.encounter.EncounterRegistry;
import com.gencounters.common.encounter.PokemonEntry;
import com.gencounters.common.selection.SelectionData;
import com.gencounters.common.selection.SelectionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class EncounterCommand {
    private static final SimpleCommandExceptionType NO_SELECTION = new SimpleCommandExceptionType(Component.translatable("command.gencounters.no_selection"));
    private static final SimpleCommandExceptionType AREA_NOT_FOUND = new SimpleCommandExceptionType(Component.translatable("command.gencounters.area_not_found"));

    private EncounterCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("pencounter")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("minLevel", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("maxLevel", IntegerArgumentType.integer(1))
                                                .then(Commands.argument("rarity", DoubleArgumentType.doubleArg(0.0, 1.0))
                                                        .executes(context -> createEncounter(context, StringArgumentType.getString(context, "name"), IntegerArgumentType.getInteger(context, "minLevel"), IntegerArgumentType.getInteger(context, "maxLevel"), DoubleArgumentType.getDouble(context, "rarity"))))))))
                .then(Commands.literal("addpokemon")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("species", StringArgumentType.string())
                                        .then(Commands.argument("weight", IntegerArgumentType.integer(1))
                                                .executes(context -> addPokemon(context, StringArgumentType.getString(context, "name"), StringArgumentType.getString(context, "species"), IntegerArgumentType.getInteger(context, "weight")))))))
                .then(Commands.literal("removepokemon")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("species", StringArgumentType.string())
                                        .executes(context -> removePokemon(context, StringArgumentType.getString(context, "name"), StringArgumentType.getString(context, "species"))))))
                .then(Commands.literal("delete")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(context -> deleteEncounter(context, StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("list")
                        .executes(EncounterCommand::listEncounters))
                .then(Commands.literal("setrarity")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("rarity", DoubleArgumentType.doubleArg(0.0, 1.0))
                                        .executes(context -> updateRarity(context, StringArgumentType.getString(context, "name"), DoubleArgumentType.getDouble(context, "rarity"))))))
                .then(Commands.literal("setlevels")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("minLevel", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("maxLevel", IntegerArgumentType.integer(1))
                                                .executes(context -> updateLevels(context, StringArgumentType.getString(context, "name"), IntegerArgumentType.getInteger(context, "minLevel"), IntegerArgumentType.getInteger(context, "maxLevel"))))))));
    }

    private static int createEncounter(CommandContext<CommandSourceStack> context, String name, int minLevel, int maxLevel, double rarity) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        SelectionData selection = SelectionManager.getSelection(player);
        if (selection == null || !selection.isComplete()) {
            throw NO_SELECTION.create();
        }
        BlockPos first = selection.getFirst();
        BlockPos second = selection.getSecond();
        EncounterArea area = new EncounterArea(name, first, second, minLevel, maxLevel, rarity);
        EncounterRegistry.addArea(area);
        SelectionManager.clearSelection(player);
        player.sendSystemMessage(Component.translatable("message.gencounters.area.created", name));
        return 1;
    }

    private static int addPokemon(CommandContext<CommandSourceStack> context, String name, String species, int weight) throws CommandSyntaxException {
        EncounterArea area = EncounterRegistry.getArea(name).orElseThrow(AREA_NOT_FOUND::create);
        area.getPokemonEntries().add(new PokemonEntry(species, weight));
        context.getSource().sendSuccess(() -> Component.translatable("message.gencounters.area.pokemon_added", species, name), true);
        EncounterRegistry.addArea(area);
        return 1;
    }

    private static int removePokemon(CommandContext<CommandSourceStack> context, String name, String species) throws CommandSyntaxException {
        EncounterArea area = EncounterRegistry.getArea(name).orElseThrow(AREA_NOT_FOUND::create);
        boolean removed = area.getPokemonEntries().removeIf(entry -> entry.getSpecies().equalsIgnoreCase(species));
        if (removed) {
            context.getSource().sendSuccess(() -> Component.translatable("message.gencounters.area.pokemon_removed", species, name), true);
            EncounterRegistry.addArea(area);
        } else {
            context.getSource().sendFailure(Component.translatable("message.gencounters.area.pokemon_missing", species, name));
        }
        return removed ? 1 : 0;
    }

    private static int deleteEncounter(CommandContext<CommandSourceStack> context, String name) {
        if (EncounterRegistry.getArea(name).isEmpty()) {
            context.getSource().sendFailure(Component.translatable("message.gencounters.area_not_found"));
            return 0;
        }
        EncounterRegistry.removeArea(name);
        context.getSource().sendSuccess(() -> Component.translatable("message.gencounters.area.deleted", name), true);
        return 1;
    }

    private static int listEncounters(CommandContext<CommandSourceStack> context) {
        if (EncounterRegistry.getAreas().isEmpty()) {
            context.getSource().sendSuccess(() -> Component.translatable("message.gencounters.area.none"), false);
            return 0;
        }
        EncounterRegistry.getAreas().forEach(area -> context.getSource().sendSuccess(() -> Component.translatable("message.gencounters.area.entry", area.getName(), area.getMinLevel(), area.getMaxLevel(), area.getRarity(), area.getPokemonEntries().size()), false));
        return 1;
    }

    private static int updateRarity(CommandContext<CommandSourceStack> context, String name, double rarity) throws CommandSyntaxException {
        EncounterArea area = EncounterRegistry.getArea(name).orElseThrow(AREA_NOT_FOUND::create);
        area.setRarity(rarity);
        EncounterRegistry.addArea(area);
        context.getSource().sendSuccess(() -> Component.translatable("message.gencounters.area.rarity", name, rarity), true);
        return 1;
    }

    private static int updateLevels(CommandContext<CommandSourceStack> context, String name, int minLevel, int maxLevel) throws CommandSyntaxException {
        EncounterArea area = EncounterRegistry.getArea(name).orElseThrow(AREA_NOT_FOUND::create);
        area.setMinLevel(minLevel);
        area.setMaxLevel(maxLevel);
        EncounterRegistry.addArea(area);
        context.getSource().sendSuccess(() -> Component.translatable("message.gencounters.area.levels", name, minLevel, maxLevel), true);
        return 1;
    }
}
