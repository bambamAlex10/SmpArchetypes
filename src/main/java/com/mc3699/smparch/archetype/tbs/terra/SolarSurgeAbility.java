package com.mc3699.smparch.archetype.tbs.terra;

import com.mc3699.smparch.SMPArch;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.util.ProvScheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;

public class SolarSurgeAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        ServerLevel serverLevel = player.serverLevel();


        LightningBolt newBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, serverLevel);
        newBolt.setPos(player.position());
        newBolt.setVisualOnly(true);
        serverLevel.addFreshEntity(newBolt);

        ProvScheduler.schedule(10, () -> {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60 * 20, 1));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60 * 20, 0));
        });
    }

    @Override
    public Component getName() {
        return Component.literal("Solar Surge");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return serverPlayer.serverLevel().isDay();
    }

    @Override
    public int getCooldown() {
        return  360 * 20;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/solar_surge.png");
    }
}
