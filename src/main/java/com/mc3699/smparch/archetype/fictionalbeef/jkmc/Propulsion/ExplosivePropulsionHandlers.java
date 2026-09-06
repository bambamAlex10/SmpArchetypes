package com.mc3699.smparch.archetype.fictionalbeef.jkmc.Propulsion;

import java.util.HashSet;
import java.util.Set;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.BloodofCovenant.BloodofCovenantHandlers;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.Brimstone.BrimstonePassive;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.JkmcAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneAttachments;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SMPArch.MODID)
public class ExplosivePropulsionHandlers {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        float currentValue = event.getEntity().hasData(JkmcAttachments.JKMC_DYINGMOMENTUM)
                ? event.getEntity().getData(JkmcAttachments.JKMC_DYINGMOMENTUM)
                : 0;

        if (currentValue <= 0 || event.getEntity().onGround() || event.getEntity().isDeadOrDying()) {
            if (event.getEntity().hasData(JkmcAttachments.JKMC_DYINGMOMENTUM)) {
                event.getEntity().removeData(JkmcAttachments.JKMC_DYINGMOMENTUM);
            }
            return;
        }

        currentValue -= (currentValue+4.0f)*(1.0f/20.0f);

        LogUtils.getLogger().debug(currentValue+"");

        event.getEntity().setData(JkmcAttachments.JKMC_DYINGMOMENTUM,currentValue);

    }
}
