package net.adinvas.prototype_pain.client.overlays.exp;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.events.ClientShaderEvents;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.fml.common.Mod;

public class PainShaderOverlay implements IShaderOverlay {
    public static float prevPain = 0f;


    @Override
    public boolean shouldRender() {
        Minecraft mc = Minecraft.getInstance();
        float pain = (float)(mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA)
                .map(PlayerHealthData::getTotalPain).orElse(0d) / 100f);

        return pain>0.1;
    }

    public void render(RenderLevelStageEvent event, RenderTarget input, RenderTarget output) {
        Minecraft mc = Minecraft.getInstance();
        ShaderInstance shader = ClientShaderEvents.PAIN_SHADER;
        if (shader == null) return;

        output.bindWrite(true);

        float time = (mc.level.getGameTime() + event.getPartialTick()) * 0.05f;

        // interpolate between last tick and current tick values
        float pain = (float)(mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA)
                .map(PlayerHealthData::getTotalPain).orElse(0d) / 100f);
        float Consiousness = (mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA)
                .map(PlayerHealthData::getContiousness).orElse(0f));
        if (Consiousness<10){
            pain = 0.1f;
        }
        pain = Mth.clamp(pain, 0f, 1f);


        float displayedPain = Mth.lerp(0.1f, prevPain, pain);



        // Tell Minecraft to use our shader
        RenderSystem.setShaderTexture(0, input.getColorTextureId());
        RenderSystem.setShaderTexture(1, new ResourceLocation("prototype_pain:textures/shaders/pain_vignette.png"));
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


        prevPain = displayedPain;
    }
}
