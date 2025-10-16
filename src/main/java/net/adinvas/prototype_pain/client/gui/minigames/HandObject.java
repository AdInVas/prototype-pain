package net.adinvas.prototype_pain.client.gui.minigames;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class HandObject {
    public double x, y;     // current position
    public double vx, vy;   // velocity
    private double prevX, prevY;
    private double shakeX = 0;
    private double shakeY = 0;
    private final double shakeAmount = 24.0;
    private float shakeScale = 0;



    private double stiffness = 0.15; // how strongly it moves toward mouse
    private final double damping = 0.7;

    private int handStartX,handStartY;

    private boolean is_clicked=false;
    private float angle = 0;

    public HandObject(double startX, double startY, int handStartX, int handStartY) {
        this.x = startX;
        this.y = startY;
        this.handStartX = handStartX;
        this.handStartY = handStartY;
    }

    public boolean isIs_clicked() {
        return is_clicked;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public void setStiffness(double stiffness) {
        this.stiffness = stiffness;
    }

    public void setShakeScale(float shakeScale) {
        this.shakeScale = shakeScale;
    }


    public void update(double mouseX, double mouseY) {

        shakeX = ((Math.random() - 0.5) * 2 * shakeAmount)*shakeScale;
        shakeY = ((Math.random() - 0.5) * 2 * shakeAmount)*shakeScale;

        prevX = x;
        prevY = y;

        // --- Existing spring physics ---
        double dx = mouseX - x+shakeX;
        double dy = mouseY - y+shakeY;

        double ax = dx * stiffness;
        double ay = dy * stiffness;

        vx += ax;
        vy += ay;

        vx *= damping;
        vy *= damping;

        x += vx;
        y += vy;

        // update angle
        double relX = x - handStartX;
        double relY = y - handStartY;
        angle = (float) Math.atan2(relY, relX);

    }

    public void setHandStartX(int handStartX) {
        this.handStartX = handStartX;
    }

    public void setHandStartY(int handStartY) {
        this.handStartY = handStartY;
    }

    public void render(GuiGraphics guiGraphics,float partialTicks){
        double renderX = prevX + (x - prevX) * partialTicks;
        double renderY = prevY + (y - prevY) * partialTicks;

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();

        pose.translate(renderX, renderY, 0);
        pose.mulPose(Axis.ZP.rotation((float) angle));

        if (is_clicked){
            guiGraphics.blit(
                    new ResourceLocation(PrototypePain.MOD_ID, "textures/gui/limbs/arm_click.png"),
                    -40 * 4, -8 * 4, 0, 0, 48 * 4, 16 * 4, 48 * 4, 16 * 4
            );
        }else {
            guiGraphics.blit(
                    new ResourceLocation(PrototypePain.MOD_ID, "textures/gui/limbs/arm.png"),
                    -40 * 4, -8 * 4, 0, 0, 48 * 4, 16 * 4, 48 * 4, 16 * 4
            );
        }

        pose.popPose();
    }

    public void mouseClicked(){
        is_clicked =true;
    }
    public void mouseReleased(){
        is_clicked = false;
    }

}
