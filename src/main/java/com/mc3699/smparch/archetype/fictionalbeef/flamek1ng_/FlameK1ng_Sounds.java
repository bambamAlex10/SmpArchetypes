package com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_;

import com.mc3699.smparch.SMPArch;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FlameK1ng_Sounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SMPArch.MODID);
    
    public static final Holder<SoundEvent> INVERTEDSCALESUCCESS = SOUNDS.register("invertedscalehai", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> DEATHEFFECT = SOUNDS.register("brimstonedeath", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> COINFLIPHEADS = SOUNDS.register("brimstonecoinflipheads", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> COINFLIPTAILS = SOUNDS.register("brimstonecoinfliptails", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> STARTCOINFLIP = SOUNDS.register("brimstonecoinstart", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> FIREDASH1 = SOUNDS.register("firedash1", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> FIREDASH2 = SOUNDS.register("firedash2", SoundEvent::createVariableRangeEvent);

}
