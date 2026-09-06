package com.mc3699.smparch;

import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock.VoidPresenceTracker;
import com.mc3699.smparch.packets.displayTargetDamage;
import com.mc3699.smparch.registry.SMPAbilities;
import com.mc3699.smparch.registry.SMPArchetypes;
import com.mc3699.smparch.registry.SMPAttachments;
import com.mc3699.smparch.registry.SMPBlocks;
import com.mc3699.smparch.registry.SMPEntities;
import com.mc3699.smparch.registry.SMPNetworking;
import com.mc3699.smparch.registry.SMPSounds;
import com.mc3699.smparch.registry.SMPParticles;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handlers.ClientPayloadHandler;
import net.neoforged.neoforge.network.payload.FrozenRegistryPayload;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(SMPArch.MODID)
public class SMPArch {
    public static final String MODID = "smparch";
   
    public SMPArch(IEventBus modEventBus, ModContainer modContainer) {
        SMPAbilities.register(modEventBus);
        SMPArchetypes.register(modEventBus);
        SMPEntities.register(modEventBus);
        SMPSounds.register(modEventBus);
        SMPAttachments.register(modEventBus);
        SMPParticles.register(modEventBus);
        SMPBlocks.register(modEventBus);


        modEventBus.addListener(SMPNetworking::registerPayloads);
    }


    public static ResourceLocation path(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }



}
