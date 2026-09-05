package com.mc3699.smparch.registry;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.AliveAlexSounds;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeSounds;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Sounds;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.JkmcAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.JkmcSounds;
import com.mc3699.smparch.archetype.fictionalbeef.pinky.PinkySounds;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneSounds;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioSounds;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SMPSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SMPArch.MODID);

    public static final Holder<SoundEvent> DISRUPTION = SOUNDS.register("disruption", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> HEARTBEAT = SOUNDS.register("heartbeat", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> SLAM_FALL = SOUNDS.register("slam_fall", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> QUICKHACK = SOUNDS.register("qhdone", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> BLACKOUT = SOUNDS.register("blackout", SoundEvent::createVariableRangeEvent);




    

    
    
    public static void register(IEventBus eventBus)
    {
        AliveAlexSounds.SOUNDS.register(eventBus);
        BigManRakeSounds.SOUNDS.register(eventBus);
        JkmcSounds.SOUNDS.register(eventBus);
        ScapuneSounds.SOUNDS.register(eventBus);
        YkorioSounds.SOUNDS.register(eventBus);
        PinkySounds.SOUNDS.register(eventBus);
        FlameK1ng_Sounds.SOUNDS.register(eventBus);
        SOUNDS.register(eventBus);
    }

}
