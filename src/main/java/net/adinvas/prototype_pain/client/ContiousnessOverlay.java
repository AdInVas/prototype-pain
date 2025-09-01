package net.adinvas.prototype_pain.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ContiousnessOverlay implements IOverlay{
    private float intensity= 1;

    private float lastInt=0;


    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    @Override
    public void render(GuiGraphics ms, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        intensity = (float) Mth.lerp(0.1,lastInt,intensity);
        lastInt = intensity;
        if (intensity<1){

            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();

            // Use multiplicative blending so white is transparent, black darkens screen
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
            float intensity2 = (float) Math.pow(intensity,2);
            ms.setColor(intensity2, intensity2, intensity2, 1f);
            ms.blit(
                    new ResourceLocation("minecraft", "textures/misc/vignette.png"),
                    0, 0,
                    0, 0,
                    width, height,
                    width, height
            );
        }
        ms.setColor(1F, 1F, 1F, (float) Math.pow(intensity-0.1f,8));
        ms.fill(0,0,width,height,0xFF000000);
        ms.setColor(1F, 1F, 1F, 1);
        if (intensity>0.95){
            Component text = Component.translatable("prototype_pain.gui.give_up",Component.keybind("key.protoype_pain.give_up"));
            ms.drawCenteredString(mc.font,text,width/2,height/2,0xFFFFFF);
        }
    }

    @Override
    public boolean shouldRender() {
        return intensity>0;
    }
}
