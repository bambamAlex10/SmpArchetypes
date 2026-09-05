package com.mc3699.smparch.registry;

import java.util.function.Supplier;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock.VoidPresenceBlockEntity;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock.Registration.VoidBlock;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock.Registration.VoidPresenceBlock;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SMPBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SMPArch.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SMPArch.MODID);


    public static final DeferredBlock<VoidPresenceBlock> VOIDPRESENCEBLOCK = BLOCKS.registerBlock("void_presence_block",
            VoidPresenceBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(2.0f)
                    .sound(SoundType.AMETHYST)
                    .noCollission()
                //     .noOcclusion()
                );

    public static final DeferredBlock<VoidBlock> VOIDBLOCK = BLOCKS.registerBlock("void_block",
            VoidBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .noCollission()
                //     .noOcclusion()
                    .noLootTable());

    public static final Supplier<BlockEntityType<VoidPresenceBlockEntity>> VOID_PRESENCE_BE = BLOCK_ENTITIES
            .register("void_presence_be", () -> BlockEntityType.Builder.of(VoidPresenceBlockEntity::new,
                    VOIDPRESENCEBLOCK.get()
            ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
    }

}
