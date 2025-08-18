package net.adinvas.prototype_pain.client;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.network.MedicalAction;
import net.adinvas.prototype_pain.network.MedicalActionPacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class CustomButton extends AbstractWidget {
    private StatusSprites status;
    private Limb limb;
    private Player target;
    private final ResourceLocation tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/button.png");
    public CustomButton(int pX, int pY, StatusSprites status, Limb limb, Player target) {
        super(pX, pY, 128, 16, status.getTextComponents());
        this.status = status;
        this.limb = limb;
        this.target = target;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.blit(tex,getX(),getY(),0,0,128,16,128,16);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font,getMessage(),getX()+width/2,getY()+height/2 -2,0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        MedicalAction action = switch (status){
            case TOURNIQUET -> MedicalAction.REMOVE_TOURNIQUET;
            case DISLOCATION -> MedicalAction.FIX_DISLOCATION;
            case SHRAPNEL -> MedicalAction.TRY_SHRAPNEL;
            case SPLINT -> MedicalAction.REMOVE_SPLINT;
            default -> null;
        };
        if (action!=null){
            ModNetwork.CHANNEL.sendToServer(new MedicalActionPacket(limb,target.getUUID(),action));
        }
        super.onClick(pMouseX, pMouseY);
    }

    public StatusSprites getStatus() {
        return status;
    }
}
