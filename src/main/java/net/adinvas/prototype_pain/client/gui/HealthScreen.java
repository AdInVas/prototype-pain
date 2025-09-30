package net.adinvas.prototype_pain.client.gui;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.client.ticksounds.HeartBeatSound;
import net.adinvas.prototype_pain.item.INarcoticUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.network.GuiSyncTogglePacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.adinvas.prototype_pain.network.UseMedItemPacket;
import net.adinvas.prototype_pain.network.UseNarcoticItemPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class HealthScreen extends Screen {
    private LimbWidget L_Hand;
    private LimbWidget R_Hand;
    private LimbWidget L_Arm;
    private LimbWidget R_Arm;
    private LimbWidget L_Foot;
    private LimbWidget R_Foot;
    private LimbWidget L_Leg;
    private LimbWidget R_Leg;
    private LimbWidget Chest;
    private LimbWidget Head;
    private ItemWidget RightItem;
    private ItemWidget LeftItem;
    private HealthInfoBoxWidget healthbox;
    private NarcoticWidget narcoticWidget;

    private Player target;

    private List<CustomButton> buttonList = new ArrayList<>();
    private int listStartX = 5;
    private int listStartY = height/4 * 3;

    private LimbWidget lastClicked;
    private LimbWidget lastHovered;

    private boolean lastHandOffHand= false;

    private HeartBeatSound heartBeatSound;


    public HealthScreen(UUID target) {
        super(Component.empty());
        this.target = Minecraft.getInstance().player.level().getPlayerByUUID(target);

    }



    @Override
    protected void init() {
        super.init();
        int start_x = (this.width/2)+50;
        int start_y = (this.height / 4)-25;
        listStartX = 1;
        listStartY = 196+2;
        Head = new LimbWidget(start_x,start_y,32,32,Component.empty(),Limb.HEAD);
        Chest = new LimbWidget(start_x,start_y+32,32,64,Component.empty(),Limb.CHEST);
        L_Arm = new LimbWidget(start_x+32,start_y+32,48,16,Component.empty(),Limb.LEFT_ARM);
        R_Arm = new LimbWidget(start_x-48,start_y+32,48,16,Component.empty(),Limb.RIGHT_ARM);
        L_Hand = new LimbWidget(start_x+32+48,start_y+32,16,16,Component.empty(),Limb.LEFT_HAND);
        R_Hand = new LimbWidget(start_x-48-16,start_y+32,16,16,Component.empty(),Limb.RIGHT_HAND);
        L_Leg = new LimbWidget(start_x+16,start_y+32+64,16,48,Component.empty(),Limb.LEFT_LEG);
        R_Leg = new LimbWidget(start_x,start_y+32+64,16,48,Component.empty(),Limb.RIGHT_LEG);
        L_Foot = new LimbWidget(start_x+16,start_y+32+64+48,16,16,Component.empty(),Limb.LEFT_FOOT);
        R_Foot = new LimbWidget(start_x,start_y+32+64+48,16,16,Component.empty(),Limb.RIGHT_FOOT);
        healthbox = new HealthInfoBoxWidget(0,0,128,196,Component.empty());

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = player.getItemInHand(hand);

                // Which arm is this hand using? (LEFT or RIGHT)
                HumanoidArm arm = Limb.getFromHand(hand, player);

                if (arm == HumanoidArm.RIGHT) {
                    // Draw right-hand item on the right side of HUD
                    RightItem = new ItemWidget(
                            start_x - 48 - 16 - 8,
                            start_y + 8,
                            stack
                    );
                } else {
                    // Draw left-hand item on the left side of HUD
                    LeftItem = new ItemWidget(
                            start_x + 32 + 48 + 8,
                            start_y + 8,
                            stack
                    );
                }
            }
        }
        narcoticWidget = new NarcoticWidget(this.width/2-64,this.height/2-32,128,64,Component.empty());
        addRenderableWidget(Head);
        addRenderableWidget(Chest);
        addRenderableWidget(L_Arm);
        addRenderableWidget(L_Leg);
        addRenderableWidget(L_Foot);
        addRenderableWidget(L_Hand);
        addRenderableWidget(R_Arm);
        addRenderableWidget(R_Foot);
        addRenderableWidget(R_Hand);
        addRenderableWidget(R_Leg);
        addRenderableWidget(LeftItem);
        addRenderableWidget(RightItem);
        addRenderableWidget(healthbox);
        addRenderableWidget(narcoticWidget);
        Head.populate_sprites();
        Chest.populate_sprites();
        L_Arm.populate_sprites();
        L_Leg.populate_sprites();
        L_Foot.populate_sprites();
        L_Hand.populate_sprites();
        R_Arm.populate_sprites();
        R_Foot.populate_sprites();
        R_Hand.populate_sprites();
        R_Leg.populate_sprites();
        updateScreen();
        ModNetwork.CHANNEL.sendToServer(new GuiSyncTogglePacket(true,target.getUUID()));
        lastHovered = Head;
        healthbox.setName(Component.literal(target.getScoreboardName()));
        if (player!=null){
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                heartBeatSound = new HeartBeatSound(player,h.getBPM());
            });
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        R_Arm.renderSprites(pGuiGraphics);
        Chest.renderSprites(pGuiGraphics);
        Head.renderSprites(pGuiGraphics);
        L_Arm.renderSprites(pGuiGraphics);
        R_Hand.renderSprites(pGuiGraphics);
        L_Hand.renderSprites(pGuiGraphics);
        R_Leg.renderSprites(pGuiGraphics);
        L_Leg.renderSprites(pGuiGraphics);
        R_Foot.renderSprites(pGuiGraphics);
        L_Foot.renderSprites(pGuiGraphics);
        LimbWidget h = getHoveringWidget(pMouseX,pMouseY);
        if (h!=null){
            lastHovered = h;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (heartBeatSound != null && Minecraft.getInstance().player != null) {
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                float bpm = h.getBPM();
                heartBeatSound.setBPM(bpm);
            });
            heartBeatSound.tick();
        }
        narcoticWidget.tick();
        if (target == null || !target.isAlive()) {
            onClose(); // target gone
            return;
        }

        Player viewer = Minecraft.getInstance().player;
        double distSq = viewer.distanceToSqr(target);
        double maxDist = 3.0D; // example, 8 blocks

        if (distSq > maxDist * maxDist) {
            onClose(); // too far -> close GUI
            return;
        }
        updateScreen();
        UpdateButtons(lastClicked);
        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(health->{
            healthbox.setSkin(health.getLimbSkinHealth(lastHovered.getLimb()));
            healthbox.setMuscle(health.getLimbMuscleHealth(lastHovered.getLimb()));
            healthbox.setLimbname(lastHovered.getLimb());
            healthbox.setPain2(health.getLimbPain(lastHovered.getLimb()));
            healthbox.setBleed2(health.getLimbBleedRate(lastHovered.getLimb()));
            healthbox.setPain((float)health.getTotalPain());
            healthbox.setContiousness(health.getContiousness());
            healthbox.setBlood(health.getBloodVolume());
            healthbox.setBleed(health.getCombinedBleed());
            healthbox.setInfection(health.getLimbInfection(lastHovered.getLimb()));
            healthbox.setOpiates(health.getOpioids());
            healthbox.setOxygen(health.getOxygen());
            healthbox.setDislocated((health.getLimbDislocated(lastHovered.getLimb())/health.getMAX_FRACT_DISL_TIME_T())*100);
            healthbox.setFracture((health.getLimbFracture(lastHovered.getLimb())/health.getMAX_FRACT_DISL_TIME_T())*100);
        });
        if (narcoticWidget.getReleased()>1){
            ItemStack itemstack = narcoticWidget.getRememberItemstack();
            float currentdamage = (itemstack.getMaxDamage()-itemstack.getDamageValue())/100f;
            float nextdamage = narcoticWidget.getAmountleft();
            float amountUsed = (currentdamage-nextdamage);
            ModNetwork.CHANNEL.sendToServer(new UseNarcoticItemPacket(itemstack,amountUsed,target.getUUID(),lastHandOffHand));
            narcoticWidget.setNull();
        }
        Minecraft.getInstance().player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            if (h.getContiousness()<=4)
                Minecraft.getInstance().setScreen(null);
        });
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void updateScreen(){
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = player.getItemInHand(hand);

                HumanoidArm arm = Limb.getFromHand(hand, player);

                if (arm == HumanoidArm.RIGHT) {
                    RightItem.setStack(stack);
                } else {
                    LeftItem.setStack(stack);
                }
            }
        }
        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(health->{
            for (Limb limb : Limb.values()) {
                LimbWidget widget = switch (limb) {
                    case HEAD      -> Head;
                    case CHEST     -> Chest;
                    case LEFT_ARM  -> L_Arm;
                    case RIGHT_ARM -> R_Arm;
                    case LEFT_HAND -> L_Hand;
                    case RIGHT_HAND-> R_Hand;
                    case LEFT_LEG  -> L_Leg;
                    case RIGHT_LEG -> R_Leg;
                    case LEFT_FOOT -> L_Foot;
                    case RIGHT_FOOT-> R_Foot;
                };

                // collect values once
                float bleed = health.getLimbBleedRate(limb);
                boolean isBleeding = bleed > 0&& !health.getTourniquet(limb)&& !health.isOppositeToChestUnderTourniquet(limb);
                float pain = health.getLimbPain(limb);
                float skin = health.getLimbSkinHealth(limb);
                float muscle = health.getLimbMuscleHealth(limb);

                boolean infection = health.getLimbInfection(limb) > 25;
                boolean dislocated = health.isLimbDislocated(limb)>0;
                boolean splint = health.hasLimbSplint(limb);
                boolean shrapnel = health.hasLimbShrapnell(limb);
                boolean fractured = health.getLimbFracture(limb) > 0;
                boolean desinfection = health.getLimbDesinfected(limb) > 0;
                boolean tourniquet = health.getTourniquet(limb);

                // ---- apply to the widget ----
                widget.setShake(pain / 100f);
                widget.setBorder_red(1 - (skin / 100f));
                widget.setBase_red(1 - (muscle / 100f));

                if (isBleeding) {
                    float scale = Math.max(0.9f, (bleed / health.getMAX_BLEED_RATE()) * 2.5f);
                    widget.setScaleOf(StatusSprites.BLEED, scale);
                }
                widget.setSubSpriteVisible(StatusSprites.BLEED, isBleeding);
                widget.setSubSpriteVisible(StatusSprites.DISINFECTION, desinfection);
                widget.setSubSpriteVisible(StatusSprites.FRACTURE, fractured);
                widget.setSubSpriteVisible(StatusSprites.INFECTION, infection);
                widget.setSubSpriteVisible(StatusSprites.SHRAPNEL, shrapnel);
                widget.setSubSpriteVisible(StatusSprites.SPLINT, splint);
                widget.setSubSpriteVisible(StatusSprites.DISLOCATION, dislocated);
                widget.setSubSpriteVisible(StatusSprites.TOURNIQUET,tourniquet);
            }
        });
    }

    @Override
    public void onClose() {
        super.onClose();
        if (heartBeatSound != null) {
            PrototypePain.LOGGER.info("stop sound");
            heartBeatSound = null;
        }
        ModNetwork.CHANNEL.sendToServer(new GuiSyncTogglePacket(false,target.getUUID()));
    }

    @Override
    public void renderBackground(GuiGraphics gui) {
        super.renderBackground(gui);
        gui.fill(0,0,this.width,this.height,0x000000FF);
        
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (RightItem.isDragging()){
            narcoticWidget.setNull();
            LimbWidget widget = getHoveringWidget(pMouseX,pMouseY);
            if (widget!=null) {
                Limb limb = widget.getLimb();
                ItemStack itemstack = getItemstackForHand(HumanoidArm.RIGHT, minecraft.player);
                if (itemstack.getItem() instanceof INarcoticUsable){
                    float damage = ((100-itemstack.getDamageValue())/100f);
                    narcoticWidget.setDisplay(damage,itemstack);
                    lastHandOffHand = false;
                }else {
                    ModNetwork.CHANNEL.sendToServer(new UseMedItemPacket(itemstack, limb, target.getUUID(), false));
                }
            }
        }else if (LeftItem.isDragging()){
            narcoticWidget.setNull();
            LimbWidget widget = getHoveringWidget(pMouseX,pMouseY);
            if (widget!=null) {
                Limb limb = widget.getLimb();
                ItemStack itemstack = getItemstackForHand(HumanoidArm.LEFT, minecraft.player);
                if (itemstack.getItem() instanceof INarcoticUsable){
                    float damage = ((itemstack.getMaxDamage()-itemstack.getDamageValue())/100f);
                    narcoticWidget.setDisplay(damage,itemstack);
                    lastHandOffHand = true;
                }else {
                    ModNetwork.CHANNEL.sendToServer(new UseMedItemPacket(itemstack, limb, target.getUUID(), true));
                }
            }
        }
        RightItem.onRelease(pMouseX,pMouseY);
        LeftItem.onRelease(pMouseX,pMouseY);
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private ItemStack getItemstackForHand(HumanoidArm arm, Player player) {
        HumanoidArm mainArm = player.getMainArm();

        // If we're asking for the player's dominant arm → MAIN_HAND
        if (arm == mainArm) {
            return player.getItemInHand(InteractionHand.MAIN_HAND);
        } else {
            // Otherwise it's the opposite → OFF_HAND
            return player.getItemInHand(InteractionHand.OFF_HAND);
        }
    }

    private LimbWidget getHoveringWidget(double pMouseX,double pMouseY){
        for (GuiEventListener child : this.children()) {
            if (child instanceof LimbWidget limbwidget) {
                if (limbwidget.isMouseOver(pMouseX,pMouseY)) return limbwidget;
            }
        }
        return null;
    }

    private CustomButton getHoveringWidgetCusomButton(double pMouseX,double pMouseY){
        for (GuiEventListener child : this.children()) {
            if (child instanceof CustomButton limbwidget) {
                if (limbwidget.isMouseOver(pMouseX,pMouseY)) return limbwidget;
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        LimbWidget widget = getHoveringWidget(pMouseX,pMouseY);
        if (widget!=null) {
            UpdateButtons(widget);
        }

        CustomButton button = getHoveringWidgetCusomButton(pMouseX,pMouseY);
        if (button!=null){
            UpdateButtons(lastClicked);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public void UpdateButtons(LimbWidget widget){
        List<StatusSprites> statusList = new ArrayList<>();
        if (widget!=null) {
            for (CustomButton button : buttonList){
                removeWidget(button);
            }
            buttonList= new ArrayList<>();
            if (widget.isSpritePresent(StatusSprites.SHRAPNEL)) statusList.add(StatusSprites.SHRAPNEL);
            if (widget.isSpritePresent(StatusSprites.DISLOCATION)) statusList.add(StatusSprites.DISLOCATION);
            if (widget.isSpritePresent(StatusSprites.TOURNIQUET)) statusList.add(StatusSprites.TOURNIQUET);
            if (widget.isSpritePresent(StatusSprites.SPLINT)) statusList.add(StatusSprites.SPLINT);
            int i = 0;
            for (StatusSprites sprite:statusList){
                buttonList.add(new CustomButton(listStartX,listStartY+(16*i),sprite,widget.getLimb(),target));
                addRenderableWidget(buttonList.get(i));
                i++;
            }
            lastClicked = widget;
        }
    }
}
