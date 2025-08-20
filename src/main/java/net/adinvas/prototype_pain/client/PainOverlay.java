package net.adinvas.prototype_pain.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PainOverlay implements IOverlay{

    private float baseIntensity = .4f;   // baseline value (0-1)
    private float pulseSpeed = 2.5f;      // cycles per second
    private float pulseStrength = 1f; // how
    private float strength = 0f;
    private double lastIntensity = 0f;

    /** Call this every time you want to change the intensity (0-1) */
    public void setIntensity(float intensity) {
        this.strength = Mth.clamp(intensity, 0f, 1f);
    }

    @Override
    public void render(GuiGraphics ms, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        // --- Compute pulsing intensity ---
        double time = (mc.level.getGameTime() + partialTicks) / 20.0; // 20 ticks = 1 second
        float pulse = (float)(Math.sin(time * Math.PI * pulseSpeed) * 0.5 + 0.5f);
        double intensity = strength*baseIntensity * (1.0f - pulseStrength + pulse * pulseStrength);
        if (intensity<strength*baseIntensity){
            intensity*=2;
        }
        lastIntensity = Mth.lerp(0.01,lastIntensity,intensity);

        // --- Render vignette ---
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);


        ms.setColor(1F, 1F, 1F, (float) lastIntensity);
        ms.blit(
                new ResourceLocation(PrototypePain.MOD_ID, "textures/overlay/vignette.png"),
                0, 0,                   // screen position
                0, 0,                   // UV start
                width, height,          // draw size
                width, height           // texture size
        );

        ms.setColor(1f, 1f, 1f, 1f); // reset color

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean shouldRender() {
        return baseIntensity > 0f;
    }
}
