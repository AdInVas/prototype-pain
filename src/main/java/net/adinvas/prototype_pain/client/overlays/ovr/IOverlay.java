package net.adinvas.prototype_pain.client.overlays.ovr;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public interface IOverlay {
    void render(GuiGraphics ms, float partialTicks);
    boolean shouldRender();
    void calculate(Player player);
}
