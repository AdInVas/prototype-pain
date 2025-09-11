package net.adinvas.prototype_pain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LootPlayerScreen extends AbstractContainerScreen<LootPlayerMenu> {
    public LootPlayerScreen(LootPlayerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 186;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        gui.blit(new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/playerloottex.png"),
                this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        Font font = Minecraft.getInstance().font;
        pGuiGraphics.drawString(font,Component.translatable("container.inventory"),6,94,0xFFFFFF);
        pGuiGraphics.drawString(font,this.title,6,6,0xFFFFFF);
    }
}
