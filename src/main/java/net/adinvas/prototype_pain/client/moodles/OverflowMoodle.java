package net.adinvas.prototype_pain.client.moodles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class OverflowMoodle extends AbstractMoodleVisual{
    public int leftover = 0;
    @Override
    public MoodleStatus calculateStatus(Player player) {
        return MoodleStatus.LIGHT;
    }

    public void setLeftover(int leftover) {
        this.leftover = leftover;
    }

    @Override
    public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        ms.pose().pushPose();
        ms.pose().scale(0.8f,0.8f,0.8f);
        ms.drawCenteredString(mc.font,"+"+leftover, (int) ((x+8)*1.25), (int) ((y+5)*1.25),0xFFFFFF);
        ms.pose().popPose();

        return null;
    }
}
