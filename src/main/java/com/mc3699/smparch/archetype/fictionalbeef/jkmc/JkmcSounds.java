package com.mc3699.smparch.archetype.fictionalbeef.jkmc;

import com.mc3699.smparch.SMPArch;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class JkmcSounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SMPArch.MODID);
}
