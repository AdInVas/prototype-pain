package net.adinvas.prototype_pain.blocks.medical_mixer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.adinvas.prototype_pain.FluidTankRenderer;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MedicalMixerScreen extends AbstractContainerScreen<MedicalMixerMenu> {
    private static final ResourceLocation TEX =
            new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/medical_mixer_gui.png");

    private FluidTankRenderer[] renderers = new FluidTankRenderer[6];

    public MedicalMixerScreen(MedicalMixerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 215;
        this.imageHeight = 185;
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 19999;
        asignFluidRenderers();
    }

    private void asignFluidRenderers() {
        for (int i=0;i<renderers.length;i++){
            renderers[i] = new FluidTankRenderer(1000,true,16,38);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.setShaderTexture(0,TEX);
        int x = (width- imageWidth)/2;
        int y = (height-imageHeight)/2;

        guiGraphics.blit(TEX,x,y,0,0,imageWidth,imageHeight);

        renderProgressArrow(guiGraphics,x,y);
        renderers[0].render(guiGraphics.pose(),x+8,y+29,menu.blockEntity.getFluidInTank(0));
        renderers[1].render(guiGraphics.pose(),x+28,y+29,menu.blockEntity.getFluidInTank(1));
        renderers[2].render(guiGraphics.pose(),x+48,y+29,menu.blockEntity.getFluidInTank(2));
        renderers[3].render(guiGraphics.pose(),x+152,y+29,menu.blockEntity.getFluidInTank(3));
        renderers[4].render(guiGraphics.pose(),x+172,y+29,menu.blockEntity.getFluidInTank(4));
        renderers[5].render(guiGraphics.pose(),x+192,y+29,menu.blockEntity.getFluidInTank(5));
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        int x = (width- imageWidth)/2;
        int y = (height-imageHeight)/2;

        //renderFluidAreaTooltips(pGuiGraphics,pMouseX,pMouseY,x,y,renderers[0]);
    }

    private void renderFluidAreaTooltips(GuiGraphics guiGraphics, int pMouseX, int pMouseY,int x,int y,FluidTankRenderer renderer) {
        if (isMouseOver(pMouseX,pMouseY,x,y,55,15,renderer.getWidth(),renderer.getHeight())){
        }
    }
    private boolean isMouseOver(int pMouseX, int pMouseY,int x, int y, int offsetX, int offsetY,int sizeX, int sizeY){
        boolean mouseOver =
                (pMouseX>=x+offsetX&&pMouseX<=x+offsetX+sizeX)&&
                        (pMouseY>=y+offsetY&&pMouseY<=y+offsetY+sizeY);
        return mouseOver;
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()){
            guiGraphics.blit(TEX,x+66,y+26,0,186, (int) (menu.getScaledProgress()*84),44);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
