package net.adinvas.prototype_pain.item;

import net.adinvas.prototype_pain.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public interface INarcoticUsable {
    boolean onMedicalUse(float amountUsed, ServerPlayer source, ServerPlayer target, ItemStack stack, InteractionHand hand);
    default SoundEvent getUseSound(){
        return ModSounds.SYRINGE_USE.get();
    };
}
