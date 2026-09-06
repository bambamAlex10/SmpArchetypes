package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep.VoidEcho;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.swing.text.html.parser.Entity;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneSounds;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStepAbility;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioSounds;
import com.mc3699.smparch.registry.SMPEntities;
import com.mc3699.smparch.registry.SMPParticles;
import com.mc3699.smparch.registry.SMPSounds;

public class VoidEchoEntity extends LivingEntity {
    private static final EntityDataAccessor<Integer> SOLID_COLOR = SynchedEntityData.defineId(
            VoidEchoEntity.class, EntityDataSerializers.INT);

    private static Random randomPitch = new Random();
    // Store the full GameProfile (including skin textures) in synched data
    private static final EntityDataAccessor<CompoundTag> GAME_PROFILE = SynchedEntityData
            .defineId(VoidEchoEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<ItemStack> DATA_MAIN_HAND = SynchedEntityData
            .defineId(VoidEchoEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_OFF_HAND = SynchedEntityData
            .defineId(VoidEchoEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_FEET = SynchedEntityData
            .defineId(VoidEchoEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_LEGS = SynchedEntityData
            .defineId(VoidEchoEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_CHEST = SynchedEntityData
            .defineId(VoidEchoEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_HEAD = SynchedEntityData
            .defineId(VoidEchoEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> VISUAL_CROUCHING = SynchedEntityData
            .defineId(VoidEchoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIFE_TICKS = SynchedEntityData.defineId(VoidEchoEntity.class,
            EntityDataSerializers.INT);

    public VoidEchoEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        // Ghost properties: no physics, no gravity, invulnerable, silent, no AI
        this.noPhysics = false;
        this.setNoGravity(false);
        this.setInvulnerable(false);
        this.setSilent(true);
        // this.setNoAi(true);
        this.getAttribute(Attributes.SCALE).setBaseValue(0.9375D);

    }

    CompoundTag nbt = new CompoundTag();

    public static VoidEchoEntity CreateClone(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        VoidEchoEntity ghost = SMPEntities.VOIDECHO.get().create(level);

        ghost.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());

        ghost.yBodyRot = player.yBodyRot;
        ghost.yHeadRot = player.yHeadRot;
        ghost.xRotO = player.getXRot();
        ghost.yRotO = player.getYRot();
        ghost.attackAnim = player.attackAnim;
        ghost.swinging = player.swinging;
        ghost.swingingArm = player.swingingArm;
        ghost.setGameProfile(player.getGameProfile());

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ghost.setItemSlot(slot, player.getItemBySlot(slot).copy());
        }

        level.addFreshEntity(ghost);

        return ghost;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(GAME_PROFILE, new CompoundTag());
        builder.define(DATA_MAIN_HAND, ItemStack.EMPTY);
        builder.define(DATA_OFF_HAND, ItemStack.EMPTY);
        builder.define(DATA_FEET, ItemStack.EMPTY);
        builder.define(DATA_LEGS, ItemStack.EMPTY);
        builder.define(DATA_CHEST, ItemStack.EMPTY);
        builder.define(DATA_HEAD, ItemStack.EMPTY);
        builder.define(VISUAL_CROUCHING, false); // new
        builder.define(LIFE_TICKS, VoidStepAbility.GhostDuration); // 0 means immortal (no timer)
        builder.define(SOLID_COLOR, 0xFF000000);
    }

    public void setLifeTicks(int ticks) {
        this.entityData.set(LIFE_TICKS, ticks);
    }

    public int getLifeTicks() {
        return this.entityData.get(LIFE_TICKS);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void die(DamageSource a) {

        super.die(a);
    }

    public void setGameProfile(GameProfile profile) {
        // Ensure the profile has a UUID. If not, generate a dummy one.
        if (profile.getId() == null) {
            profile = new GameProfile(UUID.randomUUID(), profile.getName());
        }

        ExtraCodecs.GAME_PROFILE.encodeStart(NbtOps.INSTANCE, profile)
                .resultOrPartial(error -> LogUtils.getLogger().error("Failed to encode GameProfile: {}", error))
                .ifPresent(tag -> this.entityData.set(GAME_PROFILE, (CompoundTag) tag));

        if (profile.getName() != null && !profile.getName().isEmpty()) {
            this.setCustomName(Component.literal(""));
            this.setCustomNameVisible(true);
        }
    }

    public GameProfile getGameProfile() {
        CompoundTag tag = this.entityData.get(GAME_PROFILE);

        if (tag.isEmpty()) {
            return new GameProfile(UUID.randomUUID(), ""); // fallback with random UUID
        }

        return ExtraCodecs.GAME_PROFILE.decode(NbtOps.INSTANCE, tag)
                .resultOrPartial(error -> LogUtils.getLogger().error("Failed to decode GameProfile: {}", error))
                .map(Pair::getFirst)
                .filter(profile -> profile.getId() != null) // safety filter
                .orElseGet(() -> {
                    // Fallback: read name from tag, assign a fresh UUID
                    String name = tag.contains("Name") ? tag.getString("Name") : "";
                    return new GameProfile(UUID.randomUUID(), name);
                });
    }

    // --- Completely freeze the ghost, disable all interaction ---

    // Set this from the spawn code
    public void setVisualCrouching(boolean crouching) {
        this.entityData.set(VISUAL_CROUCHING, crouching);
    }

    // Override to force the model into crouching pose
    @Override
    public boolean isCrouching() {
        return this.entityData.get(VISUAL_CROUCHING);
    }

    private int soundCooldown = 0;

    @Override
    public void tick() {
        super.tick();

        int ticks = getLifeTicks();
        if (ticks > 0) {
            setLifeTicks(ticks - 1);
            if (getLifeTicks() <= 0) {
                LivingEntity myself = this.self();
                if (myself.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(SMPParticles.MAJIK.get(), myself.getX(), myself.getY() + 1.0f,
                            myself.getZ(),
                            30, 0.3, -0.3, 0.3, 0.01);
                } else {
                    myself.level().addParticle(SMPParticles.MAJIK.get(), myself.getX(), myself.getY() + 1.0,
                            myself.getZ(),
                            0.0D, 0.1D, 0.0D);
                }

                myself.level().playSound(null, myself.getBlockPosBelowThatAffectsMyMovement(),
                        YkorioSounds.MAJIK.value(),
                        SoundSource.PLAYERS, 0.5f, 1.2f);
                Optional<UUID> Owner = myself.getData(YkorioAttachments.ECHO_UUID);
                if (Owner.isPresent() && myself.level() instanceof ServerLevel server
                        && server.getEntity(Owner.get()) instanceof Player player) {
                    if (player.hasData(YkorioAttachments.ECHO_UUID)) {
                        player.removeData(YkorioAttachments.ECHO_UUID);
                    }
                }
                this.discard();
            }
        }

        if (this.onGround()) {
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    public void setOwnerUUID(UUID uuid) {
        this.setData(YkorioAttachments.ECHO_UUID.get(), Optional.of(uuid));
        this.syncData(YkorioAttachments.ECHO_UUID);
    }

    public Optional<UUID> getOwnerUUID() {
        return this.getData(YkorioAttachments.ECHO_UUID.get());
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        return super.hurt(source, super.getMaxHealth() * 10);
    }

    @Override
    public void travel(Vec3 travelVector) {
        // Do nothing – ignore any movement logic
    }

    @Override
    public boolean canTakeItem(net.minecraft.world.item.ItemStack stack) {
        return false;
    }

    @Override
    public Component getDisplayName() {
        return this.getCustomName() != null ? this.getCustomName() : super.getDisplayName();
    }

    // allow name rendering (the default LivingEntity behaviour)
    // @Override
    // protected boolean shouldShowName() {
    // return false;
    // }

    // Save / load the GameProfile
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("PlayerProfile")) {
            this.setGameProfile(ExtraCodecs.GAME_PROFILE.parse(NbtOps.INSTANCE, tag).getOrThrow());
        }
    }

    public boolean isVisualCrouching() {
        return this.entityData.get(VISUAL_CROUCHING);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ExtraCodecs.GAME_PROFILE.encodeStart(NbtOps.INSTANCE, this.getGameProfile());
    }

    @Override
    public boolean shouldShowName() {
        if (this.isVisualCrouching()) {
            return false;
        }
        return this.hasCustomName();
    }

    // 3. Implement setItemSlot and getItemBySlot
    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        switch (slot) {
            case MAINHAND -> this.entityData.set(DATA_MAIN_HAND, stack);
            case OFFHAND -> this.entityData.set(DATA_OFF_HAND, stack);
            case FEET -> this.entityData.set(DATA_FEET, stack);
            case LEGS -> this.entityData.set(DATA_LEGS, stack);
            case CHEST -> this.entityData.set(DATA_CHEST, stack);
            case HEAD -> this.entityData.set(DATA_HEAD, stack);
        }
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return switch (slot) {
            case MAINHAND -> this.entityData.get(DATA_MAIN_HAND);
            case OFFHAND -> this.entityData.get(DATA_OFF_HAND);
            case FEET -> this.entityData.get(DATA_FEET);
            case LEGS -> this.entityData.get(DATA_LEGS);
            case CHEST -> this.entityData.get(DATA_CHEST);
            case HEAD -> this.entityData.get(DATA_HEAD);
            default -> this.entityData.get(DATA_MAIN_HAND);
        };
    }

    @Override
    public Iterable<ItemStack> getHandSlots() {
        return List.of(getItemBySlot(EquipmentSlot.MAINHAND), getItemBySlot(EquipmentSlot.OFFHAND));
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return List.of(
                getItemBySlot(EquipmentSlot.FEET),
                getItemBySlot(EquipmentSlot.LEGS),
                getItemBySlot(EquipmentSlot.CHEST),
                getItemBySlot(EquipmentSlot.HEAD));
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    private boolean shouldPlaySound() {
        // e.g., return this.isAggressive() || this.hasTarget();
        return true;
    }
}