package com.mc3699.smparch.archetype.fictionalbeef.bigmanrake;

import com.mc3699.smparch.SMPArch;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BigManRakeSounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SMPArch.MODID);

    public static final Holder<SoundEvent> MAEXTRODE = SOUNDS.register("maextrode", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> ORCHWORK = SOUNDS.register("orchwork", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> ACC1 = SOUNDS.register("acc1", SoundEvent::createVariableRangeEvent);
    
    public static final Holder<SoundEvent> ACC2 = SOUNDS.register("acc2", SoundEvent::createVariableRangeEvent);
    
    public static final Holder<SoundEvent> MOVEMENT1CAP = SOUNDS.register("movement1cap", SoundEvent::createVariableRangeEvent);
    
    public static final Holder<SoundEvent> MOVEMENT2CAP = SOUNDS.register("movement2cap", SoundEvent::createVariableRangeEvent);
    
    public static final Holder<SoundEvent> MOVEMENT3CAP = SOUNDS.register("movement3cap", SoundEvent::createVariableRangeEvent);
    
    public static final Holder<SoundEvent> MOVEMENT4CAP = SOUNDS.register("movement4cap", SoundEvent::createVariableRangeEvent);
    
    public static final Holder<SoundEvent> MOVEMENT0CAP = SOUNDS.register("movement0cap", SoundEvent::createVariableRangeEvent);
    
    public static final Holder<SoundEvent> SYMHEADBOMB = SOUNDS.register("symheadbomb", SoundEvent::createVariableRangeEvent);
    
    public static final Holder<SoundEvent> SYMCHORATK = SOUNDS.register("symchoratk", SoundEvent::createVariableRangeEvent);
    
    public static final Holder<SoundEvent> VIBRATO = SOUNDS.register("vibrato", SoundEvent::createVariableRangeEvent);
}
