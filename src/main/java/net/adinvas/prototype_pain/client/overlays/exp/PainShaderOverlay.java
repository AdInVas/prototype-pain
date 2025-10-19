package net.adinvas.prototype_pain.client.overlays.exp;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.events.ClientShaderEvents;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PainShaderOverlay implements IShaderOverlay {
    public static float prevPain = 0f;

    public void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ShaderInstance shader = ClientShaderEvents.PAIN_SHADER;
        if (shader == null) return;

        float time = (mc.level.getGameTime() + event.getPartialTick()) * 0.05f;

        // interpolate between last tick and current tick values
        float pain = (float)(mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA)
                .map(PlayerHealthData::getTotalPain).orElse(0d) / 100f);
        pain = Mth.clamp(pain, 0f, 1f);

        float displayedPain = Mth.lerp(0.1f, prevPain, pain);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Tell Minecraft to use our shader
        RenderSystem.setShaderTexture(0, mc.getMainRenderTarget().getColorTextureId());
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

        // 2. Explicitly disable blending
        RenderSystem.disableBlend();

        // 3. Reset the shader
        RenderSystem.setShader(GameRenderer::getPositionTexShader); // <--- You have this, which is good

        RenderSystem.enableDepthTest(); // <--- You have this, which is good
        RenderSystem.depthMask(true); // <--- You have this, which is good

        // 4. IMPORTANT: Ensure the main FBO is bound for subsequent rendering
        mc.getMainRenderTarget().bindWrite(false);
        prevPain = displayedPain;
    }
}
