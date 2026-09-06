package com.mc3699.smparch.archetype.fictionalbeef.jkmc.OnDeath;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.JkmcAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.RezeDeathPassive;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.ExplosionPassive.ExplosivePassiveHandlers;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = SMPArch.MODID)
public class RezeDeathEvent {
    public static void createScaledExplosion(Level level, ServerPlayer source, Vec3 pos,
                                             float chargeValue, float maxCharge, float maxPower) {
        if (level.isClientSide) return; // only server

        float percent = Math.clamp(chargeValue / maxCharge, 0f, 1f);
        float power = percent * maxPower;
        if (power <= 0.0f) return;

        level.explode(
            source,                                     
            null,                                       
            null,                                        
            pos.x, pos.y, pos.z,                         
            power,                                      
            false,                                       
            ExplosionInteraction.TNT                                 
        );
    }
    
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity dead = event.getEntity();

        if (!(dead instanceof ServerPlayer deadPlayer)) {
            return;
        }

        if (!deadPlayer.hasData(JkmcAttachments.JKMC_CHARGE)) {
            return;
        }

        float currentCharge = deadPlayer.getData(JkmcAttachments.JKMC_CHARGE);
        float maxCharge = ExplosivePassiveHandlers.maxCharges * ExplosivePassiveHandlers.oneMeter;

        deadPlayer.removeData(JkmcAttachments.JKMC_CHARGE);

        if (ProvenanceDataHandler.getAmbientAbilities(deadPlayer).stream()
                .noneMatch(ability -> ability instanceof RezeDeathPassive)) {
            return;
        }

        createScaledExplosion(deadPlayer.level(), deadPlayer, deadPlayer.getPosition(1.0f), currentCharge, maxCharge, 8);

    }
}
