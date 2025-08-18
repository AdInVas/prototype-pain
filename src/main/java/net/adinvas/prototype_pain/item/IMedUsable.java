package net.adinvas.prototype_pain.item;

import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public interface IMedUsable {
    boolean onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack, InteractionHand hand);
}
