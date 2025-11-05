package net.adinvas.prototype_pain.client.gui;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.client.gui.minigames.CPRMinigameScreen;
import net.adinvas.prototype_pain.client.gui.minigames.DislocationMinigameScreen;
import net.adinvas.prototype_pain.client.gui.minigames.ShrapnelMinigameScreen;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.network.MedicalAction;
import net.adinvas.prototype_pain.network.MedicalActionPacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class CPRButton extends AbstractWidget {
    private final Screen parent;
    private Player target;
    private final ResourceLocation tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/cpr_button.png");
    public CPRButton(int pX, int pY, Screen parent, Player target) {
        super(pX, pY, 32, 32, Component.empty());
        this.parent = parent;
        this.target = target;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.blit(tex,getX(),getY(),0,0,32,32,32,32);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        if (!visible)return;
        Minecraft.getInstance().setScreen(new CPRMinigameScreen(parent,target));
    }

}
