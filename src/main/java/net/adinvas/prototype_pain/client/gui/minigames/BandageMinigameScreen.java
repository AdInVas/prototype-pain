package net.adinvas.prototype_pain.client.gui.minigames;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.client.gui.HealthScreen;
import net.adinvas.prototype_pain.client.gui.StatusSprites;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.adinvas.prototype_pain.network.ExchangeItemInHandPacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class BandageMinigameScreen extends Screen {
    private final Screen parent;
    private final Player target;
    private final ItemStack bandageStack;
    private final Limb limb;
    private final InteractionHand hand;

    private BandageObject bandageObject;

    private HandObject handObject;

    private float bleedRate=0;
    private float maxBleed =0;
    public BandageMinigameScreen(Screen parent, Player target, ItemStack bandageStack, Limb limb, InteractionHand hand) {
        super(Component.literal("BandageMinigame"));
        this.parent = parent;
        this.target = target;
        this.bandageStack = bandageStack;
        this.limb = limb;
        this.hand = hand;
    }

    @Override
    protected void init() {
        super.init();
        handObject = new HandObject(HandObject.SpriteType.NORMAL,this.width/2,this.height/2,this.width,this.height/3*2);
        if (parent instanceof HealthScreen hp){
            hp.BGmode = true;
        }
        bandageObject =new BandageObject(
                0, 0,
                0, 0, 64, 64,
                new ResourceLocation(PrototypePain.MOD_ID, "textures/gui/bandage.png"),
                64, 64,
                1f,
                this.width/2,
                this.height/2,
                bandageStack
        );
        maxBleed = target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getMAX_BLEED_RATE).orElse(1f);
    }

    private double lastpMouseX=100,lastpMouseY=100;
    @Override
    public void tick() {
        Optional<Float> BD =target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->{
            return h.getLimbBleedRate(limb);
        });
        bleedRate = BD.orElse(0f);
        parent.tick();
        handObject.update(lastpMouseX,lastpMouseY);
        bandageObject.update(bandageStack,target,limb);
        bandageObject.mouseDragged(handObject.x,handObject.y,0);
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
        if (bandageObject.isEndCondition()){
            onClose();
        }
        super.tick();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        bandageObject.mouseClicked(handObject.x,handObject.y,pButton);
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
        bandageObject.setDragging(false);
        handObject.mouseReleased();
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }




    @Override
    public void onClose() {
        super.onClose();
        ModNetwork.CHANNEL.sendToServer(new ExchangeItemInHandPacket(bandageObject.getItemStack(),hand==InteractionHand.OFF_HAND));
        if (parent instanceof HealthScreen hp){
            hp.BGmode = false;
        }
        Minecraft.getInstance().setScreen(parent);
    }



    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        parent.render(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.fill(0,0,width,height,0x88000000);
        Minecraft mc = Minecraft.getInstance();
        guiGraphics.blit(new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/bandage_center.png"),this.width/2-40,this.height/2-40,0,0,80,80,80,80,80);


        guiGraphics.drawCenteredString(mc.font,Component.translatable("prototype_pain.gui.bandage_instruction1"),this.width/2,10,0xFFFFFF);
        guiGraphics.drawCenteredString(mc.font,Component.translatable("prototype_pain.gui.bandage_instruction2"),this.width/2,20,0xFFFFFF);
        guiGraphics.drawCenteredString(mc.font,Component.translatable("prototype_pain.gui.minigame_exit"),this.width/2,this.height / 6+190,0xFFFFFF);

        bandageObject.render(guiGraphics);
        if(bleedRate>0){
            float bleedscale = 0.5f+ 1.4f*(bleedRate/maxBleed);
            float sizePx = 20 * bleedscale;
            guiGraphics.blit(StatusSprites.BLEED.getResourceLocation(), (int) (width/2-sizePx/2), (int) (height/2-sizePx/2+10),0,0, (int) sizePx, (int) sizePx, (int) sizePx, (int) sizePx);
        }
        guiGraphics.renderItem(bandageStack,this.width/10-10,this.height/10+5);
        guiGraphics.drawString(mc.font,Component.empty().append(bandageStack.getHoverName()),this.width/10+16,this.height/10+5,0xFFFFFF);


        handObject.render(guiGraphics,partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
