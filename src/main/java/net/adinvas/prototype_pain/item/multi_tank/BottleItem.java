package net.adinvas.prototype_pain.item.multi_tank;

import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;
import net.adinvas.prototype_pain.item.ISimpleMedicalUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class BottleItem extends MultiTankFluidItem implements ISimpleMedicalUsable {

    public BottleItem(){
        super(new Properties().stacksTo(1).food(new FoodProperties.Builder().alwaysEat().build()));
    }

    @Override
    public int getCapacity() {
        return 250;
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

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // First do a fluid raytrace (the same one buckets use)
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        float cap = MultiTankHelper.getCapacity(stack);
        float amountOfFluids = MultiTankHelper.getFilledTotal(stack);
        float amountToFill = Math.min(100, cap - amountOfFluids);
        if (hitResult.getType() == HitResult.Type.BLOCK&&amountToFill>0) {
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = level.getBlockState(pos);

            // Check for water source
            if (state.getBlock() == Blocks.WATER && state.getFluidState().isSource()) {


                MultiTankHelper.addFluid(stack,100,new FluidStack(Fluids.WATER,1));

                // play a sound (optional)
                player.playSound(SoundEvents.BOTTLE_FILL, 1.0F, 1.0F);

                // Return success
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }

        // Fallback to normal drinking/use
        if (MultiTankHelper.getFilledTotal(stack) > 0)
            player.startUsingItem(hand);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }


}
