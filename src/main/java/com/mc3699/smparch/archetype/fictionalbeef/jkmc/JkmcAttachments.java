package com.mc3699.smparch.archetype.fictionalbeef.jkmc;

import com.mc3699.smparch.SMPArch;
import com.mojang.serialization.Codec;

import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class JkmcAttachments {
    
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, SMPArch.MODID);
            
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> JKMC_CHARGE = ATTACHMENTS
            .register("jkmc_charge", () -> AttachmentType.builder(() -> 0.0f)
                    .serialize(Codec.FLOAT)
                    .sync(ByteBufCodecs.FLOAT)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> JKMC_LARGEHIT = ATTACHMENTS
            .register("jkmc_largehit", () -> AttachmentType.builder(() -> false)
                    .sync(ByteBufCodecs.BOOL)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> JKMC_DYINGMOMENTUM = ATTACHMENTS
            .register("jkmc_dyingmomentum", () -> AttachmentType.builder(() -> 0.0f)
                    .sync(ByteBufCodecs.FLOAT)
                    .build());
}
