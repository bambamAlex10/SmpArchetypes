package com.mc3699.smparch.registry;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.AliveAlexArchetype;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeArchetype;
import com.mc3699.smparch.archetype.fictionalbeef.drrapscallion.DRRapScallion_Archetype;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Archetype;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.JkmcArchetype;
import com.mc3699.smparch.archetype.fictionalbeef.pinky.PinkyArchetype;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneArchetype;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioArchetype;
import com.mc3699.smparch.archetype.tbs.aidenman.AidenArchetype;
import com.mc3699.smparch.archetype.tbs.arveral.ArveralArchetype;
import com.mc3699.smparch.archetype.tbs.blox.BloxArchetype;
import com.mc3699.smparch.archetype.tbs.firelight.FirelightArchetype;
import com.mc3699.smparch.archetype.tbs.growth.GrowthArchetype;
import com.mc3699.smparch.archetype.tbs.heaven.HeavenArchetype;
import com.mc3699.smparch.archetype.tbs.john_ultrakill.JohnUltrakillArchetype;
import com.mc3699.smparch.archetype.tbs.miku.MikuArchetype;
import com.mc3699.smparch.archetype.tbs.nightheart.NightheartArchetype;
import com.mc3699.smparch.archetype.tbs.pencil_html.PencilHtmlArchetype;
import com.mc3699.smparch.archetype.tbs.tekkitdooood.TekkitdoooodArchetype;
import com.mc3699.smparch.archetype.tbs.terra.TerraArchetype;
import com.mc3699.smparch.archetype.tbs.ariytwo6.AriytwoArchetype;
import com.mc3699.smparch.archetype.tbs.emberflame65.EmberFlameArchetype;
import com.mc3699.smparch.archetype.tbs.eyae.EyaeArchetype;
import com.mc3699.smparch.archetype.tbs.john_generic.JohnArchetype;
import com.mc3699.smparch.archetype.tbs.teebee.TeebeeArchetype;
import com.mc3699.smparch.archetype.tbs.zorgoliath.HarkenerArchetype;
import net.mc3699.provenance.ProvenanceRegistries;
import net.mc3699.provenance.archetype.foundation.BaseArchetype;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SMPArchetypes {

    public static final DeferredRegister<BaseArchetype> ARCHETYPES =
            DeferredRegister.create(ProvenanceRegistries.ARCHETYPE_REGISTRY, SMPArch.MODID);


    public static final Supplier<PinkyArchetype> PINKY = 
            ARCHETYPES.register("pinky", PinkyArchetype::new);

    public static final Supplier<NightheartArchetype> NIGHTHEART =
            ARCHETYPES.register("nightheart", NightheartArchetype::new);

    public static final Supplier<FirelightArchetype> FIRELIGHT =
            ARCHETYPES.register("firelight", FirelightArchetype::new);

    public static final Supplier<ArveralArchetype> ARVERAL =
            ARCHETYPES.register("arveral", ArveralArchetype::new);

    public static final Supplier<BloxArchetype> BLOX =
            ARCHETYPES.register("blox", BloxArchetype::new);

    public static final Supplier<HeavenArchetype> HEAVEN =
            ARCHETYPES.register("heaven", HeavenArchetype::new);

    public static final Supplier<AidenArchetype> AIDEN =
            ARCHETYPES.register("aiden", AidenArchetype::new);

    public static final Supplier<GrowthArchetype> GROWTH =
            ARCHETYPES.register("growth", GrowthArchetype::new);

    public static final Supplier<TerraArchetype> TERRA =
            ARCHETYPES.register("terra", TerraArchetype::new);

    public static final Supplier<JohnUltrakillArchetype> JOHN_ULTRAKILL =
            ARCHETYPES.register("john_ultrakill", JohnUltrakillArchetype::new);

    public static final Supplier<PencilHtmlArchetype> PENCIL_HTML =
            ARCHETYPES.register("pencil_html", PencilHtmlArchetype::new);

    public static final Supplier<TekkitdoooodArchetype> TEKKITDOOOOD =
            ARCHETYPES.register("tekkitdooood", TekkitdoooodArchetype::new);

    public static final Supplier<MikuArchetype> MIKU =
            ARCHETYPES.register("miku", MikuArchetype::new);

    public static final Supplier<BigManRakeArchetype> BigManrake =
            ARCHETYPES.register("bigmanrake", BigManRakeArchetype::new);

    public static final Supplier<AliveAlexArchetype> AliveAlex = 
        ARCHETYPES.register("alivealex",AliveAlexArchetype::new);

    public static final Supplier<FlameK1ng_Archetype> FlameK1ng_ = 
        ARCHETYPES.register("flamek1ng_",FlameK1ng_Archetype::new);

    public static final Supplier<DRRapScallion_Archetype> DRRapScallion = 
        ARCHETYPES.register("drrapscallion",DRRapScallion_Archetype::new);

    public static final Supplier<JkmcArchetype> JKMC = 
        ARCHETYPES.register("jkmc",JkmcArchetype::new);

    public static final Supplier<ScapuneArchetype> Scapune = 
        ARCHETYPES.register("scapune",ScapuneArchetype::new);

    public static final Supplier<YkorioArchetype> Ykorio = 
        ARCHETYPES.register("ykorio",YkorioArchetype::new);
    public static final Supplier<EyaeArchetype> EYAE =
            ARCHETYPES.register("eyae", EyaeArchetype::new);

    public static final Supplier<JohnArchetype> JOHN_GENERIC =
            ARCHETYPES.register("john_generic", JohnArchetype::new);

    public static final Supplier<EmberFlameArchetype> EMBER_FLAME =
            ARCHETYPES.register("ember_flame", EmberFlameArchetype::new);

    public static final Supplier<HarkenerArchetype> HARKENER =
            ARCHETYPES.register("harkener", HarkenerArchetype::new);

    public static final Supplier<TeebeeArchetype> TEEBEE =
            ARCHETYPES.register("teebee", TeebeeArchetype::new);

    public static final Supplier<AriytwoArchetype> ARIYTWO =
            ARCHETYPES.register("ariytwo6", AriytwoArchetype::new);

    public static void register(IEventBus eventBus)
    {
        ARCHETYPES.register(eventBus);
    }
    
}
