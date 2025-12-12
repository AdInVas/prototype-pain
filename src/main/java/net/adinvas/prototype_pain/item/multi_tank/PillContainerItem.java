package net.adinvas.prototype_pain.item.multi_tank;

import net.adinvas.prototype_pain.ModSounds;
import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class PillContainerItem extends MultiTankFluidItem{
    public PillContainerItem(){
        super(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().alwaysEat().build()));
    }

    @Override
    public int getCapacity() {
        return 100;
    }

    public int getUseAmount(){
        return 10;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (!(pLivingEntity instanceof ServerPlayer player))return pStack;
        int max = (int) Math.min(MultiTankHelper.getFilledTotal(pStack),getUseAmount());
        List<FluidStack> drained = MultiTankHelper.drain(pStack,max);
        for (FluidStack fs : drained){
            MedicalFluid MF;
            MF = Util.getFallback(fs.getFluid());
            if (fs.hasTag()){
                if (fs.getTag().contains("MedicalId")){
                    MF = MedicalFluid.getFromId(fs.getTag().getString("MedicalId"));
                }
            }
            MF.getMedicalEffect().applyIngested(player,fs.getAmount());
        }
        return pStack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.EAT;
    }

    @Override
    public SoundEvent getEatingSound() {
        return ModSounds.PILL.get();
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 15;
    }

}
