package net.adinvas.prototype_pain.item.dressings;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.item.IMedUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.network.UseMedItemPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IcePackItem extends Item implements IMedUsable {
    public IcePackItem() {
        super(new Item.Properties().durability(4));
    }

    @Override
    public boolean onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack, InteractionHand hand) {
        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            h.setLimbMuscleHealth(limb,h.getLimbMuscleHealth(limb)+35);
            h.setLimbDislocation(limb,h.getLimbDislocated(limb)*0.4f);
            h.setLimbPain(limb,h.getLimbPain(limb)*0.5f);
            h.setLimbMuscleHeal(limb,true);
            ItemStack newItem = UseMedItemPacket.manualHurt(stack,1);
            source.setItemInHand(hand,newItem);
        });
        return true;
    }
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.ice_pack.discription").withStyle(ChatFormatting.GRAY));
    }
}
