package com.mc3699.smparch.packets;

import java.util.UUID;
import java.util.stream.Stream;


import com.mc3699.smparch.SMPArch;
import com.mojang.serialization.Codec;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record displayTargetDamage(float damage, int entity) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<displayTargetDamage> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SMPArch.MODID,"numberdisplay"));

    public static final StreamCodec<FriendlyByteBuf, displayTargetDamage> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, displayTargetDamage::damage,
        ByteBufCodecs.INT, displayTargetDamage::entity,
        displayTargetDamage::new
    );


    @Override
	public Type<? extends CustomPacketPayload> type() {
        return TYPE;
	}

    

}