package com.mc3699.smparch.archetype.tbs.aidenman;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class PhotosynthesisAbility extends AmbientAbility {

    int regenTimer = 0;

    @Override
    public void tick(ServerPlayer serverPlayer) {

        regenTimer++;
        if(regenTimer > 250)
        {
            regenTimer = 0;
            if(serverPlayer.getFoodData().needsFood())
            {
                int currentFood = serverPlayer.getFoodData().getFoodLevel();
                float currentSaturation = serverPlayer.getFoodData().getSaturationLevel();

                serverPlayer.getFoodData().setFoodLevel(currentFood + 1);
                serverPlayer.getFoodData().setSaturation(currentSaturation + 2);
            }
        }

    }

    @Override
    public Component getName() {
        return Component.literal("Photosynthesis");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        ServerLevel level = serverPlayer.serverLevel();
        boolean isDay = level.getDayTime() % 24000L < 12000L;
        BlockPos pos = serverPlayer.blockPosition();
        return level.canSeeSky(pos) && isDay;
    }
}
