package net.adinvas.prototype_pain.client.gui;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.client.gui.minigames.DislocationMinigameScreen;
import net.adinvas.prototype_pain.client.gui.minigames.ShrapnelMinigameScreen;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.network.MedicalAction;
import net.adinvas.prototype_pain.network.MedicalActionPacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;

public class CustomButton{
    private MedicalAction action;
    private int pX,pY,height,width;
    private Limb limb;
    private Player target;
    private final ResourceLocation tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/health_screen/button_box.png");
    public CustomButton(int pX, int pY, Limb limb, Player target) {
        this.pX = pX;
        this.pY = pY;
        height = 24;
        width = 64;
        this.limb = limb;
        this.target = target;
    }

    public void setAction(MedicalAction action) {
        this.action = action;
    }

    public MedicalAction getAction() {
        return action;
    }

    public void setLimb(Limb limb) {
        this.limb = limb;
    }

    public Limb getLimb() {
        return limb;
    }

    public int getX() {
        return pX;
    }

    public int getY() {
        return pY;
    }

    public void setX(int pX) {
        this.pX = pX;
    }

    public void setY(int pY) {
        this.pY = pY;
    }

    private final Style textStyle = Style.EMPTY.withFont(new ResourceLocation(PrototypePain.MOD_ID, "health_screen"));

    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        if (action==null||limb==null)return;

        guiGraphics.blit(tex,pX,pY,0,0,64,64,64,64);
        Vec2 coord = HealthInfoBoxWidget.scaleCoordinates(0.75f,0.75f,1f,1f,pX+32,pY+2);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.75f,0.75f,1);
        String sentence = action.getTextComponents().getString().toUpperCase();
        String[] words = sentence.split(" ");
        int j =0;
        for (String word : words) {
            Component text = Component.literal(word).withStyle(textStyle);
            HealthInfoBoxWidget.drawCenteredText(guiGraphics,Minecraft.getInstance().font,text,coord.x,coord.y+(6*j),0xFFFFFF,false);
            j++;
        }
        guiGraphics.pose().popPose();
        //guiGraphics.fill(pX,pY,pX+width,pY+height,0x77FF0000);
    }

    private int clickDelay=0;
    public void tick(){
        if (clickDelay>0)clickDelay--;
    }

    public void setClickDelay(int clickDelay) {
        this.clickDelay = clickDelay;
    }


    public void onClick(double pMouseX, double pMouseY) {
        if (!(pMouseX>=pX&&pMouseX<=pX+width&&pMouseY>=pY&&pMouseY<=pY+height))return;

        if (clickDelay>0)return;
        if (action==MedicalAction.FIX_DISLOCATION){
            Minecraft.getInstance().setScreen(new DislocationMinigameScreen(Minecraft.getInstance().screen,target,limb));
            action= null;
            limb = null;
            return;
        }
        if (action==MedicalAction.TRY_SHRAPNEL){
            Minecraft.getInstance().setScreen(new ShrapnelMinigameScreen(Minecraft.getInstance().screen,target,limb,false));
            action= null;
            limb = null;
            return;
        }
        if (action!=null&&limb!=null){
            ModNetwork.CHANNEL.sendToServer(new MedicalActionPacket(limb,target.getUUID(),action));
        }
    }

}
