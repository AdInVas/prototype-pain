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
import net.minecraftforge.client.event.RenderLevelStageEvent;

import javax.annotation.Nullable;

public class BlindnessShaderOverlay implements IShaderOverlay {
    @Override
    public boolean shouldRender() {
        Minecraft mc = Minecraft.getInstance();
        boolean leftEye = mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::isLeftEyeBlind).orElse(false);
        boolean rightEye = mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::isRightEyeBlind).orElse(false);
        return rightEye||leftEye;
    }

    @Override
    public void render(@Nullable RenderLevelStageEvent event, RenderTarget input, RenderTarget output) {
        //if (!(event.getStage()== RenderLevelStageEvent.Stage.AFTER_LEVEL))return;
        Minecraft mc = Minecraft.getInstance();
        boolean leftEye = mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::isLeftEyeBlind).orElse(false);
        boolean rightEye = mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::isRightEyeBlind).orElse(false);

        ShaderInstance shader;
        if (rightEye&&!leftEye){
            shader = ClientShaderEvents.RIGHT_EYE_BLIND;
        } else if (leftEye&&!rightEye) {
            shader = ClientShaderEvents.LEFT_EYE_BLIND;
        } else if (leftEye) {
            shader = ClientShaderEvents.FULL_BLIND;
        } else {
            shader = null;
        }

        if (shader != null) {
            output.bindWrite(true);


            // Tell Minecraft to use our shader
            RenderSystem.setShaderTexture(0, input.getColorTextureId());
            RenderSystem.setShader(() -> shader);

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
