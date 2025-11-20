package net.adinvas.prototype_pain.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.world.phys.Vec2;

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
    private float sickness;
    private float thirst = -20;

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

    public void setThirst(float thirst) {
        this.thirst = thirst;
    }

    public void setSickness(float sickness) {
        this.sickness = sickness;
    }


    private final ResourceLocation main_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/health_screen/info_box.png");
    private final ResourceLocation cons_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/health_screen/conc_sprites.png");
    private final ResourceLocation opioid_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/health_screen/opioids.png");
    private final Style textStyle = Style.EMPTY.withFont(new ResourceLocation(PrototypePain.MOD_ID, "health_screen"));
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

    float uiScale = 0.66f;
    float textScale = 0.75f;

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        int colorWhite = 0x48E38C;
        int colorRed = 0xFF0000;
        if (BGMode){
            colorWhite = 0x226A41;
            colorRed = 0x770000;
        }
        PoseStack pose = guiGraphics.pose();

        float time = (Minecraft.getInstance().level.getGameTime() + v) / 20f; // seconds
        boolean blink = (int)(time * 6) % 2 == 0; // toggle every 0.5s


        pose.pushPose();
        pose.scale(uiScale,uiScale,1);
        guiGraphics.blit(main_tex,getX(),getY(),0,0,196,392,196,392);

        int CV = (int) (16*(5-Math.ceil((contiousness/20))));
        guiGraphics.blit(cons_tex,getX()+9,getY()+57,0,CV,16,16,16,80);

        if (opiates>0){
            guiGraphics.blit(opioid_tex,getX()+100,getY()+167,0,0,5,33,5,33);
            float opatescale = Mth.clamp(opiates/100,0,1);
            if (opiates>100){
                if (blink)
                    guiGraphics.fill(getX()+100+2,getY()+167+13,getX()+100+4, (int) (getY()+167+13+(17*opatescale)),0xFF48E38C);
            }else{
                guiGraphics.fill(getX()+100+2,getY()+167+13,getX()+100+3, (int) (getY()+167+13+(17*opatescale)),0xFF48E38C);
            }
        }
        float brainscale = Mth.clamp(brain/100,0,1);
        guiGraphics.fill(getX()+32,getY()+39, (int) (getX()+33+(91*brainscale)), getY()+42,0xFF48E38C);
        float tempscale = Mth.clamp(1-(temp-28)/14,0,1);
        guiGraphics.fill(getX()+18, (int) (getY()+148+(28*tempscale)), getX()+19, getY()+176,0xFF48E38C);

        float skinscale = Mth.clamp(skin/100,0,1);
        guiGraphics.fill(getX()+65,getY()+311,(int)(getX()+65+(61*skinscale)),getY()+313,0xFF48E38C);

        float musclescale = Mth.clamp(muscle/100,0,1);
        guiGraphics.fill(getX()+65,getY()+328,(int)(getX()+65+(61*musclescale)),getY()+330,0xFF48E38C);

        float thisrstscale = Mth.clamp(1- thirst /100,0,1);
        float overtirstscale = Mth.clamp((thirst -100)/100,0,1);
        guiGraphics.fill(getX()+120,(int)(getY()+147+(62*thisrstscale)),getX()+122,getY()+207,0xFF48E38C);
        guiGraphics.fill(getX()+120,getY()+147,getX()+122,(int)(getY()+147+(62*overtirstscale)),0xFFfc9c35);

        pose.popPose();
        pose.pushPose();
        pose.scale(textScale,textScale,1);

        Component text = Component.literal(Math.floor(blood*100)/100+"l").setStyle(textStyle);
        Vec2 pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,35,98);
        if (blood<3){
            if (blink){
                guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
            }
        }else {
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }


        text = Component.literal((int)oxygen+"%").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,22,127);
        if (oxygen<40){
            if (blink){
                guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
            }
        }else {
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }

        text = Component.literal((int)immunity+"%").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,87,127);
        if (immunity<50){
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
        }else {
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }

        text = Component.literal((int)brain+"").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,29,45);
        guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);

        text = Component.literal((int)contiousness+"% CONS").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,27,59);
        if (contiousness<30){
            if (blink){
                guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
            }
        }else {
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }

        text = Component.literal((int)pain+"% PAIN").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,27,78);
        if (pain>60){
            if (blink){
                guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
            }
        }else {
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }

        text = Component.literal(Math.floor(temp*10)/10+"C").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,27,155);
        if (temp>41.5||temp<29){
            if (blink){
                guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
            }
        }else {
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }

        text = Component.literal((int)sickness+"%").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,28,196);
        if (sickness>50){
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
        }else {
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }

        text = Component.literal(Math.floor(bleed*20*60*100)/100+"l/m").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,40,111);
        if (bleed>0.2/20/60){
            if (blink)
                guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
        }else {
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }

        text = limbname.copy().setStyle(textStyle).withStyle(ChatFormatting.BOLD);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,69,292);
        drawCenteredText(guiGraphics,Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);


        text = Component.literal("SKIN   -").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,10,308);
        guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);


        text = Component.literal("MUSCLE -").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,10,325);
        guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);


        text = Component.literal(name.getString().toUpperCase()).setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,69,13);

        drawCenteredText(guiGraphics,Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);


        text = Component.literal((int)muscle+"").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,111,318);
        if (muscle<20){
            if (blink)
                guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
        }else {
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }

        text = Component.literal((int)skin+"").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,64,314);
        if (skin<20){
                guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
        }else {
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }

        text = Component.literal("FRACT").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,10,340);
        guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);

        text = Component.literal("DISL").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,10,350);
        guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);

        if (dislocated>0){
            text = Component.literal((int)dislocated+"%").setStyle(textStyle);
            pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,50,350);
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
        }else{
            text = Component.literal("----").setStyle(textStyle);
            pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,50,350);
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }

        if (fracture>0){
            text = Component.literal((int)fracture+"%").setStyle(textStyle);
            pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,50,340);
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
        }else{
            text = Component.literal("----").setStyle(textStyle);
            pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,50,340);
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }

        if (infection>25){
            text = Component.literal((int)infection+"").setStyle(textStyle);
            pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,80,340);
            if (infection>80){
                if (blink)
                    guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
            }else{
                guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
            }
        }else{
            text = Component.literal("---").setStyle(textStyle);
            pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,80,340);
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }

        text = Component.literal((int)pain2+"").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,80,356);
        if (pain2>60){
            if (blink)
                guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
        }else{
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }

        if (bleed2>0) {
            text = Component.literal(Math.floor(bleed2 * 20 * 60*100)/100 + "l/m").setStyle(textStyle);
            pos = scaleCoordinates(textScale, textScale, uiScale, uiScale, 70, 372);
            if (bleed2 * 20 * 60 > 0.2) {
                if (blink)
                    guiGraphics.drawString(Minecraft.getInstance().font, text, (int) pos.x, (int) pos.y, colorRed, false);
            } else {
                guiGraphics.drawString(Minecraft.getInstance().font, text, (int) pos.x, (int) pos.y, colorWhite, false);
            }
        }

        text = Component.literal((int) thirst +"").setStyle(textStyle);
        pos = scaleCoordinates(textScale,textScale,uiScale,uiScale,97,155);
        if (thirst >120|| thirst <10){
            if (blink)
                guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorRed,false);
        }else{
            guiGraphics.drawString(Minecraft.getInstance().font,text,(int)pos.x,(int)pos.y,colorWhite,false);
        }


        pose.popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public static Vec2 scaleCoordinates(
            float fromScaleX, float fromScaleY,
            float toScaleX, float toScaleY,
            float x, float y) {

        float newX = x * (toScaleX / fromScaleX);
        float newY = y * (toScaleY / fromScaleY);
        return new Vec2(newX, newY);
    }

    public static void drawCenteredText(
            GuiGraphics gui,
            Font font,
            Component text,
            float x,
            float y,
            int color,
            boolean dropShadow
    ) {
        // Measure text width using the styled component
        int width = font.width(text);

        // Center position
        int drawX = (int)(x - width / 2f);
        int drawY = (int)y;

        gui.drawString(font, text, drawX, drawY, color, dropShadow);
    }
}
