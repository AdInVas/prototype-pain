package net.adinvas.prototype_pain.client.overlays.exp;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.events.ClientShaderEvents;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderLevelStageEvent;

public class HeatOverlay implements IShaderOverlay {
    public static float prevTemp = 0f;


    @Override
    public boolean shouldRender() {
        Minecraft mc = Minecraft.getInstance();
        float temp = (float)(mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA)
                .map(PlayerHealthData::getTemperature).orElse(36.6f) );

        return temp>40;
    }

    public void render(RenderLevelStageEvent event, RenderTarget input, RenderTarget output) {
        Minecraft mc = Minecraft.getInstance();
        ShaderInstance shader = ClientShaderEvents.HEAT;
        if (shader == null) return;

        output.bindWrite(true);

        float time = (mc.player.level().getGameTime() + event.getPartialTick()) * 0.05f;

        // interpolate between last tick and current tick values
        float temp = (mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA)
                .map(PlayerHealthData::getTemperature).orElse(36.6f) );

        temp = Mth.clamp((temp-40)/2, 0f, 1f);


        float displayedPain = Mth.lerp(0.1f, prevTemp, temp);



        // Tell Minecraft to use our shader
        RenderSystem.setShaderTexture(0, input.getColorTextureId());
        RenderSystem.setShader(() -> shader);
        shader.safeGetUniform("Intensity").set(displayedPain);
        shader.safeGetUniform("Time").set(time);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buf = tesselator.getBuilder();
        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        // Fullscreen quad (clip-space coordinates)
        buf.vertex(-1.0, -1.0, 0).uv(0.0f, 0.0f).endVertex();
        buf.vertex( 1.0, -1.0, 0).uv(1.0f, 0.0f).endVertex();
        buf.vertex( 1.0,  1.0, 0).uv(1.0f, 1.0f).endVertex();
        buf.vertex(-1.0,  1.0, 0).uv(0.0f, 1.0f).endVertex();

        BufferUploader.drawWithShader(buf.end());


        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.setShaderTexture(1, 0);


        prevTemp = displayedPain;
    }
}
