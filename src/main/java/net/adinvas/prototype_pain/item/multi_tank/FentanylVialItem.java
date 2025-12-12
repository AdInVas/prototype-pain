package net.adinvas.prototype_pain.item.multi_tank;

import net.adinvas.prototype_pain.ModMedicalFluids;
import net.adinvas.prototype_pain.fluid_system.ModFluids;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FentanylVialItem extends MedicineVialItem{
    @Override
    public void setupDefault(ItemStack pStack) {
        MultiTankHelper.addMedicalFluid(pStack,
                10,
                ModMedicalFluids.FENTANYL.getId().toString(),
                new FluidStack(ModFluids.SRC_MEDICAL.get().getSource(),1));
        MultiTankHelper.addMedicalFluid(pStack,
                90,
                ModMedicalFluids.CLEAN_WATER.getId().toString(),
                new FluidStack(ModFluids.SRC_MEDICAL.get().getSource(),1));
    }
}
