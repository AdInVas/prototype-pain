package net.adinvas.prototype_pain.item.multi_tank;

import net.adinvas.prototype_pain.ModMedicalFluids;
import net.adinvas.prototype_pain.fluid_system.ModFluids;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ReliefCreamBottle extends BottleItem{
    @Override
    public int getCapacity() {
        return 200;
    }

    @Override
    public int getOnSkinAmount() {
        return 10;
    }

    @Override
    public int getDrinkingAmount() {
        return 50;
    }

    @Override
    public void setupDefault(ItemStack pStack) {
        MultiTankHelper.addMedicalFluid(pStack,
                200,
                ModMedicalFluids.RELIEF_CREAM.getId().toString(),
                new FluidStack(ModFluids.SRC_MEDICAL.get().getSource(),1));
    }
}
