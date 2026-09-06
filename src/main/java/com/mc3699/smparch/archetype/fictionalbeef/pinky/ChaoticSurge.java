package com.mc3699.smparch.archetype.fictionalbeef.pinky;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.registry.SMPSounds;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.util.ProvScheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class ChaoticSurge extends BaseAbility {

    @Override
    public boolean canExecute(ServerPlayer Player) {
        return true;
    }

    @Override
    public int getCooldown() {
        return 12*20;
    }

    @Override
    public Component getName() {
        return Component.literal("Chaotic Surge");
    }

    @Override
    public float getUseCost() {
        return 1;
    }

    public void execute(ServerPlayer Player) {
        Level level = Player.level();

        AABB bounds = Player.getBoundingBox().inflate(10);

        for (Entity inrange : level.getEntities(Player, bounds, entity -> entity.isAlive())) {
            if (!(inrange instanceof LivingEntity Liver)) continue;

            Liver.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,7*20,1));
            Liver.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 7*20,2));
            Liver.hurt(Liver.level().damageSources().sting(Player),0f);
        }

        level.playSound(null, Player.getBlockPosBelowThatAffectsMyMovement(), PinkySounds.CHAOTICSURGE.value(),
                SoundSource.PLAYERS, 0.5f, .8f);

    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/chaotic_surge.png");
    }

}
