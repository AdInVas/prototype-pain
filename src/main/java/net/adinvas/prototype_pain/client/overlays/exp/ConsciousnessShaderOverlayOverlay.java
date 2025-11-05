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
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import javax.annotation.Nullable;

public class ConsciousnessShaderOverlayOverlay implements IShaderOverlay {
    public float lastValue=0;
    @Override
    public boolean shouldRender() {
        Minecraft mc = Minecraft.getInstance();
        float consciousness = mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getContiousness).orElse(100f);
        return consciousness<100;
    }

    @Override
    public void render(@Nullable RenderLevelStageEvent event, RenderTarget input, RenderTarget output) {
        Minecraft mc = Minecraft.getInstance();
        float consciousness = mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getContiousness).orElse(100f);
        ShaderInstance shader = ClientShaderEvents.CONSCIOUSNESS_SHADER;
        if (shader != null) {
            float intensity = Mth.clamp((100-consciousness)/100f, 0f, 1f);
            float displayedIntensity = Mth.lerp(0.1f,lastValue,intensity);
            lastValue = displayedIntensity;
            output.bindWrite(true);



            // Tell Minecraft to use our shader
            RenderSystem.setShaderTexture(0, input.getColorTextureId());
            RenderSystem.setShader(() -> shader);

            shader.safeGetUniform("Intensity").set(displayedIntensity);
            shader.safeGetUniform("BlurScaleX").set(1f/mc.getWindow().getScreenWidth());
            shader.safeGetUniform("BlurScaleY").set(1f/mc.getWindow().getScreenHeight());


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
        }
    }
}
