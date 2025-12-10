package net.adinvas.prototype_pain.client.gui;

import net.adinvas.prototype_pain.item.multi_tank.MultiTankFluidItem;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;

import net.adinvas.prototype_pain.network.FluidTransferPacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class FluidExchangeScreen extends Screen {
    private final Screen parent;
    private final ItemStack toItem;
    private final ItemStack fromItem;
    private final int helperSlot;
    private float sliderValue = 1f; // 50%
    private float sourceAmount;
    private float targetCapacity;
    public float MaxSlider;
    private Button button;

    public FluidExchangeScreen(Screen parent, ItemStack toItem, ItemStack fromItem, int helperSlot) {
        super(Component.literal("Fluid Exchange"));
        this.parent = parent;
        this.toItem = toItem;
        this.fromItem = fromItem;
        this.helperSlot = helperSlot;
    }

    @Override
    protected void init() {
        button = Button.builder(Component.literal("Transfer"), b -> confirmTransfer()).bounds(width/2-40,height/2+30,80,20).build();
        addRenderableWidget(button);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderBackground(pGuiGraphics);
        pGuiGraphics.drawCenteredString(font, "Fluid Transfer", width/2, height/2 - 60, 0xFFFFFF);

        int barWidth = 120;
        int barX = width/2 - barWidth/2;
        int barY = height/2;

        if (fromItem.getItem() instanceof MultiTankFluidItem medicalFluidContainer){
            sourceAmount = MultiTankHelper.getFilledTotal(fromItem);
        }
        if (toItem.getItem() instanceof MultiTankFluidItem medicalFluidContainer){
            float cap = MultiTankHelper.getCapacity(toItem);
            float filled = MultiTankHelper.getFilledTotal(toItem);
            targetCapacity = cap-filled;
        }

        int color = 0xFFFFFFFF;
        MaxSlider = Math.min(sourceAmount,targetCapacity);

        // draw slider bar
        pGuiGraphics.fill(barX, barY, barX + barWidth, barY + 8, 0xFF444444);
        pGuiGraphics.fill(barX, barY, barX + (int)(barWidth * sliderValue), barY + 8,(255<<24)|color);

        pGuiGraphics.drawCenteredString(font, (int)(sliderValue * MaxSlider) + "ml", width/2, barY + 12, 0xAAAAAA);

    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        int barWidth = 120;
        int barX = width/2 - barWidth/2;
        int barY = height/2;
        if (pMouseY >= barY && pMouseY <= barY + 8 && pMouseX >= barX && pMouseX <= barX + barWidth) {
            sliderValue = (float)((pMouseX - barX) / barWidth);
            sliderValue = Math.max(0f, Math.min(1f, sliderValue));
            return true;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics) {
        super.renderBackground(pGuiGraphics);
    }

    public void confirmTransfer(){
        float amount = sliderValue * MaxSlider;
        ModNetwork.CHANNEL.sendToServer(new FluidTransferPacket(fromItem, toItem,helperSlot, amount));
        onClose();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
