package net.adinvas.prototype_pain.item.bandages;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.client.MinigameOpener;

import net.adinvas.prototype_pain.item.IAllowInMedicbags;
import net.adinvas.prototype_pain.item.IMedicalMinigameUsable;
import net.adinvas.prototype_pain.item.INbtDrivenDurability;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.ChatFormatting;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MedicalGauzeItem extends Item implements IMedicalMinigameUsable, IAllowInMedicbags, INbtDrivenDurability {
    public MedicalGauzeItem() {
        super(new Properties().stacksTo(1));
    }


    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.medical_gauze.description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void openMinigameScreen( Player target, ItemStack stack, @Nullable Limb limb, InteractionHand hand) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () ->{
            MinigameOpener.OpenBandageMinigame(target,stack,limb,hand);
        });
    }
    @Override
    public void openMinigameBagScreen(Player target, ItemStack stack, ItemStack bagStack,int slot, @Nullable Limb limb, InteractionHand hand) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () ->{
            MinigameOpener.OpenBandageMinigame(target,stack,bagStack,slot,limb,hand);
        });
    }

    @Override
    public void useMinigameAction(float scalableAmount, Player target, @Nullable Limb limb) {
        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            h.addDelayedChange(((0.008f*scalableAmount)/20f)/60f,200,limb);
            float painRed = Math.max(0f, 1f - 0.08f * scalableAmount);
            h.setLimbPain(limb,h.getLimbPain(limb)*painRed);
            h.setLimbSkinHealth(limb,h.getLimbSkinHealth(limb)+0.15f*scalableAmount);
            h.setPendingOpioids(h.getPendingOpioids()+0.15f*scalableAmount);
        });
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

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        setupDefaults(pStack);
        super.onCraftedBy(pStack, pLevel, pPlayer);
    }

}
