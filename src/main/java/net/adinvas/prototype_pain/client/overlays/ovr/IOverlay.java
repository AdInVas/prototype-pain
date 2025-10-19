package net.adinvas.prototype_pain.client.overlays.ovr;

import net.minecraft.client.gui.GuiGraphics;

public interface IOverlay {
    void render(GuiGraphics ms, float partialTicks);
    boolean shouldRender();
}
