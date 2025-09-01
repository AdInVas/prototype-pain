package net.adinvas.prototype_pain.item.reusable;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.item.IMedUsable;
import net.adinvas.prototype_pain.limbs.Limb;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class SplintItem extends Item implements IMedUsable {
    public SplintItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack, InteractionHand hand) {
        if (limb!=Limb.CHEST) {
            AtomicBoolean used = new AtomicBoolean(false);
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                if (!h.hasLimbSplint(limb)){
                    h.setLimbSplint(limb, true);
                    used.set(true);
                    source.setItemInHand(hand,ItemStack.EMPTY);
                }
            });
            if (used.get()){
                return true;
            }
        }
        return false;
    }
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.splint.discription").withStyle(ChatFormatting.GRAY));
    }
}
