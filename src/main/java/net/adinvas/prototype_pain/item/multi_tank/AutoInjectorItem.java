package net.adinvas.prototype_pain.item.multi_tank;

import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;
import net.adinvas.prototype_pain.item.ISimpleMedicalUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class AutoInjectorItem extends MultiTankFluidItem implements ISimpleMedicalUsable {
    @Override
    public int getCapacity() {
        return 100;
    }

    public int getInjectAmount(){
        return 34;
    }

    @Override
    public ItemStack onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack) {
        int max = (int) Math.min(MultiTankHelper.getFilledTotal(stack),getInjectAmount());
        List<FluidStack> drained = MultiTankHelper.drain(stack,max);
        for (FluidStack fs : drained){
            MedicalFluid MF;
            MF = Util.getFallback(fs.getFluid());
            if (fs.hasTag()){
                if (fs.getTag().contains("MedicalId")){
                    MF = MedicalFluid.getFromId(fs.getTag().getString("MedicalId"));
                }
            }
            MF.getMedicalEffect().applyInjected(target,fs.getAmount(),limb);
        }
        return stack;
    }
}
