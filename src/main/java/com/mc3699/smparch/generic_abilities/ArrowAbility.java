package com.mc3699.smparch.generic_abilities;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

//i made this as a test for eyae arch but figured why not keep it since the code is there
public class ArrowAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 1f;
    }

    @Override
    public int getCooldown() { return 120; }

    @Override
    public Component getName() {
        return Component.literal("Fire Arrow");
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);

        Vec3 lookVec = player.getLookAngle();
        SpectralArrow arrow = new SpectralArrow(EntityType.SPECTRAL_ARROW, player.level());
        arrow.setOwner(player);
        arrow.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
        arrow.shoot(lookVec.x, lookVec.y, lookVec.z, 3.0f, 0.0f);
        arrow.setBaseDamage(2.0);
        arrow.setCritArrow(true);
        arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;

        player.level().addFreshEntity(arrow);
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft","textures/item/arrow.png");
    }
}