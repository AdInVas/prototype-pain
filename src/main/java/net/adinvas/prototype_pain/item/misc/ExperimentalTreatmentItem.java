package net.adinvas.prototype_pain.item.misc;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.item.IAllowInMedicbags;
import net.adinvas.prototype_pain.item.ISimpleMedicalUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

public class ExperimentalTreatmentItem extends Item implements IAllowInMedicbags,ISimpleMedicalUsable {
    public ExperimentalTreatmentItem() {
        super(new Properties().stacksTo(1).food(new FoodProperties.Builder().alwaysEat().build()));
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_EAT;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof ServerPlayer player){
            Random random = new Random();
            if (hasAmputated(player)){
                float roll = random.nextFloat(100);
                if (roll<50){
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
                } else if (roll<75) {
                    roll = random.nextFloat(100);
                    if (roll<2){
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setLimbMuscleHealth(Limb.HEAD,0);
                            h.setBrainHealth(29);
                        });
                    }else if (roll<10) {
                        player.sendSystemMessage(Component.literal("You fell a sudden sense of Doom"),true);
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb : Limb.values()){
                                h.setLimbInfection(limb,1);
                            }
                        });
                    }else if (roll<15) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb : Limb.values()){
                                h.applySkinDamage(limb,random.nextFloat(5));
                                h.applyBleedDamage(limb,random.nextFloat(5),player);
                            }
                        });
                    }else if (roll<25) {
                        player.getFoodData().setFoodLevel(0);
                        player.getFoodData().setSaturation(0);
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,400,20));
                    }else if (roll<35) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb : Limb.values()){
                                if (limb==Limb.CHEST)continue;
                                h.applyMuscleDamage(limb,random.nextFloat(20),player);
                            }
                        });
                    }else if (roll<39) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setPendingOpioids(100);
                        });
                    }else if (roll<54) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setTemperature(43);
                        });
                    }else if (roll<59) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setTemperature(25);
                        });
                    }else if (roll<64) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setHearingLoss(1);
                            h.setFlashHearingLoss(1);
                        });
                    }else if (roll<79) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            Limb limb = Limb.weigtedRandomLimb();
                            h.setLimbPain(limb,150);
                            h.setLimbSkinHealth(limb,0);
                            h.setLimbMuscleHealth(limb,0);
                        });
                    }else if (roll<84) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setShock(1);
                        });
                    }else if (roll<94) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            //h.setInternalBleeding(0.2f/20f/60f);
                        });
                    } else{
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setBloodVolume(3f);
                        });
                    }
                } else if (roll<100) {
                    roll = random.nextFloat(100);
                    if (roll<5){
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,200,5));
                    } else if (roll<15) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,200,5));
                    }else if (roll<25) {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,200,5));
                    }else if (roll<35) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setBloodVolume(5);
                            h.setBloodViscosity(0);
                            for (Limb limb :Limb.values()){
                                h.setLimbBleedRate(limb,0);
                            }
                            h.setInternalBleeding(0);
                        });
                    }else if (roll<45) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setPendingOpioids(0);
                            h.setOpioids(0);
                        });
                    }else if (roll<55) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb :Limb.values()){
                                h.setLimbSkinHealth(limb,100);
                                h.setLimbFracture(limb,0);
                                h.setLimbDislocation(limb,0);
                            }
                        });
                    }else if (roll<67) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb :Limb.values()){
                                h.setLimbMuscleHealth(limb,100);
                            }
                        });
                    }else if (roll<77) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setTemperature(36.6f);
                        });
                    }else if (roll<87) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setLastStand(false);
                        });
                    }else if (roll<90) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb :Limb.values()){
                                h.setLimbDesinfected(limb,10*60*20);
                            }
                            h.setAntibiotic_timer(10*60*20);
                        });
                    }else if (roll<95) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setHearingLoss(0);
                        });
                    }else{
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb :Limb.values()){
                                h.setLimbInfection(limb,0);
                            }
                        });
                    }
                }

            }else {
                float roll = random.nextFloat(100);
                if (roll<50){
                    roll = random.nextFloat(100);
                    if (roll<2){
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setLimbMuscleHealth(Limb.HEAD,0);
                            h.setBrainHealth(29);
                        });
                    }else if (roll<10) {
                        player.sendSystemMessage(Component.literal("You fell a sudden sense of Doom"),true);
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb : Limb.values()){
                                h.setLimbInfection(limb,1);
                            }
                        });
                    }else if (roll<15) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb : Limb.values()){
                                h.applySkinDamage(limb,random.nextFloat(5));
                                h.applyBleedDamage(limb,random.nextFloat(5),player);
                            }
                        });
                    }else if (roll<25) {
                        player.getFoodData().setFoodLevel(0);
                        player.getFoodData().setSaturation(0);
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,400,20));
                    }else if (roll<35) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb : Limb.values()){
                                if (limb==Limb.CHEST)continue;
                                h.applyMuscleDamage(limb,random.nextFloat(20),player);
                            }
                        });
                    }else if (roll<39) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                           h.setPendingOpioids(100);
                        });
                    }else if (roll<54) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setTemperature(43);
                        });
                    }else if (roll<59) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setTemperature(25);
                        });
                    }else if (roll<64) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setHearingLoss(1);
                            h.setFlashHearingLoss(1);
                        });
                    }else if (roll<79) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            Limb limb = Limb.weigtedRandomLimb();
                            h.setLimbPain(limb,150);
                            h.setLimbSkinHealth(limb,0);
                            h.setLimbMuscleHealth(limb,0);
                        });
                    }else if (roll<84) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setShock(1);
                        });
                    }else if (roll<94) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            //h.setInternalBleeding(0.2f/20f/60f);
                        });
                    } else if (roll<99) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setBloodVolume(3f);
                        });
                    }else{
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                           List<Limb> ampulist = new ArrayList<>();
                           ampulist.add(Limb.LEFT_ARM);
                           ampulist.add(Limb.HEAD);
                           ampulist.add(Limb.LEFT_HAND);
                           ampulist.add(Limb.RIGHT_HAND);
                           ampulist.add(Limb.RIGHT_ARM);
                           ampulist.add(Limb.LEFT_FOOT);
                           ampulist.add(Limb.LEFT_LEG);
                           ampulist.add(Limb.RIGHT_LEG);
                           ampulist.add(Limb.RIGHT_FOOT);

                           Limb limb = ampulist.get(random.nextInt(ampulist.size()));
                           if (limb==Limb.HEAD){
                               if (!h.isRightEyeBlind()){
                                   h.setRightEyeBlind(true);
                               } else if (!h.isLeftEyeBlind()) {
                                   h.setLeftEyeBlind(true);
                               }else{
                                   h.setMouthRemoved(true);
                               }
                           }else{
                               h.dismember(limb);
                           }
                        });
                    }
                } else if (roll<100) {
                    roll = random.nextFloat(100);
                    if (roll<5){
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,200,5));
                    } else if (roll<15) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,200,5));
                    }else if (roll<25) {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,200,5));
                    }else if (roll<35) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setBloodVolume(5);
                            h.setBloodViscosity(0);
                            for (Limb limb :Limb.values()){
                                h.setLimbBleedRate(limb,0);
                            }
                            h.setInternalBleeding(0);
                        });
                    }else if (roll<45) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                           h.setPendingOpioids(0);
                           h.setOpioids(0);
                        });
                    }else if (roll<55) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb :Limb.values()){
                                h.setLimbSkinHealth(limb,100);
                                h.setLimbFracture(limb,0);
                                h.setLimbDislocation(limb,0);
                            }
                        });
                    }else if (roll<67) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb :Limb.values()){
                                h.setLimbMuscleHealth(limb,100);
                            }
                        });
                    }else if (roll<77) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setTemperature(36.6f);
                        });
                    }else if (roll<87) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setLastStand(false);
                        });
                    }else if (roll<90) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb :Limb.values()){
                                h.setLimbDesinfected(limb,10*60*20);
                            }
                            h.setAntibiotic_timer(10*60*20);
                        });
                    }else if (roll<95) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            h.setHearingLoss(0);
                        });
                    }else{
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            for (Limb limb :Limb.values()){
                                h.setLimbInfection(limb,0);
                            }
                        });
                    }
                }
            }
        }
        return super.finishUsingItem(pStack,pLevel,pLivingEntity);
    }

    public boolean hasAmputated(Player player){
        return player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(
                h->{
                    for (Limb limb :Limb.values()){
                        if (h.isAmputated(limb))return true;
                    }
                    if (h.isMouthRemoved())return true;
                    if (h.isLeftEyeBlind())return true;
                    if (h.isRightEyeBlind())return true;
                    return false;
                }
        ).orElse(false);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 100;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }


    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack,pLevel,pTooltipComponents,pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.experimental_treatment.discription1").withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.translatable("item.prototype_pain.experimental_treatment.discription2").withStyle(ChatFormatting.AQUA));
        pTooltipComponents.add(Component.translatable("item.prototype_pain.experimental_treatment.discription3").withStyle(ChatFormatting.DARK_GRAY));
        pTooltipComponents.add(Component.translatable("item.prototype_pain.experimental_treatment.discription4").withStyle(ChatFormatting.GRAY));
        if (Screen.hasShiftDown()&&pIsAdvanced.isAdvanced()){
            pTooltipComponents.add(Component.literal("A small Line on the bottom says: \"If found return to Doctor Ry** \" the rest is not readable.").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public ItemStack onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack) {
        if (limb==Limb.HEAD){
            return this.finishUsingItem(stack,target.level(),target);
        }
        return stack;
    }
}
