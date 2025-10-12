package net.adinvas.prototype_pain.client.gui;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
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

    private final ResourceLocation main_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/info_box.png");
    private final ResourceLocation blood_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/blood_drop.png");
    private final ResourceLocation pain_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/pain.png");
    private final ResourceLocation infection_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/inf.png");
    private final ResourceLocation bloodBag_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/blood_bag.png");
    private final ResourceLocation contiousness_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/conc_sprites.png");
    private final ResourceLocation opiate_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/opiate_meter.png");
    private final ResourceLocation brain_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/brain.png");

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
        guiGraphics.blit(main_tex,getX()+startoffset,getY(),0,0,128,196,128,196);
        Minecraft mc = Minecraft.getInstance();
        guiGraphics.pose().pushPose();
        guiGraphics.drawCenteredString(mc.font,name,64,6,colorWhite);
        guiGraphics.drawCenteredString(mc.font,limbname,64,146,colorWhite);

        guiGraphics.blit(brain_tex,4,123,0,0,16,16,16,16);
        guiGraphics.fill(23,133, (int) (23+(85*(brain/100))),137,0xFFFFFFFF);

        float minTemp = 27f;  // coldest possible
        float maxTemp = 43f;

        float normalized = (temp - minTemp) / (maxTemp - minTemp);

        int barTop = 109 - (int)(normalized * 28);


        guiGraphics.fill(94,barTop,95,109,0xFFFFFFFF);
        guiGraphics.drawString(mc.font,Math.floor(temp*10)/10+"",98,100,colorWhite);


        float blitV = 5;
        if (contiousness<=0) blitV = 4;
        else if (contiousness<=25) blitV = 3;
        else if (contiousness<=75) blitV = 2;
        else if (contiousness<100) blitV = 1;
        else blitV = 0;
        guiGraphics.blit(contiousness_tex,7,16,0,blitV*16,16,16,16,80);
        if(contiousness<40){
            if (blink) {
                guiGraphics.drawCenteredString(mc.font,(int)contiousness+"%",7+32,22,colorRed);
                guiGraphics.drawString(mc.font,"CONSCIOUS",7+32+15,22,colorWhite);
            }
        }else {
            guiGraphics.drawCenteredString(mc.font,(int)contiousness+"%",7+32,22,colorWhite);
            guiGraphics.drawString(mc.font,"CONSCIOUS",7+32+15,22,colorWhite);
        }

        guiGraphics.blit(pain_tex,7,32,0,0,16,16,16,16);

        if(pain>60){
            if (blink) {
                guiGraphics.drawCenteredString(mc.font,(int)pain+"% PAIN",7+32+15,36,colorRed);
            }
        }else {
            guiGraphics.drawCenteredString(mc.font,(int)pain+"% PAIN",7+32+15,36,colorWhite);
        }

        if (blood>=5){
            blitV = 10;
        } else if (blood>=4.5) {
            blitV = 9;
        }else if (blood>=4) {
            blitV = 8;
        }else if (blood>=3.5) {
            blitV = 7;
        }else if (blood>=3) {
            blitV = 6;
        }else if (blood>=2.5) {
            blitV = 5;
        }else if (blood>=2) {
            blitV = 4;
        }else if (blood>=1.5) {
            blitV = 3;
        }else if (blood>=1) {
            blitV = 2;
        }else if (blood>=0.5) {
            blitV = 1;
        }else {
            blitV = 0;
        }
        guiGraphics.blit(bloodBag_tex,5,50,0,(blitV-1)*32,32,32,32,320);

        guiGraphics.drawString(mc.font,String.format("%.2f",blood)+"L",40,52,colorWhite);


        if (bleed>0.2f/20f/60f){
            if (blink) {
                guiGraphics.drawString(mc.font,String.format("%.2f",bleed*20*60)+"L/m",40,66,colorRed);
                guiGraphics.setColor(1,0,0,1);
                guiGraphics.blit(blood_tex,76,64,0,0,16,16,16,16);
                guiGraphics.setColor(1,1,1,1);
            }
        }else if (bleed>0f){
            guiGraphics.drawString(mc.font,String.format("%.2f",bleed*20*60)+"L/m",40,66,colorWhite);
            guiGraphics.blit(blood_tex,76,64,0,0,10,10,10,10);
        }
        guiGraphics.drawString(mc.font,"O₂ "+(int)(oxygen)+"%",5,85,colorWhite);
        guiGraphics.drawString(mc.font,""+(int)(immunity),17,104,colorWhite);

        if (opiates>0){
            guiGraphics.blit(opiate_tex,104,64,0,0,16,32,16,32);
            if (opiates>100){
                if (blink) {
                    guiGraphics.fill(110,72,113,72+(int)((Math.min(opiates,100)/100)*18),0xFFFFFFFF);
                }
            }else {
                guiGraphics.fill(110,72,113,72+(int)((Math.min(opiates,100)/100)*18),0xFFFFFFFF);
            }
        }


        guiGraphics.pose().scale(0.7f,0.7f,1);
        guiGraphics.drawString(mc.font,String.valueOf((int)brain),33,177,colorWhite);
        guiGraphics.drawString(mc.font,"SKIN",7,224,colorWhite);
        guiGraphics.drawString(mc.font,"MUSCLE",7  ,235,colorWhite);
        guiGraphics.drawString(mc.font,"FRACT",7,249,colorWhite);
        guiGraphics.drawString(mc.font,"DISL",7  ,261,colorWhite);
        guiGraphics.blit(pain_tex,93,246,0,0,10,10,10,10);
        guiGraphics.blit(infection_tex,93,258,0,0,10,10,10,10);
        guiGraphics.blit(blood_tex,164,258,0,0,10,10,10,10);



        if (bleed2>0.2f/20/60){
            if (blink) {
                guiGraphics.drawString(mc.font,String.format("%.2f",bleed2*20*60) +"L/m",126,260,colorRed);
            }
        }else {
            guiGraphics.drawString(mc.font, String.format("%.2f",bleed2*20*60)+"L/m",126,260,colorWhite);
        }


        if (infection>25){
            if (infection>80){
                if (blink) {
                    guiGraphics.drawCenteredString(mc.font, (int) infection +"",111,260,colorRed);
                }
            }else {
                guiGraphics.drawCenteredString(mc.font, (int) infection +"",111,260,colorWhite);
            }
        }
        if (pain2>50){
            if (blink) {
                guiGraphics.drawCenteredString(mc.font,String.valueOf((int)pain2),111,247,colorRed);
            }
        }else {
            guiGraphics.drawCenteredString(mc.font,String.valueOf((int)pain2),111,247,colorWhite);
        }
        if (fracture>0){
            guiGraphics.drawCenteredString(mc.font,(int)(fracture)+"%",77,249,colorRed);
        }else {
            guiGraphics.drawCenteredString(mc.font,"----",77,249,colorWhite);
        }
        if (fracture>0){
            guiGraphics.drawCenteredString(mc.font,(int)dislocated+"%",77,261,colorRed);
        }else {
            guiGraphics.drawCenteredString(mc.font,"----",77,261,colorWhite);
        }
        if (skin<10) {
            if (blink) {
                guiGraphics.drawCenteredString(mc.font, String.valueOf((int) skin), 77, 224, colorRed);
            }
        }else {
            guiGraphics.drawCenteredString(mc.font, String.valueOf((int) skin), 77, 224, colorWhite);
        }
        if (muscle<10) {
            if (blink) {
                guiGraphics.drawCenteredString(mc.font, String.valueOf((int) muscle), 77, 235, colorRed);
            }
        }else {
            guiGraphics.drawCenteredString(mc.font, String.valueOf((int) muscle), 77, 235, colorWhite);
        }
        guiGraphics.pose().popPose();

        guiGraphics.fill(65,158, (int) (66+(41*(skin/100))),160, 0xFFFFFFFF);
        guiGraphics.fill(65,166, (int) (66+(41*(muscle/100))),168, 0xFFFFFFFF);

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
