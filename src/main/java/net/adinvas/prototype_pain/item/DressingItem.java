package net.adinvas.prototype_pain.item;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.network.UseMedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DressingItem extends Item implements IMedUsable {
    public DressingItem(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public boolean onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack, InteractionHand hand) {
        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            h.setLimbSkinHealth(limb,h.getLimbSkinHealth(limb)+10);
            h.setLimbFracture(limb,h.getLimbFracture(limb)*0.8f);
            h.setLimbBleedRate(limb,h.getLimbBleedRate(limb)*0.2f);
            h.setLimbPain(limb,h.getLimbPain(limb)*0.8f);
            ItemStack newItem = UseMedItemPacket.manualHurt(stack,1);
            source.setItemInHand(hand,newItem);
        });
        return true;
    }
}
