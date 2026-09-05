package com.mc3699.smparch.util.EntityHitbox;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.ibm.icu.impl.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityHitbox extends Entity {

    public int lifeTime = 200 * 5;

    public float speed = 1.0f;
    public float hitboxX = 1.0f;
    public float hitboxY = 1.0f;

    private int LivingTicks = 0;

    private List<LivingEntity> blacklistedEntities;
    private Vec3 target;

    private Function<Pair<EntityHitbox, LivingEntity>, Boolean> runOnHit = (
            Pair<EntityHitbox, LivingEntity> wasHit) -> {
        return false;
    };
    private Function<EntityHitbox, Boolean> runOnTick = (EntityHitbox hitbox) -> {
        return false;
    };

    private void checkCollision() {
        AABB hitbox = this.getBoundingBox();

        for (LivingEntity wasHit : this.level().getEntitiesOfClass(LivingEntity.class, hitbox)) {
            if ((blacklistedEntities != null && blacklistedEntities.contains(wasHit))) {
                continue;
            }

            if (runOnHit.apply(Pair.of(this, wasHit))) {
                this.discard();
                break;
            };
        }
    }

    private void startMomentum() {
        if (target ==null) { 
            return;
        }

        this.hasImpulse = true;
        Vec3 delta = target.subtract(this.position());
        Vec3 direction = delta.normalize();
        Vec3 velocity = direction.scale(speed);

        this.setDeltaMovement(velocity);
    }

    public EntityHitbox(EntityType<? extends EntityHitbox> type, Level level) {
        super(type, level);

        // this.setPos();
        // this.noPhysics = true;
        // target = givenTarget;
    }

    public EntityHitbox(EntityType<? extends EntityHitbox> type, Level level, Vec3 position, Vec3 givenTarget,
            Function<Pair<EntityHitbox, LivingEntity>, Boolean> onHit, Function<EntityHitbox, Boolean> onTick) {
        super(type, level);

        this.setPos(position);
        // this.noPhysics = true;
        target = givenTarget;
        runOnHit = onHit;
        runOnTick = onTick;
    }

    public EntityHitbox(EntityType<? extends EntityHitbox> type, Level level, List<LivingEntity> blacklisted,
            Vec3 givenTarget, Function<Pair<EntityHitbox, LivingEntity>, Boolean> onHit,
            Function<EntityHitbox, Boolean> onTick) {
        super(type, level);

        blacklistedEntities = blacklisted;
        // this.noPhysics = true;

        this.setPos(blacklisted.getLast().position());
        target = givenTarget;
        runOnHit = onHit;
        runOnTick = onTick;
    }

    public void ready() {
        startMomentum();

    }

    public void tick() {
        super.tick();

        this.setPos(this.position().add(this.getDeltaMovement()));

        if (runOnTick.apply(this) || this.level().isClientSide) {
            return;
        }

        if (this.target == null || ++this.LivingTicks >= lifeTime) {
            this.discard();
            return;
        }

        // this.move(MoverType.SELF, this.getDeltaMovement());

        checkCollision();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(hitboxX, hitboxY);
    }

    @Override
    protected void defineSynchedData(Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

}
