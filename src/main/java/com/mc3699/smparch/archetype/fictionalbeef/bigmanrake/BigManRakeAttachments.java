package com.mc3699.smparch.archetype.fictionalbeef.bigmanrake;

import java.time.Instant;
import java.util.List;

import com.mc3699.smparch.SMPArch;
import com.mojang.serialization.Codec;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class BigManRakeAttachments {
    
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, SMPArch.MODID);
            
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean[]>> CONCERTO_STAGE = ATTACHMENTS
            .register("concerto_stage", () -> AttachmentType.builder(() -> {
                    Boolean[] defaultArray = new Boolean[5];
                    java.util.Arrays.fill(defaultArray, false); 
                    return defaultArray;
                })
                    .serialize(Codec.BOOL.listOf(5, 5).xmap(
                        list -> list.toArray(new Boolean[0]),
                        array -> java.util.Arrays.stream(array)
                                .map(b -> b != null && b) // Replaces any null entry with false
                                .toList() // Safe replacement for List.of
                ))
                    .sync((
                        ByteBufCodecs.BOOL
                                .apply(ByteBufCodecs.list(5)) // Encodes a list of exactly 5 elements
                                .map(l -> l.toArray(new Boolean[0]), List::of) // Map list <-> array
                ))
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> ACCELEARNDO = ATTACHMENTS
            .register("accelerando", () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Instant>> VIBRATO = ATTACHMENTS
            .register("vibrato", () -> AttachmentType.builder(() -> Instant.now())
                    .serialize(ExtraCodecs.INSTANT_ISO8601)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> MOVEMENT = ATTACHMENTS
            .register("movement",()-> AttachmentType.builder(() -> 0f)
                    .serialize(Codec.FLOAT)
                    .sync(ByteBufCodecs.FLOAT)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Long>> START_TICK = ATTACHMENTS 
            .register("start_tick",()-> AttachmentType.builder(() -> 0L)
                    .serialize(Codec.LONG)
                    .sync(ByteBufCodecs.VAR_LONG)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Instant>> ALREADY_DEAD = ATTACHMENTS
            .register("already_dead_bigmanrake", () -> AttachmentType.builder(() -> Instant.now())
                    .serialize(ExtraCodecs.INSTANT_ISO8601)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> HITBOXHIT = ATTACHMENTS
            .register("hitboxhit_bigmanrake", () -> AttachmentType.builder(() -> false)
                //     .serialize(Codec.BOOL)
                    .build());
}
