package com.mc3699.smparch.archetype.fictionalbeef.bigmanrake;

import java.time.Instant;
import java.util.Optional;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.registry.SMPAttachments;
import com.mc3699.smparch.registry.SMPSounds;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ConcertoAbility extends BaseAbility {
    @Override
    public boolean canExecute(ServerPlayer Player) {
        return (Player.getHealth() < 2.5f);
    }

    @Override
    public int getCooldown() {
        // return 10;
        return 60*30*20; // thirty minutes so he cant fucking spam it
    }

    @Override
    public Component getName() {
        return Component.literal("Concerto");
    }

    @Override
    public float getUseCost() {
        return 5f;
    }

    public void execute(ServerPlayer Player) {
        Boolean[] movementCompleted = new Boolean[5];
        java.util.Arrays.fill(movementCompleted, false);
        Player.setData(BigManRakeAttachments.CONCERTO_STAGE,movementCompleted);
        Player.setData(BigManRakeAttachments.MOVEMENT,0.01f);
        Player.syncData(BigManRakeAttachments.MOVEMENT);
        Player.syncData(BigManRakeAttachments.CONCERTO_STAGE);

        if (Player.hasData(BigManRakeAttachments.ALREADY_DEAD)) {
            Player.removeData(BigManRakeAttachments.ALREADY_DEAD);
            Player.connection.send(new ClientboundStopSoundPacket(BigManRakeSounds.MAEXTRODE.getKey().location(),
                        SoundSource.PLAYERS));
        }

        Player.setHealth(Player.getHealth()+6f);
        Player.level().playSound(null, Player.getBlockPosBelowThatAffectsMyMovement(), BigManRakeSounds.MOVEMENT0CAP.value(), SoundSource.PLAYERS, 1f,  1f);

        Player.setData(BigManRakeAttachments.START_TICK, Player.level().getGameTime());
        // Level level = Player.level();

        // AABB bounds = Player.getBoundingBox().inflate(20);

        // for (Entity inrange : level.getEntities(Player, bounds, entity -> entity.isAlive())) {
        //     if (!(inrange instanceof LivingEntity Liver)) continue;

        //     MobEffectInstance Slowness = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,14*20,1);
        //     MobEffectInstance Weakness = new MobEffectInstance(MobEffects.WEAKNESS,14*20,1);

        //     Liver.addEffect(Slowness);
        //     Liver.addEffect(Weakness);

        // }

        // level.playSound(null, Player.getBlockPosBelowThatAffectsMyMovement(), SMPSounds.ORCHWORK.value(),
        //         SoundSource.PLAYERS, 0.3f, .8f);

    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/bigmanrake/concerto.png");
    }

}
