package net.adinvas.prototype_pain;

import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class Util {
    public static int mixColors(Map<Integer, Float> colorRatios) {
        float total = 0f;
        float r = 0f, g = 0f, b = 0f;

        for (var entry : colorRatios.entrySet()) {
            int color = entry.getKey();
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

    public static int gradient(float t, int colorA, int colorB) {
        t = Math.max(0f, Math.min(1f, t)); // clamp just in case

        int aA = (colorA >> 24) & 0xFF;
        int rA = (colorA >> 16) & 0xFF;
        int gA = (colorA >> 8) & 0xFF;
        int bA = colorA & 0xFF;

        int aB = (colorB >> 24) & 0xFF;
        int rB = (colorB >> 16) & 0xFF;
        int gB = (colorB >> 8) & 0xFF;
        int bB = colorB & 0xFF;

        int a = (int)(aA + (aB - aA) * t);
        int r = (int)(rA + (rB - rA) * t);
        int g = (int)(gA + (gB - gA) * t);
        int b = (int)(bA + (bB - bA) * t);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int darken(int color, float factor) {
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (r << 16) | (g << 8) | b;
    }

    public static int getColorFromFluid(FluidStack fluid, Level level){
        int color = 0xFFFFFF;
        MedicalFluid mF = getFallback(fluid.getFluid());
        if (fluid.hasTag()){
            if (fluid.getTag().contains("MedicalId")){
                mF =  MedicalFluid.getFromId(fluid.getTag().getString("MedicalId"));
                if (mF==null){
                    return color;
                }
                color = mF.getColor();
            }
        }
        return mF.getColor();
    }


    public static MedicalFluid getFallback(Fluid fs){
        String name = fs.getFluidType().toString();
        if (name.contains("lava"))return ModMedicalFluids.VANILLA_LAVA.get();
        if (name.contains("water"))return ModMedicalFluids.VANILLA_WATER.get();
        if (name.contains("molten")||name.contains("metal")||name.contains("iron")||name.contains("steel"))return ModMedicalFluids.GENERIC_HOT.get();
        if (name.contains("toxic")||name.contains("poison"))return ModMedicalFluids.GENERIC_TOXIC.get();
        return ModMedicalFluids.GENERIC_BAD.get();
    }


}
