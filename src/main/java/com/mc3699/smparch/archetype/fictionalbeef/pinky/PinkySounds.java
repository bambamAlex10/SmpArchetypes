package com.mc3699.smparch.archetype.fictionalbeef.pinky;

import com.mc3699.smparch.SMPArch;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PinkySounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SMPArch.MODID);

    public static final Holder<SoundEvent> CHAOTICSURGE = SOUNDS.register("chaoticsurge", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> HEALLIGHT = SOUNDS.register("heallight", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> VOIDBLITZ = SOUNDS.register("voidblitz", SoundEvent::createVariableRangeEvent);
}
