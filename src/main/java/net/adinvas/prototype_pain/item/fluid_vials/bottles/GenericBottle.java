package net.adinvas.prototype_pain.item.fluid_vials.bottles;

import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.adinvas.prototype_pain.item.ISimpleMedicalUsable;
import net.adinvas.prototype_pain.item.fluid_vials.MedicalVial;
import net.adinvas.prototype_pain.item.usable.LifeStrawItem;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Map;

public class GenericBottle extends MedicalVial implements ISimpleMedicalUsable {
    public GenericBottle() {
        super(new Item.Properties().stacksTo(1)
                .food(new FoodProperties.Builder()
                        .alwaysEat()
                        .build()));
    }

    public GenericBottle(Item.Properties properties) {
        super(properties);
    }

    @Override
    public float getCapacity(ItemStack stack) {
        return 500;
    }

    @Override
    public ItemStack onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack) {
        if (stack.getItem() instanceof MedicalVial vial){
            Map<MedicalFluid,Float> fluidmap = vial.drain(stack,100);
            for (MedicalFluid fluid : fluidmap.keySet()){
                float amount = fluidmap.get(fluid);
                fluid.getMedicalEffect().applyOnSkin(target,amount,limb);
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (pStack.getItem() instanceof MedicalVial vial&&pLivingEntity instanceof ServerPlayer player){
            Map<MedicalFluid,Float> fluidmap = vial.drain(pStack,100);
            for (MedicalFluid fluid : fluidmap.keySet()){
                float amount = fluidmap.get(fluid);
                MedicalFluid usedFluid =fluid;
                if (pLivingEntity.getMainHandItem().getItem() instanceof LifeStrawItem){
                    usedFluid = LifeStrawItem.convertFluid(fluid);
                } else if (pLivingEntity.getOffhandItem().getItem() instanceof LifeStrawItem) {
                    usedFluid = LifeStrawItem.convertFluid(fluid);
                }
                if (fluid!=usedFluid){
                    if (pLivingEntity.getMainHandItem().getItem() instanceof LifeStrawItem item){
                        item.setNbtDurability(pLivingEntity.getMainHandItem(),item.getNbtDurability(pLivingEntity.getMainHandItem())-amount/50);
                    } else if (pLivingEntity.getOffhandItem().getItem() instanceof LifeStrawItem item) {
                        item.setNbtDurability(pLivingEntity.getOffhandItem(),item.getNbtDurability(pLivingEntity.getOffhandItem())-amount/50);
                    }
                }

                usedFluid.getMedicalEffect().applyIngested(player,amount);
            }
        }
        return pStack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // First do a fluid raytrace (the same one buckets use)
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        float cap = getCapacity(stack);
        float amountOfFluids = getFilledTotal(stack);
        float amountToFill = Math.min(100, cap - amountOfFluids);
        if (hitResult.getType() == HitResult.Type.BLOCK&&amountToFill>0) {
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = level.getBlockState(pos);

            // Check for water source
            if (state.getBlock() == Blocks.WATER && state.getFluidState().isSource()) {


                addFluid(stack, amountToFill, MedicalFluids.DIRTY_WATER);

                // play a sound (optional)
                player.playSound(SoundEvents.BOTTLE_FILL, 1.0F, 1.0F);

                // Return success
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }

        // Fallback to normal drinking/use
        if (getFilledTotal(stack) > 0)
            player.startUsingItem(hand);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static BlockHitResult hitResultHelper(Level level, Player player){
        return getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
    }

}
