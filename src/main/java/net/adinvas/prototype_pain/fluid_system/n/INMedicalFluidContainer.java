package net.adinvas.prototype_pain.fluid_system.n;

import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Map;

public interface INMedicalFluidContainer {
    float getCapacity(ItemStack stack);

    float getFilledTotal(ItemStack stack);

    ListTag getFluids(ItemStack stack);

    float getAmountOfFluid(ItemStack stack, MedicalFluid fluid);

    List<FluidStack> drain(ItemStack stack, float ml);

    void addFluid(ItemStack stack, float ml, MedicalFluid fluid);
    void addFluid(ItemStack stack, int mb, FluidStack fluidStack);

    void removeFluid(ItemStack stack, float ml, MedicalFluid fluid);

    float getFluidRatio(ItemStack stack,MedicalFluid fluid,float totalfluids);
}
