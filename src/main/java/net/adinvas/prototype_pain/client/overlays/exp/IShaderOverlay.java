package net.adinvas.prototype_pain.client.overlays.exp;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraftforge.client.event.RenderLevelStageEvent;

public interface IShaderOverlay {
    abstract boolean shouldRender();
    abstract void render(RenderLevelStageEvent event,RenderTarget input,RenderTarget output);

}
