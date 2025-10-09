package net.adinvas.prototype_pain.fluid_system.items;

import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;

public class SyringeItem extends MedicalVial {
    public static int mixColors(Map<MedicalFluid, Float> colorRatios) {
        float total = 0f;
        float r = 0f, g = 0f, b = 0f;

        for (var entry : colorRatios.entrySet()) {
            int color = entry.getKey().getColor();
            float weight = entry.getValue();

            r += ((color >> 16) & 0xFF) * weight;
            g += ((color >> 8) & 0xFF) * weight;
            b += (color & 0xFF) * weight;
            total += weight;
        }

        // Normalize by total weight
        if (total == 0f) total = 1f; // avoid divide-by-zero
        r /= total;
        g /= total;
        b /= total;

        // Clamp to 0–255 just in case
        int ri = Math.min(255, Math.max(0, Math.round(r)));
        int gi = Math.min(255, Math.max(0, Math.round(g)));
        int bi = Math.min(255, Math.max(0, Math.round(b)));

        return (ri << 16) | (gi << 8) | bi;
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
    }
}
