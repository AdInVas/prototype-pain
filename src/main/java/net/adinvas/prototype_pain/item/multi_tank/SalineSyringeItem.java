package net.adinvas.prototype_pain.item.multi_tank;

import net.adinvas.prototype_pain.ModMedicalFluids;
import net.adinvas.prototype_pain.fluid_system.ModFluids;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class SalineSyringeItem extends SyringeItem{
    @Override
    public int getCapacity() {
        return 750;
    }

    @Override
    public void setupDefault(ItemStack pStack) {
        MultiTankHelper.addMedicalFluid(pStack,
                750,
                ModMedicalFluids.SALINE.getId().toString(),
                new FluidStack(ModFluids.SRC_MEDICAL.get().getSource(),1));
    }
}
