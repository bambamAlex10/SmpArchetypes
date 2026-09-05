package com.mc3699.smparch.archetype.fictionalbeef.scapune;

import com.mc3699.smparch.SMPArch;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ScapuneSounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SMPArch.MODID);

    public static final Holder<SoundEvent> QUEENBEE = SOUNDS.register("queenbee", SoundEvent::createVariableRangeEvent);
    
    public static final Holder<SoundEvent> DECIVRSWILL = SOUNDS.register("decieverswill", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> DECIVRSLUCK = SOUNDS.register("decieversluck", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> DECIVRSDECEPTION = SOUNDS.register("decieversdeception", SoundEvent::createVariableRangeEvent);
}
