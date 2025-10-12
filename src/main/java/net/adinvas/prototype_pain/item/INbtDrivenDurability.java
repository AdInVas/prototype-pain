package net.adinvas.prototype_pain.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface INbtDrivenDurability {
    default float getNbtDurability(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getFloat("Durability");
    };

    default float getMaxNbtDurability(ItemStack stack){
        return 100f;
    };

    default void setNbtDurability(ItemStack stack,float value){
        CompoundTag tag = stack.getOrCreateTag();
        tag.putFloat("Durability",value);
    };

    default void setupDefaults(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        tag.putFloat("Durability",getMaxNbtDurability(stack));
    };

    default float getNbtDurabilityRatio(ItemStack stack){
        float maxDur = getMaxNbtDurability(stack);
        float currDur = getNbtDurability(stack);
        return currDur/maxDur;
    }


}
