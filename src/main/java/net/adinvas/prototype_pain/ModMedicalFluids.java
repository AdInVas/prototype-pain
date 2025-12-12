package net.adinvas.prototype_pain;

import net.adinvas.prototype_pain.fluid_system.FallbackMedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MedicalEffects;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

public class ModMedicalFluids {
    public static final DeferredRegister<MedicalFluid> MEDICAL_FLUIDS =
            DeferredRegister.create(ModMedicalRegistry.MEDICAL_FLUIDS_KEY, PrototypePain.MOD_ID);

    public static final RegistryObject<MedicalFluid> OPIUM = MEDICAL_FLUIDS.register("opium",() -> new MedicalFluid(MedicalEffects.OPIUM, 0xeb4034));
    public static final RegistryObject<MedicalFluid> FENTANYL = MEDICAL_FLUIDS.register("fentanyl",()->new MedicalFluid(MedicalEffects.FENTANYL,0xa1d9ff));
    public static final RegistryObject<MedicalFluid> MORPHINE = MEDICAL_FLUIDS.register("morphine", () -> new MedicalFluid(MedicalEffects.MORPHINE, 0x632329));
    public static final RegistryObject<MedicalFluid> HEROIN = MEDICAL_FLUIDS.register("heroin", () -> new MedicalFluid(MedicalEffects.HEROIN, 0xedf8ff));
    public static final RegistryObject<MedicalFluid> PAINKILLERS = MEDICAL_FLUIDS.register("painkillers", () -> new MedicalFluid(MedicalEffects.PAINKILLERS, 0x888888));
    public static final RegistryObject<MedicalFluid> BRAINGROW = MEDICAL_FLUIDS.register("braingrow", () -> new MedicalFluid(MedicalEffects.BRAINGROW, 0x915946));
    public static final RegistryObject<MedicalFluid> ALCOHOL = MEDICAL_FLUIDS.register("alcohol", () -> new MedicalFluid(MedicalEffects.ALCOHOL, 0x828282));
    public static final RegistryObject<MedicalFluid> ANTISEPTIC = MEDICAL_FLUIDS.register("antiseptic", () -> new MedicalFluid(MedicalEffects.ANTISEPTIC, 0x5a6b45));
    public static final RegistryObject<MedicalFluid> RELIEF_CREAM = MEDICAL_FLUIDS.register("relief_cream", () -> new MedicalFluid(MedicalEffects.RELIEF_CREAM, 0x75644d));
    public static final RegistryObject<MedicalFluid> SALINE = MEDICAL_FLUIDS.register("saline", () -> new MedicalFluid(MedicalEffects.SALINE, 0xc9c8c5));
    public static final RegistryObject<MedicalFluid> ANTIBIOTICS = MEDICAL_FLUIDS.register("antibiotics", () -> new MedicalFluid(MedicalEffects.ANTIBIOTICS, 0x593f8a));
    public static final RegistryObject<MedicalFluid> ANTISERUM = MEDICAL_FLUIDS.register("antiserum", () -> new MedicalFluid(MedicalEffects.ANTISERUM, 0x6f3582));
    public static final RegistryObject<MedicalFluid> CEFTRIAXONE = MEDICAL_FLUIDS.register("ceftriaxone", () -> new MedicalFluid(MedicalEffects.CEFTRAIAXONE, 0x184a19));
    public static final RegistryObject<MedicalFluid> PROCOAGULANT = MEDICAL_FLUIDS.register("procoagulant", () -> new MedicalFluid(MedicalEffects.PROCOAGULANT, 0x57172b));
    public static final RegistryObject<MedicalFluid> STREPTOKINASE = MEDICAL_FLUIDS.register("streptokinase", () -> new MedicalFluid(MedicalEffects.STREPTOKINASE, 0x0aecfc));
    public static final RegistryObject<MedicalFluid> NALOXONE = MEDICAL_FLUIDS.register("naloxone", () -> new MedicalFluid(MedicalEffects.NALOXONE, 0xf2abff));
    public static final RegistryObject<MedicalFluid> REACTION_LIQUID = MEDICAL_FLUIDS.register("reaction_liquid", () -> new MedicalFluid(MedicalEffects.WATER, 0xbceb23));
    public static final RegistryObject<MedicalFluid> CLEAN_WATER = MEDICAL_FLUIDS.register("clean_water",()->new MedicalFluid(MedicalEffects.WATER,0x5276d1));
    // Generic variant
    public static final RegistryObject<MedicalFluid> VANILLA_WATER = MEDICAL_FLUIDS.register("v_water", () -> new FallbackMedicalFluid(MedicalEffects.WATER, 0x0349fc));
    public static final RegistryObject<MedicalFluid> VANILLA_LAVA = MEDICAL_FLUIDS.register("v_lava", () -> new FallbackMedicalFluid(MedicalEffects.VANILLA_LAVA, 0xfc4103));
    public static final RegistryObject<MedicalFluid> GENERIC_HOT = MEDICAL_FLUIDS.register("generic_hot", () -> new FallbackMedicalFluid(MedicalEffects.VANILLA_LAVA, 0xa31a1a));
    public static final RegistryObject<MedicalFluid> GENERIC_TOXIC = MEDICAL_FLUIDS.register("generic_toxic", () -> new FallbackMedicalFluid(MedicalEffects.WATER, 0x658c03));
    public static final RegistryObject<MedicalFluid> GENERIC_BAD = MEDICAL_FLUIDS.register("generic_bad", () -> new FallbackMedicalFluid(MedicalEffects.WATER, 0x292e13));












    public static void register(IEventBus bus) {
        MEDICAL_FLUIDS.register(bus);
    }

}
