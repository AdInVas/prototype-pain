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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AutoPumpItem extends Item implements ISimpleMedicalUsable, IAllowInMedicbags, INbtDrivenDurability {
    public AutoPumpItem() {
        super(new Properties().stacksTo(1));
    }


    @Override
    public ItemStack onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack) {
        if (limb==Limb.CHEST){
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setLifeSupportTimer(5*60*20);
            });
            return ItemStack.EMPTY;
        }
        return stack;
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
        pTooltipComponents.add(Component.translatable("item.prototype_pain.auto_pump.description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public SoundEvent getUseSound() {
        return SoundEvents.BONE_MEAL_USE;
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        setupDefaults(pStack);
        super.onCraftedBy(pStack, pLevel, pPlayer);
    }
}
