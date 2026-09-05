package com.mc3699.smparch.archetype.tbs.john_ultrakill;

import com.mc3699.smparch.registry.SMPAbilities;
import com.mc3699.smparch.registry.SMPSounds;
import net.mc3699.provenance.ProvenanceDataHandler;
import net.mc3699.provenance.ability.foundation.ToggleAbility;
import net.mc3699.provenance.util.ProvKeymappings;
import net.mc3699.provenance.util.ProvScheduler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import static net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR;

public class UltrakillSlamAbility extends ToggleAbility {

    private int fallTime = 0;

    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public Component getName() {
        return Component.literal("Potential Energy Storage System");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return !serverPlayer.onGround();
    }

    @Override
    public void execute(ServerPlayer player) {
        this.fallTime = 0;
        super.execute(player);
    }

    @Override
    public void tick(ServerPlayer serverPlayer) {
        ServerLevel serverLevel = serverPlayer.serverLevel();
        fallTime++;
        if(!serverPlayer.onGround()) {
            serverPlayer.setDeltaMovement(0, -3f, 0);
            serverPlayer.hurtMarked = true;


            if (fallTime % 20 == 0) {
                serverLevel.playSound(null, serverPlayer.getBlockPosBelowThatAffectsMyMovement().above(1), SMPSounds.SLAM_FALL.value(), SoundSource.PLAYERS, 1, 0.8f + (serverLevel.random.nextFloat() * 0.5f));
            }
        }
    }

    @Override
    public void backgroundTick(ServerPlayer serverPlayer) {
        super.backgroundTick(serverPlayer);

        if(fallTime > 0 && serverPlayer.onGround())
        {
            triggerImpact(serverPlayer);
        }
    }

    private void triggerImpact(ServerPlayer player) {
        ServerLevel serverLevel = player.serverLevel();

        serverLevel.playSound(null, player.getBlockPosBelowThatAffectsMyMovement().above(1), SoundEvents.PLAYER_BIG_FALL, SoundSource.PLAYERS, 1, 1f + (serverLevel.random.nextFloat() * 0.5f));

        if(fallTime > 3) {
            serverLevel.explode(
                    player, null, EXPLOSION_DAMAGE_CALCULATOR,
                    player.getX(), player.getY(), player.getZ(),
                    20.0F, false,
                    Level.ExplosionInteraction.TRIGGER,
                    ParticleTypes.GUST_EMITTER_LARGE,
                    ParticleTypes.GUST_EMITTER_LARGE,
                    SoundEvents.WIND_CHARGE_BURST
            );
        }

        fallTime = 0;
        player.fallDistance = 0;



        ProvenanceDataHandler.disableAbilityInstance(player, SMPAbilities.ULTRAKILL_SLAM.get());
    }

}
