package net.adinvas.prototype_pain.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

public interface IOverlay {
    void render(GuiGraphics ms, float partialTicks);
    boolean shouldRender();
}
