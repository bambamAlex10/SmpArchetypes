package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep;

import com.mc3699.smparch.SMPArch;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record VoidStepPayload() implements CustomPacketPayload {
    
    // Replace "yourmodid" with your actual mod ID
    public static final Type<VoidStepPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "void_step_packet"));
    
    // Codec used to read/write the packet from the network stream
    public static final StreamCodec<FriendlyByteBuf, VoidStepPayload> CODEC = StreamCodec.of(
            (buf, value) -> {}, // Nothing to write
            buf -> new VoidStepPayload() // Nothing to read, just construct it
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}