package com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.WaxenPinion;

import java.time.Duration;
import java.time.Instant;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.AccelerandoAbility;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Attachments;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Sounds;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.WaxenPinionActive;
import com.mc3699.smparch.util.DelayedTaskHandler;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.Provenance;
import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SMPArch.MODID)
public class WaxenPinionHandlers {
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide() || !player.hasData(FlameK1ng_Attachments.HITBOXHIT)) return;

        AABB hitbox = player.getBoundingBox();

        for (LivingEntity gotHit : player.level().getEntitiesOfClass(LivingEntity.class, hitbox)) {
            if (gotHit.equals(player)) {
                continue;
            }

            WaxenPinionActive.hitEntity(gotHit,player);
            player.removeData(BigManRakeAttachments.HITBOXHIT);

            break;
        }
    }
    
}
