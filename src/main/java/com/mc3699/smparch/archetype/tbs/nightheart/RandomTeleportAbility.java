package com.mc3699.smparch.archetype.tbs.nightheart;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

public class RandomTeleportAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 2;
    }

    @Override
    public Component getName() {
        return Component.literal("Unstable Teleport");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft","textures/item/ender_pearl.png");
    }

    @Override
    public void execute(ServerPlayer player) {

        ServerLevel level = player.serverLevel();

        for(int i = 0; i < 16; ++i) {
            double d0 = player.getX() + (player.getRandom().nextDouble() - (double) 0.5F) * (double) 16.0F;
            double d1 = Mth.clamp(player.getY() + (double) (player.getRandom().nextInt(16) - 8), (double) level.getMinBuildHeight(), (double) (level.getMinBuildHeight() + ((ServerLevel) level).getLogicalHeight() - 1));
            double d2 = player.getZ() + (player.getRandom().nextDouble() - (double) 0.5F) * (double) 16.0F;
            if (player.isPassenger()) {
                player.stopRiding();
            }

            Vec3 vec3 = player.position();
            EntityTeleportEvent.ChorusFruit event = EventHooks.onChorusFruitTeleport(player, d0, d1, d2);

            if (player.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
                level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(player));

                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS);
                player.resetFallDistance();
                break;
            }
        }

        super.execute(player);
    }
}
