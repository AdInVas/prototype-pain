package net.adinvas.prototype_pain.item.multi_tank;

import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class BottleItem extends MultiTankFluidItem{

    public BottleItem(){
        super(new Properties().stacksTo(1).food(new FoodProperties.Builder().alwaysEat().build()));
    }

    @Override
    public int getCapacity() {
        return 1000;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (!(pLivingEntity instanceof ServerPlayer player))return pStack;
        int max = (int) Math.min(MultiTankHelper.getFilledTotal(pStack),100);
        List<FluidStack> drained = MultiTankHelper.drain(pStack,max);
        for (FluidStack fs : drained){
            MedicalFluid MF;
            String raw =fs.getRawFluid().getFluidType().toString();
            MF = Util.getFallback(raw);
            if (fs.hasTag()){
                if (fs.getTag().contains("MedicalId")){
                    MF = MedicalFluid.getFromId(fs.getTag().getString("MedicalId"),pLevel);
                }
            }
            MF.getMedicalEffect().applyIngested(player,fs.getAmount());
        }
        return pStack;
    }


}
