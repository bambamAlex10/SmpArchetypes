package com.mc3699.smparch.archetype.fictionalbeef.ykorio;

import com.mc3699.smparch.SMPArch;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class YkorioSounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SMPArch.MODID);

    //clone
    public static final Holder<SoundEvent> MAJIK = SOUNDS.register("majik", SoundEvent::createVariableRangeEvent);

    //void gambling
    public static final Holder<SoundEvent> GAMBLEWIN = SOUNDS.register("voidgamblewin", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> GAMBLELOSE = SOUNDS.register("voidgamblelose", SoundEvent::createVariableRangeEvent);

    //shield
    public static final Holder<SoundEvent> SHIELDHIT = SOUNDS.register("shieldhit", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SHIELDUP = SOUNDS.register("shieldup", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SHIELDSHATTER = SOUNDS.register("shieldshatter", SoundEvent::createVariableRangeEvent);

    //domain expansion: finite void
    public static final Holder<SoundEvent> SMOKEDISSIPATES = SOUNDS.register("smokeleaves", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SMOKESTARTS = SOUNDS.register("smokestarts", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SMOKETHEMESONG = SOUNDS.register("yourinsmoke", SoundEvent::createVariableRangeEvent); //this
    public static final Holder<SoundEvent> TRYTOLEAVESMOKE = SOUNDS.register("attemptsmokeleave", SoundEvent::createVariableRangeEvent);



}
