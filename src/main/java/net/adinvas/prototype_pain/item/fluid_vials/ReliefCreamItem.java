package net.adinvas.prototype_pain.item.fluid_vials;

import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.adinvas.prototype_pain.item.IAllowInMedicbags;
import net.adinvas.prototype_pain.item.ISimpleMedicalUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ReliefCreamItem extends MedicalVial implements ISimpleMedicalUsable, IAllowInMedicbags {
    public ReliefCreamItem() {
        super();
    }

    @Override
    public float getCapacity(ItemStack stack) {
        return 100;
    }

    @Override
    public boolean onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack, InteractionHand hand) {
        if (stack.getItem() instanceof MedicalVial vial){
            Map<MedicalFluid,Float> fluidmap = vial.drain(stack,10);
            for (MedicalFluid fluid : fluidmap.keySet()){
                float amount = fluidmap.get(fluid);
                fluid.getMedicalEffect().applyOnSkin(target,amount,limb);
            }
        }
        return true;
    }

    @Override
    public void setupDefaults(ItemStack stack) {
        addFluid(stack,100, MedicalFluids.RELIEF_CREAM);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        appendOrigHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.relief_cream.discription").withStyle(ChatFormatting.GRAY));
        appendFluidListText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
