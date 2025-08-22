package net.adinvas.prototype_pain.item;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.network.UseMedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class OpiumItem extends Item implements INarcoticUsable {

    public OpiumItem() {
        super(new Properties().durability(100));
    }

    @Override
    public boolean onMedicalUse(float amountUsed, ServerPlayer source, ServerPlayer target, ItemStack stack, InteractionHand hand) {
        int damage = (int) (amountUsed*100);
        int MAX_OPIATES = 50;
        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            int opiates = (int) (amountUsed * MAX_OPIATES);
            h.setOpioids(h.getOpioids()+opiates);
            ItemStack newItem = UseMedItemPacket.manualHurt(stack,damage);
            source.setItemInHand(hand,newItem);
        });
        return false;
    }
}
