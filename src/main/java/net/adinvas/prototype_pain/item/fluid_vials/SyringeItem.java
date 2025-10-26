package net.adinvas.prototype_pain.item.fluid_vials;

import net.adinvas.prototype_pain.client.MinigameOpener;
import net.adinvas.prototype_pain.item.IAllowInMedicbags;
import net.adinvas.prototype_pain.item.IMedicalMinigameUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SyringeItem extends MedicalVial implements IAllowInMedicbags, IMedicalMinigameUsable {
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        appendOrigHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.syringe.discription").withStyle(ChatFormatting.GRAY));
        appendFluidListText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public void openMinigameScreen(Player target, ItemStack stack, @Nullable Limb limb, InteractionHand hand) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () ->{
            MinigameOpener.OpenSyringeMinigame(target,stack,limb,hand);
        });
    }

    @Override
    public void openMinigameBagScreen(Player target, ItemStack stack, ItemStack bagStack, int slot, @Nullable Limb limb, InteractionHand hand) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () ->{
            MinigameOpener.OpenSyringeMinigame(target,stack,bagStack,slot,limb,hand);
        });
    }

    @Override
    public void useMinigameAction(float durability, Player target, @Nullable Limb limb) {

    }
}
