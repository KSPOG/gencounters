package com.gencounters.common.selection;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SelectionManager {
    private static final Map<UUID, SelectionData> ACTIVE_SELECTIONS = new ConcurrentHashMap<>();

    private SelectionManager() {
    }

    public static boolean isSelectionTool(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == Items.WOODEN_AXE;
    }

    private static SelectionData getOrCreate(ServerPlayer player) {
        return ACTIVE_SELECTIONS.computeIfAbsent(player.getUUID(), uuid -> new SelectionData());
    }

    public static void setFirst(ServerPlayer player, BlockPos pos) {
        SelectionData selection = getOrCreate(player);
        selection.setFirst(pos.immutable());
        player.sendSystemMessage(Component.translatable("message.gencounters.selection.first", pos.getX(), pos.getY(), pos.getZ()));
        if (selection.getSecond() != null) {
            promptForEncounter(player);
        }
    }

    public static void setSecond(ServerPlayer player, BlockPos pos) {
        SelectionData selection = getOrCreate(player);
        selection.setSecond(pos.immutable());
        player.sendSystemMessage(Component.translatable("message.gencounters.selection.second", pos.getX(), pos.getY(), pos.getZ()));
        if (selection.getFirst() != null) {
            promptForEncounter(player);
        }
    }

    public static SelectionData getSelection(ServerPlayer player) {
        return getOrCreate(player);
    }

    public static void clearSelection(ServerPlayer player) {
        ACTIVE_SELECTIONS.remove(player.getUUID());
    }

    private static void promptForEncounter(ServerPlayer player) {
        SelectionData selection = getOrCreate(player);
        if (!selection.isComplete()) {
            return;
        }
        player.sendSystemMessage(Component.translatable("message.gencounters.selection.prompt"));
    }
}
