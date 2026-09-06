package com.mc3699.smparch.archetype.tbs.growth;

import net.mc3699.provenance.ability.foundation.ToggleAbility;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;

public class NatureGiftAbility extends ToggleAbility {

    int foodTimer = 0;

    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public Component getName() {
        return Component.literal("Nature's Gift");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        ServerLevel serverLevel = serverPlayer.serverLevel();
        return serverLevel.dimension().equals(ServerLevel.OVERWORLD);
    }

    @Override
    public void tick(ServerPlayer serverPlayer) {
        super.tick(serverPlayer);
        if(serverPlayer.getBlockStateOn().is(Blocks.GRASS_BLOCK) || serverPlayer.getBlockStateOn().is(Blocks.DIRT)) {
            foodTimer++;
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 1));

            if(foodTimer > 40 && serverPlayer.getRandom().nextBoolean())
            {
                foodTimer = 0;
                int foodLevel = serverPlayer.getFoodData().getFoodLevel();
                serverPlayer.getFoodData().setFoodLevel(foodLevel-1);
            }

        }

    }
}
