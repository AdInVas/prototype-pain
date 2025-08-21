package net.adinvas.prototype_pain.client.gui;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class NarcoticWidget extends AbstractWidget {
    private boolean displayed =false;
    private boolean isHeld= false;
    private float amountleft = 1f;
    private float released = 0;

    private int holdTicks = 0;

    private ItemStack rememberItemstack;

    public NarcoticWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
    }

    public void setDisplay(float amountleft,ItemStack rememberItemstack){
        this.displayed= true;
        this.amountleft = amountleft;
        released = 0;
        this.rememberItemstack =rememberItemstack;
        this.holdTicks = 0;
    }

    public void setNull(){
        this.displayed = false;
        this.amountleft = 1;
        released = 0;
        this.rememberItemstack = ItemStack.EMPTY;
        this.holdTicks = 0;
    }
    public float getAmountleft() {
        return amountleft;
    }

    public float getReleased() {
        return released;
    }

    public boolean isHeld() {
        return isHeld;
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public ItemStack getRememberItemstack() {
        return rememberItemstack;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        if (!displayed)return;

        if (released>0){
            if (released++>20) {
                displayed = false;
                return;
            }
        }

        ResourceLocation TEX = new ResourceLocation(PrototypePain.MOD_ID, "textures/gui/inject_box.png");
        guiGraphics.blit(TEX, this.getX(), this.getY(), 0, 0, this.width, this.height,this.width, this.height);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font,rememberItemstack.getDisplayName(),this.getX()+64,this.getY()+5,0xFFFFFFFF);

            int barMinY = this.getY()+30;
            int barMaxY = this.getY()+36;
            int barMinX = this.getX()+16+(int)(96*amountleft);
            int barMaxX = this.getX()+16+96;

            guiGraphics.fill(barMinX,barMinY,barMaxX,barMaxY,0xFF212121);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font,Component.translatable("prototype_pain.gui.inject"),this.getX()+64,this.getY()+51,0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (displayed) {
            isHeld = true;
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        if (displayed && isHeld) {
            isHeld = false;
            released = 1;
        }
    }



    public void tick() {
        if (!displayed)return;
        if (isHeld) {
            holdTicks++;
            if (!isHovered()){
                isHeld=false;
                released++;
            }

            // Regression speed increases with time
            float  speed = 0.01f * (float)Math.pow(1.05, holdTicks);
            // starts slow (0.005) and ramps up steadily

            amountleft -= speed;
            if (amountleft < 0) amountleft = 0;
        }
    }
}
