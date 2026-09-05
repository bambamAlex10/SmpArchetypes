package com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.Brimstone;

import com.mc3699.smparch.SMPArch;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BrimstonePayload(java.util.UUID playerId, boolean result) implements CustomPacketPayload {
    
    // Replace "yourmodid" with your actual mod ID
    public static final Type<BrimstonePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "coin_flip_packet"));
    
    public static final StreamCodec<FriendlyByteBuf, BrimstonePayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, BrimstonePayload::playerId,
            ByteBufCodecs.BOOL, BrimstonePayload::result,
            BrimstonePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}