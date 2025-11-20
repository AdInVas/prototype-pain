package net.adinvas.prototype_pain.item.usable;

import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.adinvas.prototype_pain.item.INbtDrivenDurability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LifeStrawItem extends Item implements INbtDrivenDurability {
    public LifeStrawItem() {
        super(new Properties().stacksTo(1));
    }

    public ItemStack drinkAction(ItemStack stack, ServerPlayer player, MedicalFluid fluid, float ml){
        MedicalFluid converted = convertFluid(fluid);
        converted.getMedicalEffect().applyIngested(player,ml);
        setNbtDurability(stack,getNbtDurability(stack)-ml/50);
        if (getNbtDurability(stack)<=0){
            stack.setCount(0);
        }
        return stack;
    }

    public static MedicalFluid convertFluid(MedicalFluid fluid){
        if (fluid ==MedicalFluids.DIRTY_WATER)return MedicalFluids.WATER;


        return fluid;
    }

    @Override
    public Component getName(ItemStack pStack) {
        Component finalcomp = super.getName(pStack);
        finalcomp = Component.empty().append(finalcomp)
                .append(Component.literal(" (").withStyle(ChatFormatting.GRAY))
                .append(Component.literal((int)(getNbtDurabilityRatio(pStack)*100)+"%").withStyle(Style.EMPTY.withColor(Util.getRedToGreenColor(getNbtDurabilityRatio(pStack)))))
                .append(Component.literal(")").withStyle(ChatFormatting.GRAY));
        return finalcomp;
    }

}
