package net.adinvas.prototype_pain.item;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class TourniquetItem extends Item implements IMedUsable{
    public TourniquetItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack, InteractionHand hand) {
        if (limb!=Limb.CHEST) {
            AtomicBoolean used = new AtomicBoolean(false);
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                if (!h.getTourniquet(limb)){
                    h.setTourniquet(limb, true);
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
}
