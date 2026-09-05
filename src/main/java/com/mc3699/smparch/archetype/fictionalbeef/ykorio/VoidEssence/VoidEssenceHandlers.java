package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidEssence;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioArchetype;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioSounds;
import com.mojang.logging.LogUtils;

@EventBusSubscriber(modid = SMPArch.MODID)
public class VoidEssenceHandlers {
    public static float damageBlocked = .8f;

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity beingAttacked = event.getEntity();
        Entity attacking = event.getSource().getEntity();

        if (!(beingAttacked instanceof Player attackedPlayer)
                || !attackedPlayer.hasData(YkorioAttachments.HIT_DEFENSE)) {
            return;
        }

        float currentDamage = event.getNewDamage();
        currentDamage = currentDamage * (1f - damageBlocked);
        event.setNewDamage(currentDamage);

        int currentCount = attackedPlayer.getData(YkorioAttachments.HIT_DEFENSE);
        currentCount -= 1;
        if (currentCount <= 0) {
            if (currentCount == 1) {

                attackedPlayer.level().playSound(null, attackedPlayer.getOnPos(), YkorioSounds.SHIELDSHATTER.value(),
                        SoundSource.PLAYERS, 1f, 1f);
            }

            attackedPlayer.removeData(YkorioAttachments.HIT_DEFENSE);
            attackedPlayer.syncData(YkorioAttachments.HIT_DEFENSE);
        } else {

            attackedPlayer.level().playSound(null, attackedPlayer.getOnPos(), YkorioSounds.SHIELDHIT.value(),
                    SoundSource.PLAYERS, 1f, 1f);
            attackedPlayer.setData(YkorioAttachments.HIT_DEFENSE, currentCount);
            attackedPlayer.syncData(YkorioAttachments.HIT_DEFENSE);
        }

    }
}
