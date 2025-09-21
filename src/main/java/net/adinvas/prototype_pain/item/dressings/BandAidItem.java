package net.adinvas.prototype_pain.item.dressings;

import net.adinvas.prototype_pain.ModSounds;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.item.IMedUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.network.UseMedItemPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BandAidItem extends Item implements IMedUsable {
    public BandAidItem() {
        super(new Properties().durability(6));
    }


    @Override
    public boolean onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack, InteractionHand hand) {
        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            h.setLimbSkinHealth(limb,h.getLimbSkinHealth(limb)+3);
            h.addDelayedChange(((0.1f)/20f)/60f,100,limb);
            h.setLimbPain(limb,h.getLimbPain(limb)*0.90f);
            ItemStack newItem = UseMedItemPacket.manualHurt(stack,1);
            source.setItemInHand(hand,newItem);
        });
        return true;
    }
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.band_aid.discription").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public SoundEvent getUseSound() {
        return SoundEvents.BONE_MEAL_USE;
    }
}
