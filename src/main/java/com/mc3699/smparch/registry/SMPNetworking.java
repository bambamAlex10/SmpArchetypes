package com.mc3699.smparch.registry;

import com.mc3699.smparch.archetype.fictionalbeef.alivealex.Salvation.SalvationUIOwner;
import com.mc3699.smparch.packets.displayTargetDamage;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class SMPNetworking {
    
    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(displayTargetDamage.TYPE, displayTargetDamage.STREAM_CODEC, 
            (payload,content) -> {
                SalvationUIOwner.retributionHandler(payload, content);
            });
    }
}