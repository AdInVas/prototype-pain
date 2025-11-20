package net.adinvas.prototype_pain.fluid_system;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class NewFluidSystem {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, PrototypePain.MOD_ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, PrototypePain.MOD_ID);

    public static final RegistryObject<FluidType> MORPHINE_TYPE = FLUID_TYPES.register("morphine",
            () -> new FluidType(FluidType.Properties.create()));
}
