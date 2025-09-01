package net.adinvas.prototype_pain.item.narcotics;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.item.INarcoticUsable;
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

public class MorphineItem extends Item implements INarcoticUsable {

    public MorphineItem() {
        super(new Properties().durability(100));
    }

    @Override
    public boolean onMedicalUse(float amountUsed, ServerPlayer source, ServerPlayer target, ItemStack stack, InteractionHand hand) {
        int damage = (int) (amountUsed*100);
        int MAX_OPIATES = 110;
        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            int opiates = (int) (amountUsed * MAX_OPIATES);
            h.setOpioids(h.getOpioids()+opiates);
            ItemStack newItem = UseMedItemPacket.manualHurt(stack,damage);
            source.setItemInHand(hand,newItem);
        });
        return true;
    }
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.morphine.discription").withStyle(ChatFormatting.GRAY));
    }
}
