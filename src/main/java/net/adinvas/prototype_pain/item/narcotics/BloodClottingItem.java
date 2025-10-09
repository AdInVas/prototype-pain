package net.adinvas.prototype_pain.item.narcotics;

import net.adinvas.prototype_pain.ModSounds;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.item.ISimpleMed;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.network.UseMedItemPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BloodClottingItem extends Item implements ISimpleMed {
    public BloodClottingItem() {
        super(new Properties().stacksTo(1).durability(4));
    }

    @Override
    public boolean onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack, InteractionHand hand) {
        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            for (Limb limb1:Limb.values()){
                h.setLimbBleedRate(limb1,h.getLimbBleedRate(limb1)*0.65f);
            }
            h.setInternalBleeding(h.getInternalBleeding()*0.4f);
            h.setBloodViscosity(h.getBloodViscosity()+20);
            ItemStack newItem = UseMedItemPacket.manualHurt(stack,1);
            source.setItemInHand(hand,newItem);
        });
        return true;
    }
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.blood_clotting.discription").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public SoundEvent getUseSound() {
        return ModSounds.PILL.get();
    }
}
