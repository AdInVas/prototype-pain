package net.adinvas.prototype_pain.item.multi_tank;

import net.adinvas.prototype_pain.ModMedicalFluids;
import net.adinvas.prototype_pain.fluid_system.ModFluids;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;
import net.adinvas.prototype_pain.item.IAllowInMedicbags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class AntisepticSprayItem extends SprayBottleItem implements IAllowInMedicbags {
    @Override
    public void setupDefault(ItemStack pStack) {
        MultiTankHelper.addMedicalFluid(pStack,
                200,
                ModMedicalFluids.ANTISEPTIC.getId().toString(),
                new FluidStack(ModFluids.SRC_MEDICAL.get().getSource(),1));
    }

}
