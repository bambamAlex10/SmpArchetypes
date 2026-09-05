package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class VoidPresenceTracker implements INBTSerializable<CompoundTag> {
    private boolean isInside;
    private boolean wasInside;
    private BlockPos lastInsidePos;   // new field

    public VoidPresenceTracker() {}

    public boolean isInside() { return isInside; }
    public void setInside(boolean inside) { this.isInside = inside; }
    public boolean wasInside() { return wasInside; }
    public void updatePrevious() { this.wasInside = this.isInside; }

    public BlockPos getLastInsidePos() { return lastInsidePos; }
    public void setLastInsidePos(BlockPos pos) { this.lastInsidePos = pos; }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("isInside", this.isInside);
        tag.putBoolean("wasInside", this.wasInside);
        if (this.lastInsidePos != null) {
            tag.putLong("lastInsidePos", this.lastInsidePos.asLong());
            tag.putBoolean("hasLastPos", true);
        } else {
            tag.putBoolean("hasLastPos", false);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.isInside = tag.getBoolean("isInside");
        this.wasInside = tag.getBoolean("wasInside");
        if (tag.getBoolean("hasLastPos")) {
            this.lastInsidePos = BlockPos.of(tag.getLong("lastInsidePos"));
        } else {
            this.lastInsidePos = null;
        }
    }
}