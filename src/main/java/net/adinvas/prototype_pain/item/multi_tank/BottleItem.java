package net.adinvas.prototype_pain.item.multi_tank;

import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;
import net.adinvas.prototype_pain.item.ISimpleMedicalUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class BottleItem extends MultiTankFluidItem implements ISimpleMedicalUsable {

    public BottleItem(){
        super(new Properties().stacksTo(1).food(new FoodProperties.Builder().alwaysEat().build()));
    }

    @Override
    public int getCapacity() {
        return 400;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (!(pLivingEntity instanceof ServerPlayer player))return pStack;
        int max = (int) Math.min(MultiTankHelper.getFilledTotal(pStack), getDrinkingAmount());
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
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 15;
    }

    public int getDrinkingAmount(){
        return 100;
    }

    public int getOnSkinAmount(){
        return 100;
    }

    @Override
    public ItemStack onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack) {
        int max = (int) Math.min(MultiTankHelper.getFilledTotal(stack),getOnSkinAmount());
        List<FluidStack> drained = MultiTankHelper.drain(stack,max);
        for (FluidStack fs : drained){
            MedicalFluid MF;
            MF = Util.getFallback(fs.getFluid());
            if (fs.hasTag()){
                if (fs.getTag().contains("MedicalId")){
                    MF = MedicalFluid.getFromId(fs.getTag().getString("MedicalId"));
                }
            }
            MF.getMedicalEffect().applyOnSkin(target,fs.getAmount(),limb);
        }
        return stack;
    }


}
