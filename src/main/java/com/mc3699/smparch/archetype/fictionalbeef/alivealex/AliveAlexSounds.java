package com.mc3699.smparch.archetype.fictionalbeef.alivealex;

import com.mc3699.smparch.SMPArch;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AliveAlexSounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SMPArch.MODID);
            
    public static final Holder<SoundEvent> DIETIME = SOUNDS.register("dietime", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> HEALTIME = SOUNDS.register("healtime", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> TIMEPRETRIGGER = SOUNDS.register("timepretrigger", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> COUNTERACTIVE = SOUNDS.register("counteractive", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> COUNTERWIN = SOUNDS.register("counterwin", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> COUNTERLOSE = SOUNDS.register("counterlose", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> WELCOMEBACKVOID = SOUNDS.register("welcomebackvoid", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> VOIDFAILWOW = SOUNDS.register("voidfailwow", SoundEvent::createVariableRangeEvent);
}
