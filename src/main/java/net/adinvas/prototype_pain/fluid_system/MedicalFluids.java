package net.adinvas.prototype_pain.fluid_system;

import java.util.HashMap;
import java.util.Map;

public class MedicalFluids {
    public static final Map<String,MedicalFluid> REGISTRY = new HashMap<>();

    public static final MedicalFluid OPIUM = register(new MedicalFluid("opium",MedicalEffects.OPIUM,0xeb4034));
    public static final MedicalFluid FENTANYL = register(new MedicalFluid("fentanyl",MedicalEffects.FENTANYL,0xa1d9ff));
    public static final MedicalFluid MORPHINE = register(new MedicalFluid("morphine",MedicalEffects.MORPHINE,0x632329));
    public static final MedicalFluid HEROIN = register(new MedicalFluid("heroin",MedicalEffects.HEROIN,0xedf8ff));
    public static final MedicalFluid WATER = register(new MedicalFluid("water",MedicalEffects.WATER,0xa2c6db));
    public static final MedicalFluid PAINKILLERS = register(new MedicalFluid("painkillers",MedicalEffects.WATER,0xAAAAAA));
    public static final MedicalFluid BRAINGROW = register(new MedicalFluid("braingrow",MedicalEffects.WATER,0x915946));
    public static final MedicalFluid ALCOHOL = register(new MedicalFluid("alcohol",MedicalEffects.WATER,0x828282));
    public static final MedicalFluid ANTISEPTIC = register(new MedicalFluid("antiseptic",MedicalEffects.WATER,0x5a6b45));
    public static final MedicalFluid RELIEF_CREAM = register(new MedicalFluid("relief_cream",MedicalEffects.WATER,0x75644d));
    public static final MedicalFluid SALINE = register(new MedicalFluid("saline",MedicalEffects.WATER,0xc9c8c5));
    public static final MedicalFluid ANTIBIOTICS = register(new MedicalFluid("antibiotics",MedicalEffects.WATER,0x593f8a));
    public static final MedicalFluid ANTISERUM = register(new MedicalFluid("antiserum",MedicalEffects.WATER,0x6f3582));
    public static final MedicalFluid CEFTRAIAXONE = register(new MedicalFluid("ceftriaxone",MedicalEffects.WATER,0x184a19));
    public static final MedicalFluid PROCOAGULANT = register(new MedicalFluid("procoagulant",MedicalEffects.WATER,0x57172b));
    public static final MedicalFluid STREPTOKINASE = register(new MedicalFluid("streptokinase",MedicalEffects.WATER,0x0aecfc));
    public static final MedicalFluid NALOXONE= register(new MedicalFluid("naloxone",MedicalEffects.WATER,0xf2abff));

    private static MedicalFluid register(MedicalFluid fluid){
        REGISTRY.put(fluid.getId(),fluid);
        return fluid;
    }

    public static MedicalFluid get(String id) {
        return REGISTRY.get(id);
    }

}
