package com.gencounters;

import com.gencounters.common.command.EncounterCommand;
import com.gencounters.common.command.WandCommand;
import com.gencounters.common.config.EncounterStorage;
import com.gencounters.common.encounter.EncounterRegistry;
import com.gencounters.common.encounter.GrassEncounterHandler;
import com.gencounters.common.selection.SelectionEventHandler;
import com.gencounters.common.util.ModConstants;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModConstants.MOD_ID)
public class GencountersMod {
    public GencountersMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(SelectionEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(GrassEncounterHandler.INSTANCE);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        // No-op for now but kept for future network sync or initialization.
    }

    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        EncounterStorage.load(event.getServer());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        EncounterStorage.save();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        WandCommand.register(event.getDispatcher());
        EncounterCommand.register(event.getDispatcher());
    }
}
