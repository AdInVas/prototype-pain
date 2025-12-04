package net.adinvas.prototype_pain.item.misc;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrownCapMushItem extends Item {
    public BrownCapMushItem() {
        super(new Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(8)
                        .saturationMod(8)
                        .alwaysEat()
                        .build()));
    }


    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_EAT;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof ServerPlayer player){
            Random random = new Random();
            for (int i=0;i<9;i++){
                float maineffect = random.nextFloat(100f);
                if (maineffect<20){
                    player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()+3);
                    player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel()+3);
                }else if (maineffect<35) {
                    player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()+5);
                    player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel()+5);
                }else if (maineffect<50) {
                    player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()+3);
                    player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel()+3);
                    player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        for (Limb limb :Limb.values()){
                            h.setLimbInfection(limb,h.getLimbInfection(limb)-20);
                        }
                    });
                }else if (maineffect<60) {
                    player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()-3);
                    player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel()-3);
                }else if (maineffect<75) {
                    player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()+1);
                    player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel()+1);
                    player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        h.setPendingOpioids(h.getPendingOpioids()+35);
                    });
                }else if (maineffect<97.5) {
                    player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()+5);
                    player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel()+5);
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,100,0));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,3000,2));
                }else if (maineffect<99.5) {
                    player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()+1);
                    player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel()+1);
                    player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        h.setLimbMuscleHealth(Limb.CHEST,0);
                    });
                }else{
                    player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        List<Limb> amputated = new ArrayList<>();
                        for (Limb limb:Limb.values()){
                            if (h.isAmputated(limb))
                                amputated.add(limb);
                        }
                        if (!amputated.isEmpty()) {
                            Limb toFix = amputated.get(random.nextInt(amputated.size()));
                            for (Limb limb : toFix.getConnectedLimbs()) {
                                if (h.isAmputated(limb)) {
                                    h.setlimbAmputated(limb, false);
                                    h.setLimbPain(limb, 400);
                                }
                            }
                            h.setlimbAmputated(toFix, false);
                            h.setLimbPain(toFix, 400);
                        }else {
                            if (h.isRightEyeBlind()){
                                h.setRightEyeBlind(false);
                            } else if (h.isLeftEyeBlind()) {
                                h.setLeftEyeBlind(false);
                            }else if (h.isMouthRemoved()){
                                h.setMouthRemoved(false);
                            }
                        }
                    });
                }

                maineffect = random.nextFloat(100f);

                if (maineffect<10){
                    player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        h.setContiousness(0);
                    });
                } else if (maineffect<15) {
                    player.addEffect(new MobEffectInstance(MobEffects.WITHER,200,4));
                }else if (maineffect<25) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,200,4));
                }else if (maineffect<31) {
                    player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        h.setTemperature(h.getTemperature()-4.5f);
                    });
                }else if (maineffect<39) {
                    player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()+10);
                    player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel()+10);
                }else if (maineffect<49) {
                    player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()-20);
                    player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel()-20);
                }else if (maineffect<57) {
                    player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        h.setTemperature(h.getTemperature()+4.5f);
                    });
                }
            }
        }
        return super.finishUsingItem(pStack,pLevel,pLivingEntity);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 64;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack,pLevel,pTooltipComponents,pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.brown_cap_mush.discription").withStyle(ChatFormatting.GRAY));
    }
}
