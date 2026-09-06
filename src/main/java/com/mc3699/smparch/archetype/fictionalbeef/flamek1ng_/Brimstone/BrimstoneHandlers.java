package com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.Brimstone;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.BrimstonePassive;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Attachments;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Sounds;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep.VoidStepPayload;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.Provenance;
import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = SMPArch.MODID)
public class BrimstoneHandlers {
    @SubscribeEvent
    public static void onLivingAttack(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        Entity source = event.getSource().getEntity();

        if ((entity instanceof Player player) && (ProvenanceDataHandler.getAmbientAbilities(player).stream()
                .anyMatch(ability -> ability instanceof BrimstonePassive))) {
            if (!event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_FIRE)
                    && !event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)) {
                return;
            }

            player.heal(event.getNewDamage());
            event.setNewDamage(0f);
        } else if ((source instanceof Player playerHurting) && (entity instanceof LivingEntity hurting)
                && (ProvenanceDataHandler.getAmbientAbilities(playerHurting).stream()
                        .anyMatch(ability -> ability instanceof BrimstonePassive))) {
            if (!hurting.isOnFire()) {
                return;
            }

            float newHurt = event.getNewDamage() * 1.5f;
            event.setNewDamage(newHurt);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }
        if (ProvenanceDataHandler.getAmbientAbilities(player).stream()
                .noneMatch(ability -> ability instanceof BrimstonePassive)) {
            return;
        }
        if (entity.isOnFire()) {
            entity.clearFire();
        }
    }

    @SubscribeEvent
    public static void playerDeath(LivingDeathEvent Event) {
        if (!(Event.getEntity() instanceof Player player)) {
            return;
        }

        if (ProvenanceDataHandler.getAmbientAbilities(player).stream().noneMatch(ability -> ability instanceof BrimstonePassive)) {
            return;
        }

        player.level().playSound(null,
                player.getBlockPosBelowThatAffectsMyMovement(),
                FlameK1ng_Sounds.DEATHEFFECT.value(),
                SoundSource.PLAYERS, 1f, 1f);

    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        // Bump the version string if you ever change the payload's fields.
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                BrimstonePayload.TYPE,
                BrimstonePayload.CODEC,
                BrimstoneHandlers::renderCoinFlip);
    }

    private static void renderCoinFlip(BrimstonePayload payload, IPayloadContext context) {
        BrimstoneUI.play(payload.playerId(), payload.result());
    }

    public static boolean flipCoin(Player player) {
        float middleHealth = player.getMaxHealth() / 2f;

        float maximum = ((player.getMaxHealth() - middleHealth) + 20f);
        float current = (player.getHealth() - middleHealth) + ((player.getFoodData().getSaturationLevel() - 10f)
                + (((float) player.getFoodData().getFoodLevel()) - 10f));

        float multiplier = Math.clamp((Math.min(current / maximum, 1) * .45f), -.45f, .45f) + .5f;

        float flush = player.level().getRandom().nextFloat();

        boolean heads = multiplier >= flush;

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                new BrimstonePayload(player.getUUID(), heads));

        player.level().playSound(null,
                player.getBlockPosBelowThatAffectsMyMovement(),
                FlameK1ng_Sounds.STARTCOINFLIP.value(),
                SoundSource.PLAYERS, .4f, 1f);
                
        return heads;

    }

}
