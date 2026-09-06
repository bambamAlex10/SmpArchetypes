package com.mc3699.smparch.archetype.emberflame65;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.registry.SMPSounds;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;

public class EmberBirdSong extends BaseAbility {
    private static final SoundEvent[] BIRD_SOUNDS = new SoundEvent[]{
            SMPSounds.BIRDSONG1.value(),
            SMPSounds.BIRDSONG2.value(),
            SMPSounds.BIRDSONG3.value(),
            SMPSounds.AMERICAN_WOODCOCK.value()
    };


    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(
                "smparch",
                "textures/ability_icon/bird_song.png"
        );
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    public Component getName() {
        return Component.literal("Birdsong");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        int randomSound = player.level().random.nextInt(BIRD_SOUNDS.length);
        if (randomSound == 1) {
            randomSound = player.level().random.nextInt(BIRD_SOUNDS.length);
        }
        SoundEvent randomBirdSong = BIRD_SOUNDS[randomSound];
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), randomBirdSong, player.getSoundSource(), 1.1f, 1.0f);
    }
}
