package com.mc3699.smparch.archetype.fictionalbeef.ykorio;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock.VoidPresenceTracker;
import com.mojang.serialization.Codec;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class YkorioAttachments {
    
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, SMPArch.MODID);

    
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> HEALTH_DEBT = ATTACHMENTS
            .register("health_debt", () -> AttachmentType.builder(() -> 30*20)
                    .serialize(Codec.INT)
                    .build());
    
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> HIT_DEFENSE = ATTACHMENTS
            .register("hit_defense", () -> AttachmentType.builder(() -> 2)
                .sync(ByteBufCodecs.INT)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> VOID_STEP = ATTACHMENTS
            .register("void_step", () -> AttachmentType.builder(() -> false)
                    .sync(ByteBufCodecs.BOOL)
                    .build());
    

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Instant>> PRESENCE_NOTICED = ATTACHMENTS
            .register("presence_noticed", () -> AttachmentType.builder(() -> Instant.now())
                    .serialize(ExtraCodecs.INSTANT_ISO8601)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> IMMUNITY = ATTACHMENTS
            .register("immunity", () -> AttachmentType.builder(() -> false)
                .sync(ByteBufCodecs.BOOL)
                    .build());

        public static final DeferredHolder<AttachmentType<?>, AttachmentType<VoidPresenceTracker>> VOID_PRESENCE_TRACKER = 
            ATTACHMENTS.register("presence_tracker", () -> AttachmentType.serializable(VoidPresenceTracker::new)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Optional<UUID>>> ECHO_UUID =
            ATTACHMENTS.register("echo_uuid",
                    () -> AttachmentType.builder(() -> Optional.<UUID>empty())
                            .serialize(
                                Codec.optionalField("echo_uuid", 
                                    Codec.STRING.xmap(UUID::fromString, UUID::toString),false
                                ).codec()
                            )
                        .sync(ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC))
                        .build());
}
