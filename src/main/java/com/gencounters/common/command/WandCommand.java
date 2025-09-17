package com.gencounters.common.command;

import com.gencounters.common.selection.SelectionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class WandCommand {
    private WandCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("pwand")
                .requires(source -> source.hasPermission(2))
                .executes(context -> giveWand(context.getSource())));
    }

    private static int giveWand(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ItemStack wand = new ItemStack(Items.WOODEN_AXE);
        wand.setHoverName(Component.translatable("item.gencounters.selection_wand"));
        boolean added = player.getInventory().add(wand);
        if (!added) {
            player.drop(wand, false);
        }
        player.sendSystemMessage(Component.translatable("message.gencounters.wand.given"));
        SelectionManager.clearSelection(player);
        return 1;
    }
}
