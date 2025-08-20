package net.adinvas.prototype_pain.client.gui;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HealthInfoBoxWidget extends AbstractWidget {
    private Component name;
    private float contiousness;
    private float pain;
    private float blood;
    private float bleed;
    private float oxygen;
    private float opiates;

    private Component limbname =Component.empty();
    private float skin=100;
    private float muscle=100;
    private float fracture;
    private float dislocated;
    private float pain2;
    private float bleed2;
    private float infection = 90;

    private final ResourceLocation main_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/info_box.png");
    private final ResourceLocation blood_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/blood_drop.png");
    private final ResourceLocation pain_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/pain.png");
    private final ResourceLocation infection_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/inf.png");
    private final ResourceLocation bloodBag_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/blood_bag.png");



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

    int tickCounter=-20;
    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        tickCounter++;
        int startoffset = 0;
        guiGraphics.blit(main_tex,getX()+startoffset,getY(),0,0,128,196,128,196);
        Minecraft mc = Minecraft.getInstance();
        guiGraphics.pose().pushPose();
        guiGraphics.drawCenteredString(mc.font,name,64,6,0xFFFFFF);
        guiGraphics.drawCenteredString(mc.font,limbname,64,146,0xFFFFFF);
        guiGraphics.pose().scale(0.7f,0.7f,1);
        guiGraphics.drawString(mc.font,"SKIN",7,224,0xFFFFFF);
        guiGraphics.drawString(mc.font,"MUSCLE",7  ,235,0xFFFFFF);
        guiGraphics.drawString(mc.font,"FRACT",7,249,0xFFFFFF);
        guiGraphics.drawString(mc.font,"DISL",7  ,261,0xFFFFFF);
        guiGraphics.blit(pain_tex,93,246,0,0,10,10,10,10);
        guiGraphics.blit(infection_tex,93,258,0,0,10,10,10,10);



        if (infection>25){
            if (infection>80){
                if ((tickCounter % 10) < 5) {
                    guiGraphics.drawString(mc.font, (int) infection +"%",110,259,0xFF0000);
                }
            }else {
                guiGraphics.drawString(mc.font, (int) infection +"%",110,259,0xFFFFFF);
            }
        }
        if (pain2>50){
            if ((tickCounter % 10) < 5) {
                guiGraphics.drawCenteredString(mc.font,String.valueOf((int)pain2),110,247,0xFF0000);
            }
        }else {
            guiGraphics.drawCenteredString(mc.font,String.valueOf((int)pain2),110,247,0xFFFFFF);
        }
        if (fracture>0){
            guiGraphics.drawCenteredString(mc.font,(int)(fracture)+"%",77,249,0xFF0000);
        }else {
            guiGraphics.drawCenteredString(mc.font,"----",77,249,0xFFFFFF);
        }
        if (fracture>0){
            guiGraphics.drawCenteredString(mc.font,(int)dislocated+"%",77,261,0xFF0000);
        }else {
            guiGraphics.drawCenteredString(mc.font,"----",77,261,0xFFFFFF);
        }
        if (skin<10) {
            if ((tickCounter % 10) < 5) {
                guiGraphics.drawCenteredString(mc.font, String.valueOf((int) skin), 77, 224, 0xFF0000);
            }
        }else {
            guiGraphics.drawCenteredString(mc.font, String.valueOf((int) skin), 77, 224, 0xFFFFFF);
        }
        if (muscle<10) {
            if ((tickCounter % 10) < 5) {
                guiGraphics.drawCenteredString(mc.font, String.valueOf((int) muscle), 77, 235, 0xFF0000);
            }
        }else {
            guiGraphics.drawCenteredString(mc.font, String.valueOf((int) muscle), 77, 235, 0xFFFFFF);
        }
        guiGraphics.pose().popPose();

        guiGraphics.fill(65,158, (int) (66+(41*(skin/100))),160, 0xFFFFFFFF);
        guiGraphics.fill(65,166, (int) (66+(41*(muscle/100))),168, 0xFFFFFFFF);



        if (tickCounter>20)tickCounter=0;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
