package com.mc3699.smparch.registry;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.google.common.collect.Lists;
// import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.parser.Entity;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.AliveAlexAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.Salvation.SalvationHandlers.TargetDamage;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Attachments;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.JkmcAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentSync;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class SMPAttachments {
    
public record TARGET_DAMAGE_CODEC(UUID targetId, float damage) {
    public static final Codec<TargetDamage> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.FLOAT.fieldOf("damage").forGetter(TargetDamage::damage),
            Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("target").forGetter(TargetDamage::targetId)
        ).apply(instance, TargetDamage::new)
    );

    public static final Codec<List<TargetDamage>> LIST_CODEC = CODEC.listOf();
}



    public static void register(IEventBus eventBus)
    {
        AliveAlexAttachments.ATTACHMENTS.register(eventBus);
        BigManRakeAttachments.ATTACHMENTS.register(eventBus);
        JkmcAttachments.ATTACHMENTS.register(eventBus);
        ScapuneAttachments.ATTACHMENTS.register(eventBus);
        YkorioAttachments.ATTACHMENTS.register(eventBus);
        FlameK1ng_Attachments.ATTACHMENTS.register(eventBus);
    }
}
