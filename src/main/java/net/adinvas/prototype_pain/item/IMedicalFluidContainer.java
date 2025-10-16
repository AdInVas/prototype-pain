package net.adinvas.prototype_pain.item;

import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public interface IMedicalFluidContainer {
    float getCapacity(ItemStack stack);

    float getFilledTotal(ItemStack stack);

    ListTag getFluids(ItemStack stack);

    float getAmountOfFluid(ItemStack stack, MedicalFluid fluid);

    Map<MedicalFluid,Float> drain(ItemStack stack, float ml);

    void addFluid(ItemStack stack, float ml, MedicalFluid fluid);
    void removeFluid(ItemStack stack, float ml, MedicalFluid fluid);

    float getFluidRatio(ItemStack stack,MedicalFluid fluid,float totalfluids);

}
