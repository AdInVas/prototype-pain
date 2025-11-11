package net.adinvas.prototype_pain.client.gui;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class HealthInfoBoxWidget extends AbstractWidget {
    private Component name;
    private float contiousness = 0;
    private float pain;
    private float blood;
    private float bleed;
    private float oxygen;
    private float opiates;
    private float brain;
    private float immunity;
    private float temp;

    private Component limbname =Component.empty();
    private float skin=100;
    private float muscle=100;
    private float fracture;
    private float dislocated;
    private float pain2;
    private float bleed2;
    private float infection = 100;

    private boolean BGMode = false;

    public void setBGMode(boolean BGMode) {
        this.BGMode = BGMode;
    }

    public void setBrain(float brain) {
        this.brain = brain;
    }

    public void setImmunity(float immunity) {
        this.immunity = immunity;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    private final ResourceLocation main_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/health_screen/info_box.png");

    public HealthInfoBoxWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
    }

    public void setName(Component name) {
        this.name = name;
    }

    public void setSkin(float value){
        skin = value;
    }

    public void setPain2(float pain2) {
        this.pain2 = pain2;
    }

    public void setMuscle(float muscle) {
        this.muscle = muscle;
    }

    public void setLimbname(Limb limb) {
        this.limbname = limb.getComponent();
    }

    public void setInfection(float infection) {
        this.infection = infection;
    }

    public void setContiousness(float contiousness) {
        this.contiousness = contiousness;
    }

    public void setBleed2(float bleed2) {
        this.bleed2 = bleed2;
    }

    public void setPain(float pain) {
        this.pain = pain;
    }

    public void setBlood(float blood) {
        this.blood = blood;
    }

    public void setBleed(float bleed) {
        this.bleed = bleed;
    }

    public void setOpiates(float opiates) {
        this.opiates = opiates;
    }

    public void setOxygen(float oxygen) {
        this.oxygen = oxygen;
    }

    public void setFracture(float fracture) {
        this.fracture = fracture;
    }

    public void setDislocated(float dislocated) {
        this.dislocated = dislocated;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        int colorWhite = 0xFFFFFF;
        int colorRed = 0xFF0000;
        if (BGMode){
            colorWhite = 0x777777;
            colorRed = 0x770000;
        }

        float time = (Minecraft.getInstance().level.getGameTime() + v) / 20f; // seconds
        boolean blink = (int)(time * 6) % 2 == 0; // toggle every 0.5s

        int startoffset = 0;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.625f,0.625f,1);
        guiGraphics.blit(main_tex,getX()+startoffset,getY(),0,0,196,392,196,392);
        Font font = Minecraft.getInstance().font;
        Component text = Component.literal("000")
                .setStyle(Style.EMPTY.withFont(new ResourceLocation(PrototypePain.MOD_ID, "health_screen")));
        guiGraphics.drawString(Minecraft.getInstance().font,text,100,100,0xFFFFFF,false);
        guiGraphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
