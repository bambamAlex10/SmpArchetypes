package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock.Registration;

import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidPresenceAbility;
import com.mc3699.smparch.registry.SMPBlocks;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoidBlock extends Block {

    public static final BooleanProperty[] DIRECTIONS = {
            BooleanProperty.create("down"),
            BooleanProperty.create("up"),
            BooleanProperty.create("north"),
            BooleanProperty.create("south"),
            BooleanProperty.create("west"),
            BooleanProperty.create("east")
    };

    public VoidBlock(BlockBehaviour.Properties properties) {
        super(properties);

        int num = 0;

        for (Direction Cardinal : Direction.values()) {
            this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTIONS[num], false));
            num += 1;
        }
    }

    public boolean isThatMe(BlockState state) {
        return state.is(this) || state.is(SMPBlocks.VOIDPRESENCEBLOCK);
    }

    // this.registerDefaultState(this.stateDefinition.any().setValue(BOTTOM,
    // false));
    // this.registerDefaultState(this.stateDefinition.any().setValue(TOP, false));
    // }

    // @Override
    // protected void createBlockStateDefinition(StateDefinition.Builder<Block,
    // BlockState> builder) {
    // builder.add(BOTTOM);
    // builder.add(TOP);
    // Direction.
    // }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        int num = 0;

        for (Direction Cardinal : Direction.values()) {
            builder.add(DIRECTIONS[num]);
            num += 1;
        }
    }

    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        updateBottomState(level, pos, state);

        int num = 0;

        for (Direction Cardinal : Direction.values()) {
            BlockPos relPos = pos.relative(Cardinal);
            BlockState relState = level.getBlockState(relPos);

            if (relState.is(this)) {
                updateBottomState(level, relPos, relState);
            }

            num += 1;
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {

        int num = 0;

        for (Direction Cardinal : Direction.values()) {
            if (!isThatMe(state)) {
                BlockPos relPos = pos.relative(Cardinal);
                BlockState relState = level.getBlockState(relPos);

                if (relState.is(this)) {
                    updateBottomState(level, relPos, relState);
                }
            }

            num += 1;
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    private void updateBottomState(Level level, BlockPos pos, BlockState currentState) {
        if (level.isClientSide)
            return;

        int num = 0;

        for (Direction Cardinal : Direction.values()) {
            BlockPos relPos = pos.relative(Cardinal);
            BlockState relState = level.getBlockState(relPos);

            boolean shouldBeRelative = !isThatMe(relState)
                    && !Block.isShapeFullBlock(relState.getCollisionShape(level, relPos));

            if (Cardinal == Direction.DOWN) {
                boolean solidBelow = false;
                for (int i = 1; i <= 1; i++) {
                    BlockPos checkPos = pos.below(i);
                    BlockState checkState = level.getBlockState(checkPos);

                    if (isThatMe(checkState) || Block.isShapeFullBlock(checkState.getCollisionShape(level, checkPos))) {
                        solidBelow = true;
                        break;
                    }
                }
                shouldBeRelative = !solidBelow;
            } else {
                shouldBeRelative = !isThatMe(relState)
                        && !Block.isShapeFullBlock(relState.getCollisionShape(level, relPos));
            }

            if (currentState.getValue(DIRECTIONS[num]) != shouldBeRelative) {
                currentState = currentState.setValue(DIRECTIONS[num], shouldBeRelative);
                level.setBlock(pos, currentState, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);

                // If the down property changed, notify the block above to re‑evaluate.
                if (Cardinal == Direction.DOWN) {
                    BlockPos abovePos = pos.above();
                    BlockState aboveState = level.getBlockState(abovePos);
                    if (aboveState.is(this)) {
                        updateBottomState(level, abovePos, aboveState);
                    }
                }
            }
            num++;

        }

    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {

        return state.getValue(DIRECTIONS[0]) ? Shapes.block() : Shapes.empty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 15; // blocks all light, like a solid block
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return false; // stops skylight from passing through
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity instanceof Player player && ProvenanceDataHandler.getAbilities(player).stream()
                .anyMatch(ability -> ability instanceof VoidPresenceAbility)) {
            return;
        }
        super.fallOn(level, state, pos, entity, fallDistance);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
            boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        // LogUtils.getLogger().debug("something nearby changed");
        // updateBottomState(level, fromPos, state);

        if (state.is(this)) {
        updateBottomState(level, pos, state);
        }
    }

    @Override
    @Deprecated
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {

        boolean anyRelState = false;

        int num = 0;
        for (Direction Cardinal : Direction.values()) {
            if (state.getValue(DIRECTIONS[num])) {
                anyRelState = true;
                break;
            }
            num += 1;
        }

        if (anyRelState) {
            return false;
        }

        if (isThatMe(adjacentBlockState)) {
            return true;
        }
        return super.skipRendering(state, adjacentBlockState, side);

    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        level.removeBlock(pos, false);
    }
}
