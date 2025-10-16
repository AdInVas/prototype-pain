package net.adinvas.prototype_pain;

import net.adinvas.prototype_pain.fluid_system.MedicalFluid;

import java.util.Map;

public class Util {
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

    public static int getRedToGreenColor(float value) {
        // clamp between 0 and 1
        value = Math.max(0f, Math.min(1f, value));

        int red   = (int)((1 - value) * 255);
        int green = (int)(value * 255);
        int blue  = 0;
        int alpha = 255;

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
