package com.mc3699.smparch.archetype.tbs.growth;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class NatureSightAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 4;
    }

    @Override
    public Component getName() {
        return Component.literal("Nature's Sight");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);

        int foodLevel = player.getFoodData().getFoodLevel();
        ServerLevel serverLevel = player.serverLevel();
        player.getFoodData().setFoodLevel(foodLevel - 10);

        AABB searchArea = new AABB(player.getBlockPosBelowThatAffectsMyMovement()).inflate(16);
        List<Player> players = serverLevel.getEntitiesOfClass(Player.class, searchArea);
        players.forEach(p -> {
            p.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 1));
        });
    }
}
