package net.adinvas.prototype_pain.item.multi_tank;

import net.adinvas.prototype_pain.ModMedicalFluids;
import net.adinvas.prototype_pain.fluid_system.ModFluids;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class BrainGrowPillItem extends PillContainerItem{
    @Override
    public void setupDefault(ItemStack pStack) {
        MultiTankHelper.addMedicalFluid(pStack,
                50,
                ModMedicalFluids.BRAINGROW.getId().toString(),
                new FluidStack(ModFluids.SRC_MEDICAL.get().getSource(),1));
    }

    @Override
    public int getCapacity() {
        return 50;
    }
}
