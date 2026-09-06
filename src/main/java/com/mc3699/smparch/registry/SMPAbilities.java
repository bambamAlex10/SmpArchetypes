package com.mc3699.smparch.registry;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.BloodofCovenant.BloodofCovenantAmbient;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.Concurrent.ConcurrentAbility;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.Retribution.RetributionAbility;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.Salvation.SalvationAbility;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.Accelerando.AccelerandoAbility;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.AccesoMorendo.AccesoMorendoAmbient;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.Concerto.ConcertoAbility;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.Vibrato.VibratoAbility;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.Brimstone.BrimstonePassive;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.InvertedScale.InvertedScaleActive;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.Pyrophagia.PyrophagiaPassive;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.WaxenPinion.WaxenPinionActive;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.ExplosionPassive.RezePassive;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.OnDeath.RezeDeathPassive;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.Propulsion.ExplosivePropulsion;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.RezeLargeHit.RezeLargeHitAbility;
import com.mc3699.smparch.archetype.fictionalbeef.pinky.CatLike;
import com.mc3699.smparch.archetype.fictionalbeef.pinky.ChaoticSurge;
import com.mc3699.smparch.archetype.fictionalbeef.pinky.HolyLight;
import com.mc3699.smparch.archetype.fictionalbeef.pinky.VoidBlitz;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversFury.DecieversFuryAbility;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGraceAbility;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversWill.DecieversWillAbility;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidCasino.VoidCasinoAmbient;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidDomain.VoidDomainAmbient;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidEssence.VoidEssenceAbility;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidPresenceAbility;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep.VoidStepAbility;
import com.mc3699.smparch.archetype.tbs.aidenman.PhotosynthesisAbility;
import com.mc3699.smparch.archetype.tbs.arveral.TendrilsAbility;
import com.mc3699.smparch.archetype.tbs.blox.BloxDashAbility;
import com.mc3699.smparch.archetype.tbs.blox.BloxShieldAbility;
import com.mc3699.smparch.archetype.tbs.blox.BloxSpeedAbility;
import com.mc3699.smparch.archetype.tbs.firelight.DragonLeapAbility;
import com.mc3699.smparch.archetype.tbs.growth.GrowthDebuff;
import com.mc3699.smparch.archetype.tbs.growth.NatureGiftAbility;
import com.mc3699.smparch.archetype.tbs.growth.NatureSightAbility;
import com.mc3699.smparch.archetype.tbs.heaven.BlackoutAbility;
import com.mc3699.smparch.archetype.tbs.heaven.ContagionAbility;
import com.mc3699.smparch.archetype.tbs.heaven.DisruptionAbility;
import com.mc3699.smparch.archetype.tbs.heaven.OverheatAbility;
import com.mc3699.smparch.archetype.tbs.john_ultrakill.UltrakillDashAbility;
import com.mc3699.smparch.archetype.tbs.john_ultrakill.UltrakillFeedbackerAbility;
import com.mc3699.smparch.archetype.tbs.john_ultrakill.UltrakillSlamAbility;
import com.mc3699.smparch.archetype.tbs.miku.AidFromBelowAbility;
import com.mc3699.smparch.archetype.tbs.miku.DeepDarknessAbility;
import com.mc3699.smparch.archetype.tbs.miku.OneWithTheDark;
import com.mc3699.smparch.archetype.tbs.miku.SkulkBlastAbility;
import com.mc3699.smparch.archetype.tbs.miku.SonicBoomAbility;
import com.mc3699.smparch.archetype.tbs.miku.StrongLegsHeavyArmsAbility;
import com.mc3699.smparch.archetype.tbs.miku.WardenSkinAbility;
import com.mc3699.smparch.archetype.tbs.miku.WardenStrengthAbility;
import com.mc3699.smparch.archetype.tbs.nightheart.RandomTeleportAbility;
import com.mc3699.smparch.archetype.tbs.pencil_html.EndStepAbility;
import com.mc3699.smparch.archetype.tbs.terra.HeartbeatAbility;
import com.mc3699.smparch.archetype.tbs.terra.SolarSurgeAbility;
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

    public static final Supplier<InvertedScaleActive> INVERTED_SCALE =
            ABILITIES.register("inverted_scale", InvertedScaleActive::new);

    public static final Supplier<PyrophagiaPassive> PYROPHAGIA =
            ABILITIES.register("pyrophagia", PyrophagiaPassive::new);

    public static final Supplier<WaxenPinionActive> WAXENPINION =
            ABILITIES.register("waxen_pinion", WaxenPinionActive::new);

    public static final Supplier<BrimstonePassive> BRIMSTONE =
            ABILITIES.register("brimstone", BrimstonePassive::new);

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

    public static final Supplier<ChaoticSurge> CHAOTIC_SURGE =
            ABILITIES.register("chaotic_surge", ChaoticSurge::new);

    public static final Supplier<CatLike> CAT_LIKE =
            ABILITIES.register("cat_like", CatLike::new);

    public static final Supplier<AccesoMorendoAmbient> ACCESO_MORENDO =
            ABILITIES.register("acceso_morendo", AccesoMorendoAmbient::new);

    public static final Supplier<AccelerandoAbility> ACCELERANDO =
            ABILITIES.register("accelerando", AccelerandoAbility::new);

    public static final Supplier<ConcertoAbility> CONCERTO =
            ABILITIES.register("concerto", ConcertoAbility::new);

    public static final Supplier<VibratoAbility> VIBRATO =
            ABILITIES.register("vibrato", VibratoAbility::new);

    public static final Supplier<RetributionAbility> RETRIBUTION =
            ABILITIES.register("retribution", RetributionAbility::new);

    public static final Supplier<SalvationAbility> SALVATION =
            ABILITIES.register("salvation", SalvationAbility::new);

    public static final Supplier<ConcurrentAbility> CONCURRENT =
            ABILITIES.register("concurrent", ConcurrentAbility::new)
            ;
    public static final Supplier<BloodofCovenantAmbient> BLDOFCVT =
            ABILITIES.register("blood_of_the_covenant", BloodofCovenantAmbient::new);

    public static final Supplier<DecieversFuryAbility> DECIVRSFURY =
            ABILITIES.register("decievers_fury", DecieversFuryAbility::new);

    public static final Supplier<DecieversWillAbility> DECIVRSWILL =
            ABILITIES.register("decievers_will", DecieversWillAbility::new)
            ;
    public static final Supplier<DecieversGraceAbility> DECIVRSGRACE =
            ABILITIES.register("decievers_grace", DecieversGraceAbility::new);
            
    public static final Supplier<com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversAwareness.DecieversAwarenessPassive> DECIVRSAWARENESS =
            ABILITIES.register("decievers_awareness", com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversAwareness.DecieversAwarenessPassive::new);

    public static final Supplier<RezePassive> REZEPASSIVE =
            ABILITIES.register("reze_passive", RezePassive::new);

    public static final Supplier<RezeLargeHitAbility> REZELARGEHIT =
            ABILITIES.register("reze_large_hit", RezeLargeHitAbility::new);

    public static final Supplier<ExplosivePropulsion> REZEPROPULSION =
            ABILITIES.register("reze_propulse", ExplosivePropulsion::new);
            
    public static final Supplier<RezeDeathPassive> REZEDEATHPASSIVE =
            ABILITIES.register("reze_death_passive", RezeDeathPassive::new);

    public static final Supplier<VoidDomainAmbient> VOIDDOMAIN =
            ABILITIES.register("void_domain", VoidDomainAmbient::new);

    public static final Supplier<VoidCasinoAmbient> VOIDCASINO =
            ABILITIES.register("void_casino", VoidCasinoAmbient::new);

    public static final Supplier<VoidPresenceAbility> VOIDPRESENCE =
            ABILITIES.register("void_presence", VoidPresenceAbility::new);

    public static final Supplier<VoidEssenceAbility> VOIDESSENCE =
            ABILITIES.register("void_essence", VoidEssenceAbility::new);

    public static final Supplier<VoidStepAbility> VOIDSTEP =
            ABILITIES.register("void_step", VoidStepAbility::new);

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
