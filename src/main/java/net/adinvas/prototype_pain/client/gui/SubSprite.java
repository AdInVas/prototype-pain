package net.adinvas.prototype_pain.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class SubSprite {
    private final ResourceLocation txt;
    private final int txtWidth,txtHeight;
    public float scale;
    private boolean visible=false;

    private float currentX, currentY;
    private float targetX, targetY;

    private float bobAmplitude = 1f; // pixels up/down
    private float bobSpeed = 0.02f; // speed of bobbing

    public int x, y;

    private int tickCount = 0;

    public SubSprite(StatusSprites sprite, float parentX, float parentY) {
        this.txt = sprite.getResourceLocation();
        this.txtWidth = 16;
        this.txtHeight = 16;
        this.currentX = this.targetX = parentX;
        this.currentY = this.targetY = parentY;
        this.scale = sprite.getBaseScale();
    }

    public float getScale() {
        return scale;
    }

    public void setTargetScale(float scale) {
        this.scale = scale;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setTargetPosition(float x, float y) {
        this.targetX = x;
        this.targetY = y;
    }
    public void forcePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void update() {
        tickCount++;

        // Smoothly interpolate position
        currentX += (targetX - currentX) * 0.2f;
        currentY += (targetY - currentY) * 0.2f;

        // Bobbing
        float bobOffset = (float) Math.sin(tickCount * bobSpeed) * bobAmplitude;
        x = Math.round(currentX);
        y = Math.round(currentY + bobOffset);
    }

    public void render(GuiGraphics guiGraphics) {
        if (!visible)return;
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        guiGraphics.blit(txt, (int) (x-(txtHeight/2*(scale-1))), (int) (y-(txtHeight/2*(scale-1))), 0, 0,
                (int)(txtWidth * scale),(int)(txtHeight * scale) ,
                (int)(txtWidth * scale), (int)(txtHeight * scale));
    }
}
