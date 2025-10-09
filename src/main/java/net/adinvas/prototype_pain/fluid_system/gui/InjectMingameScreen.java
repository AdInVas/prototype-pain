package net.adinvas.prototype_pain.fluid_system.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.client.gui.HealthScreen;
import net.adinvas.prototype_pain.fluid_system.items.SyringeItem;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.adinvas.prototype_pain.network.ExchangeItemInHandPacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.adinvas.prototype_pain.network.SyringeFailPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class InjectMingameScreen extends Screen {
    private final Screen parent;
    private final Player target;
    private final ItemStack syringeStack;
    private final Limb limb;
    private double lastpMouseX=this.width/2,lastpMouseY=this.height/2;

    private SyringeObject syringeObject;
    private final InteractionHand hand;

    private HandObject handObject;
    public InjectMingameScreen(Screen parent, Player target, ItemStack syringeStack, Limb limb, InteractionHand hand) {
        super(Component.literal("Inject screen"));
        this.parent = parent;
        this.target = target;
        this.syringeStack = syringeStack;
        this.limb = limb;
        this.hand = hand;
    }

    @Override
    protected void init() {
        super.init();
        handObject = new HandObject(this.width/2,this.height/2,this.width,this.height/3*2);
        if (parent instanceof HealthScreen hp){
            hp.BGmode = true;
        }
        syringeObject = new SyringeObject(this.width/2,0,1f,this.height/6,this.height/6+50);
        syringeObject.setFullness(syringeStack);
        syringeObject.setColor(syringeStack);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        parent.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.fill(0,0,width,height,0x88000000);
        pGuiGraphics.fill(0,height/6+158,width,height/6+161,0xFFFFFFFF);

        Minecraft mc = Minecraft.getInstance();
        int screenHeight = mc.getWindow().getScreenHeight();
        int screenWidth = mc.getWindow().getScreenWidth();

        double guiScaleX = (double) screenWidth / (double) this.width;
        double guiScaleY = (double) screenHeight / (double) this.height;

        int clipY = this.height / 6+161;

        int scissorX = 0;
        int scissorY = (int) (screenHeight - (clipY * guiScaleY));
        int scissorW = (int) (this.width * guiScaleX);
        int scissorH = (int) ((clipY) * guiScaleY);

        // Enable scissor
        RenderSystem.enableScissor(scissorX, scissorY, scissorW, scissorH);
        syringeObject.render(pGuiGraphics);
        RenderSystem.disableScissor();

        pGuiGraphics.drawCenteredString(mc.font,Component.translatable("prototype_pain.gui.syringe_instruction"),this.width/2,10,0xFFFFFF);
        pGuiGraphics.drawCenteredString(mc.font,Component.translatable("prototype_pain.gui.minigame_exit"),this.width/2,clipY+30,0xFFFFFF);
        pGuiGraphics.renderItem(syringeStack,this.width/10-10,this.height/10+5);
        int full = 100;
        if (syringeStack.getItem() instanceof SyringeItem syringeItem){
            full = (int) (syringeItem.getFilledTotal(syringeStack));
        }
        pGuiGraphics.drawString(mc.font,Component.empty().append(syringeStack.getHoverName()).append(Component.literal("(")).append(Component.literal(full+"%").withStyle(Style.EMPTY.withColor(getRedToGreenColor(full/100f)))).append(")"),this.width/10+16,this.height/10+5,0xFFFFFF);

        handObject.render(pGuiGraphics,pPartialTick);
    }

    public static int getRedToGreenColor(float value) {
        // clamp between 0 and 1
        value = Math.max(0f, Math.min(1f, value));

        int red   = (int)((1 - value) * 255);
        int green = (int)(value * 255);
        int blue  = 0;
        int alpha = 255;

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }


    @Override
    public void tick() {
        parent.tick();
        handObject.update(lastpMouseX,lastpMouseY);
        syringeObject.mouseDragged(handObject.x,handObject.y,0);
        syringeObject.update(syringeStack,target,limb);
        if (syringeObject.isSnapped()){
            handleFail();
        }
        Player player = Minecraft.getInstance().player;
        if (player!=null){
            Optional<Float> cons=  player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getContiousness);
            Optional<Double> pain = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getTotalPain);
            float consscale = (cons.orElse(100f)/100)*0.15f;
            float painscale = (float) (pain.orElse(0d)/100);
            handObject.setShakeScale(painscale);
            handObject.setStiffness(consscale);
        }
        Minecraft.getInstance().player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            if (h.getContiousness()<=4)
                onClose();
        });
        super.tick();
    }

    public void handleFail(){
        Minecraft.getInstance().player.playSound(SoundEvents.GLASS_BREAK);
        ModNetwork.CHANNEL.sendToServer(new SyringeFailPacket(target.getUUID(),limb));
        onClose();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        syringeObject.mouseClicked(handObject.x,handObject.y,pButton);
        handObject.mouseClicked();
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }


    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        lastpMouseY = pMouseY;
        lastpMouseX = pMouseX;
        super.mouseMoved(pMouseX, pMouseY);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        syringeObject.setDragging(false);
        handObject.mouseReleased();
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public void onClose() {
        super.onClose();
        ModNetwork.CHANNEL.sendToServer(new ExchangeItemInHandPacket(syringeStack,hand==InteractionHand.OFF_HAND));
        syringeObject.stop();
        Minecraft.getInstance().getSoundManager().stop();
        if (parent instanceof HealthScreen hp){
            hp.BGmode = false;
        }
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


}
