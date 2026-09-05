package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock.Registration;

import javax.annotation.Nullable;

import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock.VoidPresenceBlockEntity;
import com.mc3699.smparch.registry.SMPBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoidPresenceBlock extends Block implements EntityBlock {


    public VoidPresenceBlock(BlockBehaviour.Properties properties) {
        super(properties);


    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
    }
    public boolean isThatMe(BlockState state) {
        return state.is(this) || state.is(SMPBlocks.VOIDBLOCK);
    }

    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty(); // Removes the selection outline entirely
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 15; // blocks all light, like a solid block
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return false; // stops skylight from passing through
    }

    // 2. Fix the newBlockEntity method to return your custom BlockEntity
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VoidPresenceBlockEntity(pos, state);
    }


    @Override
    @Deprecated
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (isThatMe(adjacentBlockState)) {
        return true;
        }
        return super.skipRendering(state, adjacentBlockState, side);
    }

    // 3. Add the ticker method so your tick() function inside the BlockEntity
    // actually runs
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        // Only run ticking logic on the server side
        return level.isClientSide ? null : (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof VoidPresenceBlockEntity voidBe) {
                voidBe.tick();
            }
        };
    }

}
