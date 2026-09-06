package com.mc3699.smparch.archetype.fictionalbeef.scapune;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.mc3699.smparch.SMPArch;
import com.mojang.serialization.Codec;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ScapuneAttachments {
    
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, SMPArch.MODID);
            
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> DECIEVERS_AWARENESS = ATTACHMENTS //needed for bigmanrakes passive 'Acceso Morendo' and alivealexs active 'retribution'
            .register("decievers_awareness", () -> AttachmentType.builder(() -> 0.01f)
                    .serialize(Codec.FLOAT)
                    .sync(ByteBufCodecs.FLOAT)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Optional<UUID>>> GHOST_UUID =
            ATTACHMENTS.register("ghost_uuid",
                    () -> AttachmentType.builder(() -> Optional.<UUID>empty())
                            .serialize(
                                Codec.optionalField("ghost_uuid", 
                                    Codec.STRING.xmap(UUID::fromString, UUID::toString),false
                                ).codec()
                            )
                        .sync(ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC))
                        .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> STRENGTH_AMPLIFIER = ATTACHMENTS //needed for bigmanrakes passive 'Acceso Morendo' and alivealexs active 'retribution'
            .register("decievers_strength", () -> AttachmentType.builder(() -> 1)
                    .serialize(Codec.INT)
                    .sync(ByteBufCodecs.INT)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Instant>> WILL_MOMENT = ATTACHMENTS
            .register("will_start", () -> AttachmentType.builder(() -> Instant.now())
                    .serialize(ExtraCodecs.INSTANT_ISO8601)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> WILL_STRENGTH = ATTACHMENTS
            .register("will_strength", () -> AttachmentType.builder(() -> 0.0f)
                    .serialize(Codec.FLOAT)
                    .sync(ByteBufCodecs.FLOAT)
                    .build());
                    
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> DISTANCE_EFFECT = ATTACHMENTS //needed for bigmanrakes passive 'Acceso Morendo' and alivealexs active 'retribution'
            .register("decievers_deception", () -> AttachmentType.builder(() -> 1)
                //     .serialize(Codec.INT)
                //     .sync(ByteBufCodecs.INT)
                    .build());
}
