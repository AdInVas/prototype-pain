package net.adinvas.prototype_pain.fluid_system;

import net.minecraft.util.RandomSource;

import java.util.*;

public class MedicalFluids {
    public static final Map<String,MedicalFluid> REGISTRY = new HashMap<>();

    public static final MedicalFluid OPIUM = register(new MedicalFluid("opium",MedicalEffects.OPIUM,0xeb4034));
    public static final MedicalFluid FENTANYL = register(new MedicalFluid("fentanyl",MedicalEffects.FENTANYL,0xa1d9ff));
    public static final MedicalFluid MORPHINE = register(new MedicalFluid("morphine",MedicalEffects.MORPHINE,0x632329));
    public static final MedicalFluid HEROIN = register(new MedicalFluid("heroin",MedicalEffects.HEROIN,0xedf8ff));
    public static final MedicalFluid WATER = register(new MedicalFluid("water",MedicalEffects.WATER,0xa2c6db));
    public static final MedicalFluid PAINKILLERS = register(new MedicalFluid("painkillers",MedicalEffects.PAINKILLERS,0x888888));
    public static final MedicalFluid BRAINGROW = register(new MedicalFluid("braingrow",MedicalEffects.BRAINGROW,0x915946));
    public static final MedicalFluid ALCOHOL = register(new MedicalFluid("alcohol",MedicalEffects.ALCOHOL,0x828282));
    public static final MedicalFluid ANTISEPTIC = register(new MedicalFluid("antiseptic",MedicalEffects.ANTISEPTIC,0x5a6b45));
    public static final MedicalFluid RELIEF_CREAM = register(new MedicalFluid("relief_cream",MedicalEffects.RELIEF_CREAM,0x75644d));
    public static final MedicalFluid SALINE = register(new MedicalFluid("saline",MedicalEffects.SALINE,0xc9c8c5));
    public static final MedicalFluid ANTIBIOTICS = register(new MedicalFluid("antibiotics",MedicalEffects.ANTIBIOTICS,0x593f8a));
    public static final MedicalFluid ANTISERUM = register(new MedicalFluid("antiserum",MedicalEffects.ANTISERUM,0x6f3582));
    public static final MedicalFluid CEFTRAIAXONE = register(new MedicalFluid("ceftriaxone",MedicalEffects.CEFTRAIAXONE,0x184a19));
    public static final MedicalFluid PROCOAGULANT = register(new MedicalFluid("procoagulant",MedicalEffects.PROCOAGULANT,0x57172b));
    public static final MedicalFluid STREPTOKINASE = register(new MedicalFluid("streptokinase",MedicalEffects.STREPTOKINASE,0x0aecfc));
    public static final MedicalFluid NALOXONE= register(new MedicalFluid("naloxone",MedicalEffects.NALOXONE,0xf2abff));
    public static final MedicalFluid REACTION_LIQUID = register(new MedicalFluid("reaction_liquid",MedicalEffects.WATER,0xbceb23));
    public static final MedicalFluid DIRTY_WATER = register(new MedicalFluid("ground_water",MedicalEffects.DIRTY_WATER,0x7494a6));
    public static final MedicalFluid COFFEE = register(new MedicalFluid("coffee",MedicalEffects.COFFEE,0x9e8859));
    public static MedicalFluid register(MedicalFluid fluid){
        REGISTRY.put(fluid.getId(),fluid);
        return fluid;
    }

    public static MedicalFluid get(String id) {
        return REGISTRY.get(id);
    }

    public static MedicalFluid getRandom(RandomSource random) {
        if (REGISTRY.isEmpty()) return null;
        List<MedicalFluid> fluids = List.copyOf(REGISTRY.values());
        return fluids.get(random.nextInt(fluids.size()));
    }


    private static final Map<String, Set<MedicalFluid>> TAGS = new HashMap<>();

    public static void registerInTag(String tag, MedicalFluid... fluids) {
        TAGS.put(tag, new HashSet<>(List.of(fluids)));
    }

    public static boolean fluidInTag(MedicalFluid fluid, String tag) {
        Set<MedicalFluid> set = TAGS.get(tag);
        return set != null && set.contains(fluid);
    }

    public static Set<MedicalFluid> getTag(String tag) {
        return TAGS.getOrDefault(tag, Collections.emptySet());
    }

    public static Set<String> getAllTags() {
        return TAGS.keySet();
    }

    static {
        MedicalFluids.registerInTag("prototype_pain:opioids", OPIUM, FENTANYL, MORPHINE, HEROIN);
        MedicalFluids.registerInTag("prototype_pain:antiseptics", ANTISEPTIC,ALCOHOL,RELIEF_CREAM);
    }

}
