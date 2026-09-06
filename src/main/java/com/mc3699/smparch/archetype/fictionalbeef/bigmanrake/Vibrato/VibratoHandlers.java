package com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.Vibrato;

import java.rmi.AccessException;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

import javax.swing.text.html.parser.Entity;

import com.google.common.eventbus.Subscribe;
import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.Concerto.ConcertoHandlers;
import com.mc3699.smparch.registry.SMPAbilities;
import com.mc3699.smparch.registry.SMPAttachments;
import com.mc3699.smparch.registry.SMPSounds;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.mc3699.provenance.ProvenanceDataHandler;
import net.mc3699.provenance.ProvenanceRegistries;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.network.ProvNetworking;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.phys.AABB;

import com.mojang.serialization.Codec;

@EventBusSubscriber(modid = SMPArch.MODID)
public class VibratoHandlers {
    public static final int VibratoWindow = 7000;
    public static final float saturationCap = 30.0f;

    public static void addFood(FoodData dealer, int FoodIncrease, float SaturationIncrease) {
        dealer.setFoodLevel(Math.clamp(dealer.getFoodLevel() + FoodIncrease, 0, 20));
        dealer.setSaturation(Math.clamp(dealer.getSaturationLevel() + SaturationIncrease, 0.0f, saturationCap));

    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        float CurrentDamage = event.getNewDamage();
        final float FinalCurrentDamage = CurrentDamage / 2.0f;
        final int FinalCurrentDamageFood = (int) (CurrentDamage / 4.0f);

        if (!(event.getSource().getEntity() instanceof Player dealer)
                || !dealer.hasData(BigManRakeAttachments.VIBRATO)) {
            return;
        }

        ResourceLocation insanity = ConcertoHandlers.insanityResource;
        Optional<Reference<MobEffect>> insanityEffect = BuiltInRegistries.MOB_EFFECT
                .getHolder(insanity);

            Instant timeofHit = dealer.getData(BigManRakeAttachments.VIBRATO);

            if (!Instant.now().isBefore(timeofHit.plusMillis(VibratoWindow))) {
                return;
            }

        insanityEffect.ifPresentOrElse(effect -> {
            if (!event.getEntity().hasEffect(effect)) {
                event.getEntity()
                        .addEffect(new MobEffectInstance(effect.getDelegate(), (int) 5 * 20, (int) 0, true, false));
                dealer.removeData(BigManRakeAttachments.VIBRATO);
                return;
            }

            addFood(dealer.getFoodData(), FinalCurrentDamageFood, FinalCurrentDamage);
            dealer.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 0));
        }, () -> {

            addFood(dealer.getFoodData(), FinalCurrentDamageFood, FinalCurrentDamage);
            dealer.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 0));
        });

        event.setNewDamage(CurrentDamage);

    }

}
