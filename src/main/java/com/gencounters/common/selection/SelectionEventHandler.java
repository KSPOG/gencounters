package com.gencounters.common.selection;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class SelectionEventHandler {
    public static final SelectionEventHandler INSTANCE = new SelectionEventHandler();

    private SelectionEventHandler() {
    }

    @SubscribeEvent
    public void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        if (!SelectionManager.isSelectionTool(event.getItemStack())) {
            return;
        }
        BlockPos pos = event.getPos();
        SelectionManager.setFirst(player, pos);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        if (!SelectionManager.isSelectionTool(event.getItemStack())) {
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        BlockPos pos = event.getPos();
        SelectionManager.setSecond(player, pos);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
