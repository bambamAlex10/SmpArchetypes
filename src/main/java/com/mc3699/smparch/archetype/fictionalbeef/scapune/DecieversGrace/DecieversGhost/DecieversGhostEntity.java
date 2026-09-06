package com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGhost;

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
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGraceAbility;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneSounds;
import com.mc3699.smparch.registry.SMPEntities;
import com.mc3699.smparch.registry.SMPParticles;
import com.mc3699.smparch.registry.SMPSounds;

public class DecieversGhostEntity extends LivingEntity {

    private static Random randomPitch = new Random();
    // Store the full GameProfile (including skin textures) in synched data
    private static final EntityDataAccessor<CompoundTag> GAME_PROFILE = SynchedEntityData
            .defineId(DecieversGhostEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<ItemStack> DATA_MAIN_HAND = SynchedEntityData
            .defineId(DecieversGhostEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_OFF_HAND = SynchedEntityData
            .defineId(DecieversGhostEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_FEET = SynchedEntityData
            .defineId(DecieversGhostEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_LEGS = SynchedEntityData
            .defineId(DecieversGhostEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_CHEST = SynchedEntityData
            .defineId(DecieversGhostEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_HEAD = SynchedEntityData
            .defineId(DecieversGhostEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> VISUAL_CROUCHING = SynchedEntityData
            .defineId(DecieversGhostEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIFE_TICKS = SynchedEntityData.defineId(DecieversGhostEntity.class,
            EntityDataSerializers.INT);

    private Player parentPlayer;

    private FakePlayer cachedFakePlayer;

    public DecieversGhostEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        // Ghost properties: no physics, no gravity, invulnerable, silent, no AI
        // this.noPhysics = false;
        // this.setNoGravity(false);
        this.noPhysics = false;
        this.setInvulnerable(false);
        this.setSilent(true);
        // this.setNoAi(true);
        this.getAttribute(Attributes.SCALE).setBaseValue(0.9375D);

    }

    public FakePlayer getPlayerRepresentation() {
        if (this.cachedFakePlayer == null && this.level() instanceof ServerLevel serverLevel) {
            // Generates a fully working ServerPlayer mapped inside this dimension
            this.cachedFakePlayer = FakePlayerFactory.get(serverLevel, getGameProfile());
        }

        if (this.cachedFakePlayer != null) {
            // Keep the fake player coordinates synchronized with the actual ghost entity
            // position
            this.cachedFakePlayer.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

        return this.cachedFakePlayer;
    }

    CompoundTag nbt = new CompoundTag();

    public static DecieversGhostEntity CreateClone(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        DecieversGhostEntity ghost = SMPEntities.DECIEVERSGHOST.get().create(level);

        ghost.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());

        ghost.yBodyRot = player.yBodyRot;
        ghost.yHeadRot = player.yHeadRot;
        ghost.xRotO = player.getXRot();
        ghost.yRotO = player.getYRot();
        ghost.attackAnim = player.attackAnim;
        ghost.swinging = player.swinging;
        ghost.swingingArm = player.swingingArm;
        ghost.setGameProfile(player.getGameProfile());
        ghost.parentPlayer = player;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ghost.setItemSlot(slot, player.getItemBySlot(slot).copy());
        }

        // level.addFreshEntity(ghost);

        ghost.setLockedMotion(player.getKnownMovement());
        ghost.setSprinting(player.isSprinting()); // optional, see below
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
        builder.define(LIFE_TICKS, DecieversGraceAbility.GhostDuration); // 0 means immortal (no timer)
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
        LivingEntity myself = this.self();
        if (myself.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(SMPParticles.QUEENBEE.get(), myself.getX(), myself.getY() + 1.0f, myself.getZ(),
                    100, 0.3, -0.3, 0.3, 0.01);
        } else {
            myself.level().addParticle(SMPParticles.QUEENBEE.get(), myself.getX(), myself.getY() + 1.0, myself.getZ(),
                    0.0D, 0.1D, 0.0D);
        }

        myself.level().playSound(null, myself.getBlockPosBelowThatAffectsMyMovement(), ScapuneSounds.QUEENBEE.value(),
                SoundSource.PLAYERS, 0.7f, randomPitch.nextFloat() * (1.2f - .8f) + 1f);
        Optional<UUID> Owner = myself.getData(ScapuneAttachments.GHOST_UUID);
        if (Owner.isPresent() && myself.level() instanceof ServerLevel server
                && server.getEntity(Owner.get()) instanceof Player player) {
            if (player.hasData(ScapuneAttachments.GHOST_UUID)) {
                player.removeData(ScapuneAttachments.GHOST_UUID);
            }
            if (player.hasEffect(MobEffects.INVISIBILITY)) {
                player.removeEffect(MobEffects.INVISIBILITY);
            }
        }

        if (a.getEntity() instanceof LivingEntity liver && !liver.equals(parentPlayer)) {
            liver.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 5 * 20, 0));
            liver.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5 * 20, 0));
        }

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
            if (!this.isCrouching()) {
                this.setCustomName(Component.literal(profile.getName()));
            } else {
                this.setCustomName(Component.literal(""));

            }
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
        if (this.level().isClientSide()) {
            if (this.isAlive() && this.shouldPlaySound()) {
                if (soundCooldown <= 0) {
                    this.level().playLocalSound(
                            this.getX(), this.getY(), this.getZ(),
                            ScapuneSounds.DECIVRSDECEPTION.getDelegate().value(),
                            SoundSource.NEUTRAL,
                            0.01F, 1.0F,
                            false);
                    soundCooldown = (20 * 5) + 10;
                } else {
                    soundCooldown--;
                }
            }
        } else {
            getPlayerRepresentation();
        }
        if (this.onGround()) {
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    public void setOwnerUUID(UUID uuid) {
        this.setData(ScapuneAttachments.GHOST_UUID.get(), Optional.of(uuid));
        this.syncData(ScapuneAttachments.GHOST_UUID);
    }

    public Optional<UUID> getOwnerUUID() {
        return this.getData(ScapuneAttachments.GHOST_UUID.get());
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        return super.hurt(source, super.getMaxHealth() * 10);
    }

    private Vec3 lockedMotion = Vec3.ZERO;

    public void setLockedMotion(Vec3 motion) {

        

        this.lockedMotion = new Vec3(motion.x, 0.0, motion.z); // horizontal only; gravity owns Y
    }

    @Override
    public void travel(Vec3 travelVector) {

        if (!this.level().isClientSide()) {
        Vec3 dm = this.getDeltaMovement();
        double dy = this.isNoGravity() ? dm.y : dm.y - 0.08;

        this.setDeltaMovement(this.lockedMotion.x, dy, this.lockedMotion.z);
        this.move(MoverType.SELF, this.getDeltaMovement());

        this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.98, 1.0));

        if (this.horizontalCollision) {
            this.lockedMotion = Vec3.ZERO; 
        }
        }

        this.calculateEntityAnimation( false);
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
        this.lockedMotion = new Vec3(tag.getDouble("LockedMX"), 0, tag.getDouble("LockedMZ"));

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
        tag.putDouble("LockedMX", lockedMotion.x);
        tag.putDouble("LockedMZ", lockedMotion.z);
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