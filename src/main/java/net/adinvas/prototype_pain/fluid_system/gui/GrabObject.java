package net.adinvas.prototype_pain.fluid_system.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class GrabObject {
    protected int x, y; // logical position
    protected int hitX, hitY, hitWidth, hitHeight;
    protected final ResourceLocation tex;
    protected final int texWidth, texHeight;
    protected boolean dragging = false;
    protected int dragOffsetX, dragOffsetY;
    protected final float scale;


    public GrabObject(int x, int y, int hitX, int hitY, int hitWidth, int hitHeight,
                      ResourceLocation tex, int texWidth, int texHeight, float scale) {
        this.x = x;
        this.y = y;
        this.tex = tex;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitWidth = hitWidth;
        this.hitHeight = hitHeight;
        this.scale = scale;
    }

    public void render(GuiGraphics guiGraphics) {
        var pose = guiGraphics.pose();
        pose.pushPose();

        // Translate to object position
        pose.translate(x, y, 0);

        // Draw texture at 0,0 in translated coordinates
        guiGraphics.blit(tex,
                0, 0, // top-left at translated origin
                0f, 0f,
                (int) (texWidth * scale), (int) (texHeight * scale),
                (int) (texWidth * scale), (int) (texHeight * scale));

        pose.popPose();
    }

    // --- Position setters ---
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    // --- Dragging ---
    public boolean isDragging() { return dragging; }
    public void setDragging(boolean dragging) { this.dragging = dragging; }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isInside(mouseX, mouseY) && button == 0) { // left click
            dragging = true;
            dragOffsetX = (int) mouseX - x;
            dragOffsetY = (int) mouseY - y;
        }
    }

    public boolean isInside(double mouseX, double mouseY) {
        int absHitX = (int) (x + hitX * scale);
        int absHitY = (int) (y + hitY * scale);
        return mouseX >= absHitX && mouseX <= absHitX + hitWidth * scale
                && mouseY >= absHitY && mouseY <= absHitY + hitHeight * scale;
    }

    public void mouseDragged(double mouseX, double mouseY, int button) {
        if (dragging && button == 0) {
            this.x = (int) mouseX - dragOffsetX;
            this.y = (int) mouseY - dragOffsetY;
        }
    }
}


