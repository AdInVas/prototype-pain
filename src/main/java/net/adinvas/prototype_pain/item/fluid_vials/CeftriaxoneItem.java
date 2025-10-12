package net.adinvas.prototype_pain.item.fluid_vials;

import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.adinvas.prototype_pain.item.IAllowInMedicbags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CeftriaxoneItem extends MedicalVial implements IAllowInMedicbags {

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        super.onCraftedBy(pStack, pLevel, pPlayer);
        if (!pStack.hasTag()) {
            addFluid(pStack, 100, MedicalFluids.CEFTRAIAXONE);
        }
    }

    @Override
    public void setupDefaults(ItemStack stack) {
        addFluid(stack, 100, MedicalFluids.CEFTRAIAXONE);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        appendOrigHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.ceftriaxone.discription").withStyle(ChatFormatting.GRAY));
        appendFluidListText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

}
