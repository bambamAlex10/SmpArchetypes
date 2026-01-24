package com.mc3699.smparch.registry;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.aidenman.PhotosynthesisAbility;
import com.mc3699.smparch.archetype.arveral.TendrilsAbility;
import com.mc3699.smparch.archetype.blox.BloxDashAbility;
import com.mc3699.smparch.archetype.blox.BloxShieldAbility;
import com.mc3699.smparch.archetype.blox.BloxSpeedAbility;
import com.mc3699.smparch.archetype.firelight.DragonLeapAbility;
import com.mc3699.smparch.archetype.growth.GrowthDebuff;
import com.mc3699.smparch.archetype.growth.NatureGiftAbility;
import com.mc3699.smparch.archetype.growth.NatureSightAbility;
import com.mc3699.smparch.archetype.heaven.BlackoutAbility;
import com.mc3699.smparch.archetype.heaven.ContagionAbility;
import com.mc3699.smparch.archetype.heaven.DisruptionAbility;
import com.mc3699.smparch.archetype.heaven.OverheatAbility;
import com.mc3699.smparch.archetype.john_ultrakill.UltrakillDashAbility;
import com.mc3699.smparch.archetype.john_ultrakill.UltrakillFeedbackerAbility;
import com.mc3699.smparch.archetype.john_ultrakill.UltrakillSlamAbility;
import com.mc3699.smparch.archetype.miku.*;
import com.mc3699.smparch.archetype.nightheart.RandomTeleportAbility;
import com.mc3699.smparch.archetype.pencil_html.EndStepAbility;
import com.mc3699.smparch.archetype.pinky.ChaoticSurge;
import com.mc3699.smparch.archetype.pinky.HolyLight;
import com.mc3699.smparch.archetype.pinky.HolyLight;
import com.mc3699.smparch.archetype.pinky.VoidBlitz;
import com.mc3699.smparch.archetype.terra.HeartbeatAbility;
import com.mc3699.smparch.archetype.terra.SolarSurgeAbility;
import com.mc3699.smparch.generic_abilities.DashAbility;
import com.mc3699.smparch.generic_abilities.FireballAbility;
import com.mc3699.smparch.generic_abilities.WallClimbAbility;
import net.mc3699.provenance.ProvenanceRegistries;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SMPAbilities {

    public static final DeferredRegister<BaseAbility> ABILITIES =
            DeferredRegister.create(ProvenanceRegistries.ABILITY_REGISTRY, SMPArch.MODID);


    public static final Supplier<RandomTeleportAbility> RANDOM_TELEPORT =
            ABILITIES.register("random_teleport", RandomTeleportAbility::new);

    public static final Supplier<ContagionAbility> CONTAGION =
            ABILITIES.register("contagion", ContagionAbility::new);

    public static final Supplier<OverheatAbility> OVERHEAT =
            ABILITIES.register("overheat", OverheatAbility::new);

    public static final Supplier<DashAbility> DASH =
            ABILITIES.register("dash", DashAbility::new);

    public static final Supplier<DragonLeapAbility> DRAGON_LEAP =
            ABILITIES.register("dragon_leap", DragonLeapAbility::new);

    public static final Supplier<WallClimbAbility> WALL_CLIMB =
            ABILITIES.register("wall_climb", WallClimbAbility::new);

    public static final Supplier<TendrilsAbility> TENDRILS =
            ABILITIES.register("tendrils", TendrilsAbility::new);

    public static final Supplier<BlackoutAbility> BLACKOUT =
            ABILITIES.register("blackout", BlackoutAbility::new);

    public static final Supplier<DisruptionAbility> DISRUPTION =
            ABILITIES.register("disruption", DisruptionAbility::new);

    public static final Supplier<BloxShieldAbility> BLOX_SHIELD =
            ABILITIES.register("blox_shield", BloxShieldAbility::new);

    public static final Supplier<BloxDashAbility> BLOX_DASH =
            ABILITIES.register("blox_dash", BloxDashAbility::new);

    public static final Supplier<BloxSpeedAbility> BLOX_SPEED =
            ABILITIES.register("blox_speed", BloxSpeedAbility::new);

    public static final Supplier<PhotosynthesisAbility> PHOTOSYNTHESIS =
            ABILITIES.register("photosynthesis", PhotosynthesisAbility::new);

    public static final Supplier<NatureGiftAbility> NATURES_GIFT =
            ABILITIES.register("nature_gift", NatureGiftAbility::new);

    public static final Supplier<NatureSightAbility> NATURES_SIGHT =
            ABILITIES.register("nature_sight", NatureSightAbility::new);

    public static final Supplier<GrowthDebuff> GROWTH_DEBUFF =
            ABILITIES.register("growth_debuff", GrowthDebuff::new);

    public static final Supplier<HeartbeatAbility> HEARTBEAT =
            ABILITIES.register("heartbeat", HeartbeatAbility::new);

    public static final Supplier<SolarSurgeAbility> SOLAR_SURGE =
            ABILITIES.register("solar_surge", SolarSurgeAbility::new);

    public static final Supplier<UltrakillDashAbility> ULTRAKILL_DASH =
            ABILITIES.register("ultrakill_dash", UltrakillDashAbility::new);

    public static final Supplier<UltrakillSlamAbility> ULTRAKILL_SLAM =
            ABILITIES.register("ultrakill_slam", UltrakillSlamAbility::new);

    public static final Supplier<UltrakillFeedbackerAbility> ULTRAKILL_FEEDBACKER =
            ABILITIES.register("ultrakill_feedbacker", UltrakillFeedbackerAbility::new);

    public static final Supplier<EndStepAbility> END_STEP =
            ABILITIES.register("end_step", EndStepAbility::new);
    public static final Supplier<SkulkBlastAbility> SKULK_BLAST =
            ABILITIES.register("skulk_blast", SkulkBlastAbility::new);
    public static final Supplier<HolyLight> HEALING_LIGHT =
            ABILITIES.register("holy_light", HolyLight::new);
    public static final Supplier<VoidBlitz> VOID_BLITZ =
            ABILITIES.register("void_blitz", VoidBlitz::new);
    public static final Supplier<ChaoticSurge> ChaoticSurge =
            ABILITIES.register("chaotic_surge", ChaoticSurge::new);

    public static final Supplier<WardenSkinAbility> WARDEN_SKIN =
            ABILITIES.register("warden_skin", WardenSkinAbility::new);

    public static final Supplier<WardenStrengthAbility> WARDEN_STRENGTH =
            ABILITIES.register("warden_strength", WardenStrengthAbility::new);

    public static final Supplier<SonicBoomAbility> SONIC_BOOM =
            ABILITIES.register("sonic_boom", SonicBoomAbility::new);

    public static final Supplier<StrongLegsHeavyArmsAbility> STRONG_LEGS_HEAVY_ARMS =
            ABILITIES.register("strong_legs_heavy_arms", StrongLegsHeavyArmsAbility::new);

    /*public static final Supplier<SkulkShieldAbility> SKULK_SHIELD =
            ABILITIES.register("skulk_shield", SkulkShieldAbility::new);*/
    //Unimplemented ability commented out for now

    public static final Supplier<DeepDarknessAbility> DEEP_DARKNESS =
            ABILITIES.register("deep_darkness", DeepDarknessAbility::new);

    public static final Supplier<AidFromBelowAbility> AID_FROM_BELOW =
            ABILITIES.register("aid_from_below", AidFromBelowAbility::new);

    public static final Supplier<OneWithTheDark> ONE_WITH_THE_DARK =
            ABILITIES.register("one_with_the_dark", OneWithTheDark::new);

    public static final Supplier<FireballAbility> FIREBALL =
            ABILITIES.register("fireball", FireballAbility::new);


    public static void register(IEventBus eventBus) { ABILITIES.register(eventBus); }

}
