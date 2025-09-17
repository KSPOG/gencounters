package com.gencounters.common.encounter;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public final class GrassEncounterHandler {
    public static final GrassEncounterHandler INSTANCE = new GrassEncounterHandler();
    private static final int COOLDOWN_TICKS = 200;
    private final Map<UUID, Integer> cooldowns = new HashMap<>();
    private final Random random = new Random();

    private GrassEncounterHandler() {
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }
        UUID uuid = player.getUUID();
        cooldowns.computeIfPresent(uuid, (id, ticks) -> ticks - 1 <= 0 ? null : ticks - 1);
        if (cooldowns.containsKey(uuid)) {
            return;
        }
        if (player.tickCount % 20 != 0) {
            return;
        }
        if (!isInGrass(player)) {
            return;
        }
        EncounterArea area = EncounterRegistry.findAreaFor(player.blockPosition());
        if (area == null) {
            return;
        }
        if (random.nextDouble() > area.getRarity()) {
            return;
        }
        PokemonEntry entry = area.pickRandomEntry(random);
        if (entry == null) {
            return;
        }
        triggerEncounter(player, area, entry);
    }

    private boolean isInGrass(ServerPlayer player) {
        BlockPos pos = player.blockPosition();
        ServerLevel level = player.serverLevel();
        BlockState stateAtFeet = level.getBlockState(pos);
        BlockState stateBelow = level.getBlockState(pos.below());
        return isGrassBlock(stateAtFeet) || isGrassBlock(stateBelow);
    }

    private boolean isGrassBlock(BlockState state) {
        Block block = state.getBlock();
        return block instanceof TallGrassBlock || block instanceof BushBlock || block instanceof GrassBlock
                || block == Blocks.GRASS || block == Blocks.FERN || block == Blocks.TALL_GRASS || block == Blocks.LARGE_FERN
                || block == Blocks.GRASS_BLOCK;
    }

    private void triggerEncounter(ServerPlayer player, EncounterArea area, PokemonEntry entry) {
        MinecraftServer server = player.server;
        if (server == null) {
            return;
        }
        if (entry.getSpecies() == null || entry.getSpecies().isEmpty()) {
            return;
        }
        cooldowns.put(player.getUUID(), COOLDOWN_TICKS);
        int level = area.pickLevel(random);
        boolean shiny = random.nextInt(30000) == 0;
        StringBuilder command = new StringBuilder("pokespawn ").append(entry.getSpecies())
                .append(" lvl:").append(level);
        if (shiny) {
            command.append(" shiny");
        }
        server.getCommands().performPrefixedCommand(player.createCommandSourceStack().withSuppressedOutput().withPermission(4), command.toString());
        player.sendSystemMessage(Component.translatable("message.gencounters.encounter.trigger", entry.getSpecies(), level, shiny));
    }
}
