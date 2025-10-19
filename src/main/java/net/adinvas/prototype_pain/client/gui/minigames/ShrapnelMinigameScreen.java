package net.adinvas.prototype_pain.client.gui.minigames;

import com.mojang.blaze3d.systems.RenderSystem;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.client.gui.HealthScreen;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.adinvas.prototype_pain.network.AdjustShrapnelPacket;
import net.adinvas.prototype_pain.network.ExchangeItemInHandPacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.adinvas.prototype_pain.network.SyringeFailPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShrapnelMinigameScreen extends Screen {
    private final Screen parent;
    private final Player target;
    private final Limb limb;
    private double lastpMouseX=this.width/2,lastpMouseY=this.height/2;
    private final boolean ignorevel;

    private List<ShrapnelObject> shrapnelObjects = new ArrayList<>();
    private List<Integer> xlists = new ArrayList<>();

    private HandObject handObject;

    public ShrapnelMinigameScreen(Screen parent, Player target, Limb limb, boolean ignorevel) {
        super(Component.literal("ShrapnelMinigame"));
        this.parent = parent;
        this.target = target;
        this.limb = limb;
        this.ignorevel = ignorevel;
    }

    @Override
    protected void init() {
        super.init();
        handObject = new HandObject(ignorevel?HandObject.SpriteType.TWEEZERS:HandObject.SpriteType.NORMAL,this.width/2,this.height/2,this.width,this.height/3*2);
        if (parent instanceof HealthScreen hp){
            hp.BGmode = true;
        }
        int ShrapnelAmount = target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->h.hasLimbShrapnell(limb)).orElse(0);
        int x = this.width/2-16;
        shrapnelObjects.clear();
        xlists.clear();
        for (int i=0;i<ShrapnelAmount;i++){
            int passX = x;
            if (i%2==0){
                passX-= 48*i;
            }else {
                passX += 48*i;
            }
            xlists.add(passX);
            shrapnelObjects.add(
                    new ShrapnelObject(passX, (int) (height/6+145+((Math.random()*2-0.5f)*5)),this.height / 6,this.height / 6+150,target,limb)
            );
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        parent.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.fill(0,0,width,height,0x88000000);
        pGuiGraphics.fill(0,height/6+158,width,height/6+162,0xFFFFFFFF);

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

        for (Integer i:xlists){
            pGuiGraphics.blit(new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/blood_decal.png"), i,height/6+157,0,0,32,5,32,5);
        }

        // Enable scissor
        RenderSystem.enableScissor(scissorX, scissorY, scissorW, scissorH);

        for (ShrapnelObject shrapnelObject:shrapnelObjects){
            shrapnelObject.render(pGuiGraphics);
        }

        RenderSystem.disableScissor();

        pGuiGraphics.drawCenteredString(mc.font,Component.translatable("prototype_pain.gui.shrapnel_instruction"),this.width/2,10,0xFFFFFF);
        pGuiGraphics.drawCenteredString(mc.font,Component.translatable("prototype_pain.gui.minigame_exit"),this.width/2,clipY+30,0xFFFFFF);

        handObject.render(pGuiGraphics,pPartialTick);
    }



    @Override
    public void tick() {
        parent.tick();
        handObject.update(lastpMouseX,lastpMouseY);
        float yVel = (float) Math.abs(handObject.getVy());
        for (ShrapnelObject shrapnelObject:shrapnelObjects){
            shrapnelObject.mouseDragged(handObject.x,handObject.y,0);
            shrapnelObject.update(yVel,ignorevel);
        }

        Player player = Minecraft.getInstance().player;
        if (player!=null){
            Optional<Float> cons=  player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getContiousness);
            Optional<Double> pain = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getTotalPain);
            float consscale = (cons.orElse(100f)/100)*0.15f;
            float painscale = (float) (pain.orElse(0d)/100)*0.55f;
            handObject.setShakeScale(painscale);
            handObject.setStiffness(consscale);
        }
        Minecraft.getInstance().player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            if (h.getContiousness()<=4)
                onClose();
        });
        super.tick();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (ShrapnelObject shrapnelObject:shrapnelObjects){
            shrapnelObject.mouseClicked(handObject.x,handObject.y,0);
        }
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
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        for (ShrapnelObject shrapnelObject:shrapnelObjects){
            shrapnelObject.setDragging(false);
        }
        handObject.mouseReleased();
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (parent instanceof HealthScreen hp){
            hp.BGmode = false;
        }
        int shrapnellLeft = (int) shrapnelObjects.stream().filter(ShrapnelObject::isSticked).count();
        ModNetwork.CHANNEL.sendToServer(new AdjustShrapnelPacket(target.getUUID(),limb,shrapnellLeft));
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
