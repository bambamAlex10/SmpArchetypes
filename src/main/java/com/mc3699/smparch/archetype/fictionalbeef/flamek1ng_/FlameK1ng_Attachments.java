package com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_;
import java.time.Instant;
import java.util.List;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.Salvation.SalvationHandlers.TargetDamage;
import com.mc3699.smparch.registry.SMPAttachments.TARGET_DAMAGE_CODEC;
import com.mojang.serialization.Codec;
import com.google.common.collect.Lists;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class FlameK1ng_Attachments {
    
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, SMPArch.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Instant>> PYROPHAGIA = ATTACHMENTS
            .register("pyrophagia_happened", () -> AttachmentType.builder(() -> Instant.now())
                    .serialize(ExtraCodecs.INSTANT_ISO8601)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Instant>> SCALEDURATION = ATTACHMENTS
            .register("scale_duration", () -> AttachmentType.builder(() -> Instant.now())
                    .serialize(ExtraCodecs.INSTANT_ISO8601)
                    .build());
                    
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> IN_SUPER_STATE = ATTACHMENTS
            .register("in_super_state", () -> AttachmentType.builder(() -> false)
                //     .serialize(Codec.BOOL)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> FIREDASH = ATTACHMENTS
            .register("firedash", () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> HITBOXHIT = ATTACHMENTS
            .register("hitboxhit_flame", () -> AttachmentType.builder(() -> false)
                //     .serialize(Codec.BOOL)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> HASHEADS = ATTACHMENTS
            .register("hasheads_flame", () -> AttachmentType.builder(() -> false)
                //     .serialize(Codec.BOOL)
                    .build());
//     public static final DeferredHolder<AttachmentType<?>, AttachmentType<Vec3>> LAST_POS = ATTACHMENTS
//             .register("last_pos", () -> AttachmentType.builder(() -> new Vec3(0,0,0))
//                     .serialize(Vec3.CODEC)
//                     .build());

//     public static final DeferredHolder<AttachmentType<?>, AttachmentType<Instant>> INSTANT2 = ATTACHMENTS
//             .register("instant2", () -> AttachmentType.builder(() -> Instant.now())
//                     .build());

//     public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> SALVATION = ATTACHMENTS
//             .register("salvation", () -> AttachmentType.builder(() -> false)
//                     .sync(ByteBufCodecs.BOOL)
//                     .build());

//     public static final DeferredHolder<AttachmentType<?>, AttachmentType<List<TargetDamage>>> CAPTUREDDAMAGE = ATTACHMENTS
//     .register("captured_damage", () ->
//         AttachmentType.<List<TargetDamage>>builder(() -> Lists.newArrayList())
//             .serialize(TARGET_DAMAGE_CODEC.LIST_CODEC)  
//             .build()
//     );

//     public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> DAMAGE_LOADING = ATTACHMENTS
//             .register("damage_loading", () -> AttachmentType.builder(() -> false)
//                     .sync(ByteBufCodecs.BOOL)
//                     .build());

//     public static final DeferredHolder<AttachmentType<?>, AttachmentType<Instant>> ALREADY_DEAD = ATTACHMENTS
//             .register("already_dead", () -> AttachmentType.builder(() -> Instant.now())
//                     .serialize(ExtraCodecs.INSTANT_ISO8601)
//                     .build());
}
