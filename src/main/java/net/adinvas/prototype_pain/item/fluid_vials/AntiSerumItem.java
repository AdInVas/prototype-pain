package net.adinvas.prototype_pain.item.fluid_vials;

import net.adinvas.prototype_pain.ModSounds;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.adinvas.prototype_pain.item.IAllowInMedicbags;
import net.adinvas.prototype_pain.item.ISimpleMedicalUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class AntiSerumItem extends MedicalVial implements ISimpleMedicalUsable, IAllowInMedicbags {
    public AntiSerumItem() {
        super();
    }

    @Override
    public ItemStack onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack) {
        if (stack.getItem() instanceof MedicalVial vial){
            Map<MedicalFluid,Float> fluidmap = vial.drain(stack,50);
            for (MedicalFluid fluid : fluidmap.keySet()){
                float amount = fluidmap.get(fluid);
                fluid.getMedicalEffect().applyInjected(target,amount,limb);
            }
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        appendOrigHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.antiserum.discription").withStyle(ChatFormatting.GRAY));
        appendFluidListText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public SoundEvent getUseSound() {
        return ModSounds.SYRINGE_USE.get();
    }

    @Override
    public void setupDefaults(ItemStack stack) {
        addFluid(stack,100, MedicalFluids.ANTISERUM);
    }
}
