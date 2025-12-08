package net.adinvas.prototype_pain.fluid_system.n;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, PrototypePain.MOD_ID);
    public static final RegistryObject<FluidType> MEDICAL_TYPE =
            FLUID_TYPES.register("medical", () -> new MedicalFluidType(FluidType.Properties.create()));
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, PrototypePain.MOD_ID);

    public static RegistryObject<FlowingFluid> SRC_MEDICAL = FLUIDS.register("medical_fluid",
            ()->new ForgeFlowingFluid.Source(ModFluids.MEDICAL_PROPERTIES));

    public static final ForgeFlowingFluid.Properties MEDICAL_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluids.MEDICAL_TYPE,SRC_MEDICAL,SRC_MEDICAL);


    public static void register(IEventBus bus){
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
    }

}
