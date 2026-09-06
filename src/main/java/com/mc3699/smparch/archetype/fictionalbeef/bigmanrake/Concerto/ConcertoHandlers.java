package com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.Concerto;

import java.rmi.AccessException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Random;

import javax.swing.text.html.parser.Entity;

import com.google.common.eventbus.Subscribe;
import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeSounds;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.ConcertoAbility;
import com.mc3699.smparch.registry.SMPAbilities;
import com.mc3699.smparch.registry.SMPSounds;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.mc3699.provenance.ProvenanceDataHandler;
import net.mc3699.provenance.ProvenanceRegistries;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.network.ProvNetworking;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import com.mojang.serialization.Codec;

@EventBusSubscriber(modid = SMPArch.MODID)
public class ConcertoHandlers {

    private static final ArrayDeque<Runnable> DEFERRED = new ArrayDeque<>();

    @SubscribeEvent
    public static void onServerTickPost(ServerTickEvent.Post event) {
        Runnable r;
        while ((r = DEFERRED.poll()) != null) {
            try {
                r.run();
            } catch (Exception e) {
                LogUtils.getLogger().error("Deferred concerto action failed", e);
            }
        }
    }

    public static final ResourceLocation SPEED_BUFF_ID = ResourceLocation.fromNamespaceAndPath("smparch", "bigmanaura");
    public static Random rand = new Random();
    public static float ConcertoSpeed = (1.0f / 12.0f);

    public static ResourceLocation insanityResource = ResourceLocation.parse("rake:insanity");
    public static ResourceKey<DamageType> Insanity = ResourceKey.create(Registries.DAMAGE_TYPE,
            insanityResource);

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        float CurrentDamage = event.getNewDamage();

        if (event.getEntity() instanceof Player bigmanrake && !ProvenanceDataHandler.getAbilities(bigmanrake).stream()
                .noneMatch(ability -> ability instanceof ConcertoAbility)
                && bigmanrake.hasData(BigManRakeAttachments.MOVEMENT)) {
            CurrentDamage = ((1f - (.2f * bigmanrake.getData(BigManRakeAttachments.MOVEMENT))) * CurrentDamage);
        }

        if (event.getSource().getEntity() instanceof LivingEntity dealer
                && dealer.hasData(BigManRakeAttachments.MOVEMENT)) {

            float Increase = 0;

            if ((dealer.getData(BigManRakeAttachments.MOVEMENT) / 10f) > 1f) {
                Increase += 1;
            }

            if ((dealer.getData(BigManRakeAttachments.MOVEMENT) / 10f) > 3f) {
                Increase += 1;
            }

            CurrentDamage = (CurrentDamage * (1 - (.15f * Increase)));
        }

        event.setNewDamage(CurrentDamage);
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        net.minecraft.world.entity.Entity source = event.getSource().getEntity();

        if ((source instanceof Player player) && ProvenanceDataHandler.getAbilities(player).stream()
                .anyMatch(ability -> ability instanceof ConcertoAbility)
                && source.hasData(BigManRakeAttachments.MOVEMENT)) {

            float CurrentData = source.getData(BigManRakeAttachments.MOVEMENT);
            float Total = (victim instanceof Player) ? 2f : (victim.getMaxHealth() * (1f / 2f) * .01f);

            System.out.println(Total);
            ;

            source.setData(BigManRakeAttachments.MOVEMENT,
                    CurrentData - (Total));
            source.syncData(BigManRakeAttachments.MOVEMENT);
        } else if (victim instanceof Player player && player instanceof ServerPlayer serverPlayer
                && ProvenanceDataHandler.getAbilities(player).stream()
                        .noneMatch(ability -> ability instanceof ConcertoAbility)
                && player.hasData(BigManRakeAttachments.MOVEMENT)) {

            serverPlayer.connection
                    .send(new ClientboundStopSoundPacket(BigManRakeSounds.MOVEMENT2CAP.getKey().location(),
                            SoundSource.PLAYERS));
            serverPlayer.connection
                    .send(new ClientboundStopSoundPacket(BigManRakeSounds.MOVEMENT1CAP.getKey().location(),
                            SoundSource.PLAYERS));
            serverPlayer.connection
                    .send(new ClientboundStopSoundPacket(BigManRakeSounds.MOVEMENT3CAP.getKey().location(),
                            SoundSource.PLAYERS));
            serverPlayer.connection
                    .send(new ClientboundStopSoundPacket(BigManRakeSounds.MOVEMENT4CAP.getKey().location(),
                            SoundSource.PLAYERS));
            serverPlayer.connection
                    .send(new ClientboundStopSoundPacket(BigManRakeSounds.MOVEMENT0CAP.getKey().location(),
                            SoundSource.PLAYERS));
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (ProvenanceDataHandler.getAbilities(player).stream()
                .noneMatch(ability -> ability instanceof ConcertoAbility)) {
            return;
        }

        if (!player.hasData(BigManRakeAttachments.MOVEMENT)) {
            return;
        }

        player.setData(BigManRakeAttachments.START_TICK, player.level().getGameTime() + (3 * 20));
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        Player player = event.getEntity();

        if (player.level().isClientSide) {
            return;
        }

        ServerPlayer ServerPlayer = (ServerPlayer) player;

        if (ProvenanceDataHandler.getAbilities(player).stream()
                .noneMatch(ability -> ability instanceof ConcertoAbility)
                || !player.hasData(BigManRakeAttachments.MOVEMENT)
                || !player.isAlive()) { // this
            return;
        }
        // is
        // for
        // concerto

        float imv = (player.getData(BigManRakeAttachments.MOVEMENT));
        float cmovement = player.getData(BigManRakeAttachments.MOVEMENT);

        long BeginningTick = player.getData(BigManRakeAttachments.START_TICK);

        if (BeginningTick == 0) {
            BeginningTick = player.level().getGameTime();
        }

        long timePassed = player.level().getGameTime() - BeginningTick;

        if (cmovement <= 0f) {
            player.removeData(BigManRakeAttachments.MOVEMENT);
            player.removeData(BigManRakeAttachments.CONCERTO_STAGE);
            player.syncData(BigManRakeAttachments.MOVEMENT);
            player.syncData(BigManRakeAttachments.CONCERTO_STAGE);
            return;
        }

        DamageSource witherSource = player.level().damageSources().wither();

        if (Math.floorMod(timePassed, (20 * 4)) == 40) { // every 4 seconds

            AABB area = player.getBoundingBox().inflate(18);
            float HealthGained = 0;

            for (net.minecraft.world.entity.Entity temp : player.level().getEntities(player, area)) {
                if (!(temp instanceof LivingEntity)) {
                    continue;
                }

                LivingEntity hitplr = (LivingEntity) temp;
                int withinMovement = 0;

                for (int i = 1; i <= imv; i++) {
                    if (player.position().distanceTo(hitplr.position()) > Math.sqrt((((2 + i) * (2 + i))))) {
                        continue;
                    }

                    final int UseThis = i;

                    DEFERRED.add(() -> {

                        player.level().registryAccess().registry(Registries.DAMAGE_TYPE).ifPresent(registry -> {
                            registry.getHolder(Insanity).ifPresentOrElse(holder -> {
                                hitplr.hurt(new DamageSource(holder, player), 1 * (1.5f * UseThis));
                            },
                                    () -> {
                                        hitplr.hurt(witherSource, 1 * (1.5f * UseThis));
                                    });
                        });

                        if (hitplr.getHealth() > 0f) {
                            player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                                    BigManRakeSounds.SYMCHORATK.value(), SoundSource.PLAYERS, 0.5f, 1f);
                        } else {
                            player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                                    BigManRakeSounds.SYMHEADBOMB.value(), SoundSource.PLAYERS, 0.5f, 1f);
                        }
                    });

                    HealthGained += 1;
                    withinMovement += 1;

                }

                withinMovement = Math.clamp(withinMovement, 0, 4);

                if (withinMovement > 0) {
                    final int last = 12 - (withinMovement * 2);

                    if (rand.nextFloat(last / 4) <= 1) {
                        Optional<Reference<MobEffect>> insanityEffect = BuiltInRegistries.MOB_EFFECT
                                .getHolder(insanityResource);
                        insanityEffect.ifPresent(effectHolder -> {
                            MobEffectInstance instance = new MobEffectInstance(effectHolder,
                                    last * 20, 0);

                            hitplr.addEffect(instance);
                        });
                    }
                }

                hitplr.setData(BigManRakeAttachments.MOVEMENT, (float) (withinMovement) * 10f);

            }

            player.heal(HealthGained);

            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);

            if (imv >= 2f && !movementSpeed.hasModifier(SPEED_BUFF_ID)) {
                movementSpeed.addTransientModifier(
                        new AttributeModifier(SPEED_BUFF_ID, .15, Operation.ADD_MULTIPLIED_BASE));
            } else {
                movementSpeed.removeModifier(SPEED_BUFF_ID);
            }

        }

        if ((Math.floorMod(timePassed, (20 * 6)) == 0)) { // every
                                                          // 6
                                                          // seconds

            Boolean[] movementCompleted = player.getData(BigManRakeAttachments.CONCERTO_STAGE.get());

            cmovement = cmovement + ConcertoSpeed;
            player.setData(BigManRakeAttachments.MOVEMENT, Math.min(cmovement, 4.3f));
            player.syncData(BigManRakeAttachments.MOVEMENT);
            if (cmovement >= 4.3f) {
                if (!movementCompleted[4]) {
                    player.setData(BigManRakeAttachments.MOVEMENT, 4.3f);

                    BeginningTick = player.level().getGameTime();
                    movementCompleted[4] = true;
                    player.setData(BigManRakeAttachments.CONCERTO_STAGE.get(), movementCompleted);
                    player.hurt(player.damageSources().fellOutOfWorld(), 600f);
                }
            } else if (cmovement >= 3.0f) {
                if (!movementCompleted[3]) {
                    player.setData(BigManRakeAttachments.MOVEMENT, 3.0f);

                    BeginningTick = player.level().getGameTime();
                    movementCompleted[3] = true;
                }
            } else if (cmovement >= 2.0f) {
                if (!movementCompleted[2]) {
                    player.setData(BigManRakeAttachments.MOVEMENT, 2.0f);

                    BeginningTick = player.level().getGameTime();
                    movementCompleted[2] = true;
                }

            } else if (cmovement >= 1.0f) {
                if (!movementCompleted[1]) {
                    player.setData(BigManRakeAttachments.MOVEMENT, 1.0f);

                    BeginningTick = player.level().getGameTime();
                    movementCompleted[1] = true;
                }

            } else if (cmovement > (0.0f + (1f / 12f))) {
                if (movementCompleted == null || movementCompleted[0] == null || !movementCompleted[0]) {
                    if (movementCompleted == null || movementCompleted[0] == null) {
                        movementCompleted = new Boolean[5];
                        java.util.Arrays.fill(movementCompleted, false);
                    }
                    player.setData(BigManRakeAttachments.MOVEMENT, 0.0f + (1f / 12f));
                    BeginningTick = player.level().getGameTime();
                    movementCompleted[0] = true;
                }
            } else if (cmovement == 0.01f) {
                BeginningTick = player.level().getGameTime();
            }

            if (BeginningTick != player.getData(BigManRakeAttachments.START_TICK)) {
                player.setData(BigManRakeAttachments.START_TICK, BeginningTick);
            }
            player.setData(BigManRakeAttachments.CONCERTO_STAGE.get(), movementCompleted);
        }

        if (player.hasData(BigManRakeAttachments.MOVEMENT) && ProvenanceDataHandler.getAbilities(player).stream()
                .anyMatch(ability -> ability instanceof ConcertoAbility)) {
            timePassed = player.level().getGameTime() - BeginningTick;

            if (cmovement >= 4.3f) {
                ServerPlayer.connection
                        .send(new ClientboundStopSoundPacket(BigManRakeSounds.MOVEMENT4CAP.getKey().location(),
                                SoundSource.PLAYERS));
            } else if (cmovement >= 3.0f) {
                if (Math.floorMod(timePassed, (20 * 13)) == 0) {
                    player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                            BigManRakeSounds.MOVEMENT4CAP.value(), SoundSource.PLAYERS, 1f, 1f);
                }
                ServerPlayer.connection
                        .send(new ClientboundStopSoundPacket(BigManRakeSounds.MOVEMENT3CAP.getKey().location(),
                                SoundSource.PLAYERS));

            } else if (cmovement >= 2.0f) {
                if (Math.floorMod(timePassed, (20 * 5)) == 0) {
                    player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                            BigManRakeSounds.MOVEMENT3CAP.value(), SoundSource.PLAYERS, 0.2f, 1f);
                }

                ServerPlayer.connection
                        .send(new ClientboundStopSoundPacket(BigManRakeSounds.MOVEMENT2CAP.getKey().location(),
                                SoundSource.PLAYERS));
            } else if (cmovement >= 1.0f) {
                if (Math.floorMod(timePassed, (20 * 16)) == 0) {
                    player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                            BigManRakeSounds.MOVEMENT2CAP.value(), SoundSource.PLAYERS, 1f, 1f);
                }

                ServerPlayer.connection
                        .send(new ClientboundStopSoundPacket(BigManRakeSounds.MOVEMENT1CAP.getKey().location(),
                                SoundSource.PLAYERS));
            } else if (cmovement >= ((0.0f + (.5f / 12f)))) {
                if (Math.floorMod(timePassed, (20 * 22)) == 0) {
                    player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                            BigManRakeSounds.MOVEMENT1CAP.value(), SoundSource.PLAYERS, 1f, 1f);
                }
            }
        }

    }
}
