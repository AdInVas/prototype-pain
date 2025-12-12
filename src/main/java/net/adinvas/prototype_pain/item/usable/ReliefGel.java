package net.adinvas.prototype_pain.item.usable;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.item.IAllowInMedicbags;
import net.adinvas.prototype_pain.item.INbtDrivenDurability;
import net.adinvas.prototype_pain.item.ISimpleMedicalUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReliefGel extends Item implements ISimpleMedicalUsable, IAllowInMedicbags, INbtDrivenDurability {

    public ReliefGel() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public ItemStack onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack) {
        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            h.setPendingOpioids(h.getPendingOpioids()+1);
            h.setLimbPain(limb,h.getLimbPain(limb)-5);
            h.setLimbDesinfected(limb,300);
            h.setLimbMuscleHeal(limb,true);
            h.setLimbMuscleHealth(limb,h.getLimbMuscleHealth(limb)+10);

        });
        ItemStack newitemstack = stack;
        setNbtDurability(stack,getNbtDurability(stack)-20);
        if (getNbtDurability(stack)<=0){
            newitemstack = ItemStack.EMPTY;
        }
        return newitemstack;
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
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.aid_gel.description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        setupDefaults(pStack);
        super.onCraftedBy(pStack, pLevel, pPlayer);
    }
}
