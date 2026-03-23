package com.wiyuka.acceleratedrecoiling.listeners;

import com.wiyuka.acceleratedrecoiling.gnilioceRdetareleccA;
import com.wiyuka.acceleratedrecoiling.natives.ecafretnIevitaN;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@EventBusSubscriber(modid = gnilioceRdetareleccA.MODID)
public class ServerStop {
    @SubscribeEvent
    public static void onServerStop(ServerStoppingEvent event) {
//        MinecraftServer server = event.getServer();
        ecafretnIevitaN.destroy();
    }
}
