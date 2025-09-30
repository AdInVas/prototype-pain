package net.adinvas.prototype_pain.limbs;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum Limb { HEAD, CHEST, LEFT_ARM, RIGHT_ARM,RIGHT_HAND,LEFT_HAND, LEFT_LEG, RIGHT_LEG, LEFT_FOOT, RIGHT_FOOT;

    public Limb randomFromConectedLimb(){
        List<Limb> temp_Limb_list= new ArrayList<>();
        switch (this){
            case CHEST -> {
                temp_Limb_list.add(Limb.HEAD);
                temp_Limb_list.add(Limb.LEFT_ARM);
                temp_Limb_list.add(Limb.LEFT_LEG);
                temp_Limb_list.add(Limb.RIGHT_ARM);
                temp_Limb_list.add(Limb.RIGHT_LEG);
            }
            case LEFT_ARM -> {
                temp_Limb_list.add(Limb.CHEST);
                temp_Limb_list.add(Limb.LEFT_HAND);
            }
            case LEFT_HAND -> {
                return Limb.LEFT_ARM;
            }
            case RIGHT_ARM -> {
                temp_Limb_list.add(Limb.CHEST);
                temp_Limb_list.add(Limb.RIGHT_HAND);
            }
            case RIGHT_HAND -> {
                return Limb.RIGHT_ARM;
            }
            case LEFT_LEG -> {
                temp_Limb_list.add(Limb.CHEST);
                temp_Limb_list.add(Limb.LEFT_FOOT);
            }
            case RIGHT_LEG -> {
                temp_Limb_list.add(Limb.CHEST);
                temp_Limb_list.add(Limb.RIGHT_FOOT);
            }
            case LEFT_FOOT -> {
                return Limb.LEFT_LEG;
            }
            case RIGHT_FOOT -> {
                return  Limb.RIGHT_LEG;
            }
            case HEAD -> {
                return Limb.CHEST;
            }
        }
        Limb[] limb_list = temp_Limb_list.toArray(new Limb[]{});
        Random rand = new Random();
        return limb_list[rand.nextInt(limb_list.length)];
    }

    static Limb randomLimb(){
        Limb[] values = Limb.values();
        Random rand = new Random();
        return values[rand.nextInt(values.length)];
    }


    static Limb weigtedRandomLimb(){
        Limb[] limb_list = {
                HEAD, //~4%
                CHEST,//~8%
                CHEST,
                RIGHT_ARM,//~12%
                RIGHT_ARM,
                RIGHT_ARM,
                LEFT_ARM,//~12%
                LEFT_ARM,
                LEFT_ARM,
                RIGHT_LEG,//~12%
                RIGHT_LEG,
                RIGHT_LEG,
                LEFT_LEG,//~12%
                LEFT_LEG,
                LEFT_LEG,
                RIGHT_HAND,//~8%
                RIGHT_HAND,
                LEFT_HAND,//~8%
                LEFT_HAND,
                RIGHT_FOOT,//~8%
                RIGHT_FOOT,
                LEFT_FOOT,//~8%
                LEFT_FOOT,
        };
        Random rand = new Random();
        return limb_list[rand.nextInt(limb_list.length)];
    }

    public Component getComponent(){
        return switch (this){
            case CHEST -> Component.translatable("prototype_pain.limb.chest");
            case LEFT_FOOT -> Component.translatable("prototype_pain.limb.lfoot");
            case HEAD -> Component.translatable("prototype_pain.limb.head");
            case RIGHT_LEG -> Component.translatable("prototype_pain.limb.rleg");
            case RIGHT_FOOT -> Component.translatable("prototype_pain.limb.rfoot");
            case LEFT_LEG -> Component.translatable("prototype_pain.limb.lleg");
            case LEFT_ARM -> Component.translatable("prototype_pain.limb.larm");
            case LEFT_HAND -> Component.translatable("prototype_pain.limb.lhand");
            case RIGHT_ARM -> Component.translatable("prototype_pain.limb.rarm");
            case RIGHT_HAND -> Component.translatable("prototype_pain.limb.rhand");
            default -> Component.empty();
        };
    }


    static InteractionHand getRightHand(Player player){
        if (player.getMainArm()== HumanoidArm.RIGHT){
            return InteractionHand.MAIN_HAND;
        }
        return InteractionHand.OFF_HAND;
    }

    static InteractionHand getLeftHand(Player player){
        if (player.getMainArm()== HumanoidArm.LEFT){
            return InteractionHand.MAIN_HAND;
        }
        return InteractionHand.OFF_HAND;
    }

    public static HumanoidArm getFromHand(InteractionHand hand, Player player){
        HumanoidArm mainArm = player.getMainArm();
        if (hand == InteractionHand.MAIN_HAND) {
            // MAIN_HAND always uses the player's dominant arm
            return mainArm;
        } else {
            // OFF_HAND is always the opposite of the main arm
            return (mainArm == HumanoidArm.RIGHT) ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        }
    }

}

