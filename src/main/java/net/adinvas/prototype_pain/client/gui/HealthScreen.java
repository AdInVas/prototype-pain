package net.adinvas.prototype_pain.client.gui;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.client.moodles.AbstractMoodleVisual;
import net.adinvas.prototype_pain.client.moodles.MoodleController;
import net.adinvas.prototype_pain.client.ticksounds.HeartBeatSound;
import net.adinvas.prototype_pain.item.IBag;
import net.adinvas.prototype_pain.item.IMedicalMinigameUsable;

import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.network.*;
import net.adinvas.prototype_pain.tags.ModItemTags;
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
    private List<ItemWidget> RightItemsubWidgets = new ArrayList<>();
    private ItemWidget LeftItem;
    private List<ItemWidget> LeftItemsubWidgets = new ArrayList<>();
    private HealthInfoBoxWidget healthbox;
    private CPRButton cprButton;
    private Player target;

    private CustomButton actionButton;
    private int listStartX = 5;
    private int listStartY = height/4 * 3;

    private LimbWidget lastClicked;
    private LimbWidget lastHovered;

    private HeartBeatSound heartBeatSound;

    public boolean BGmode = false;


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
        cprButton = new CPRButton(this.width-36,this.height-36,this,target);

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
        this.actionButton = new CustomButton(0,0,null,target);

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
        addRenderableWidget(cprButton);
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
        if (target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->h.getContiousness()<10).orElse(false)){
            cprButton.visible= true;
        }else{
            cprButton.visible = false;
        }

        ModNetwork.CHANNEL.sendToServer(new GuiSyncTogglePacket(true,target.getUUID()));
        lastHovered = Head;
        healthbox.setName(Component.literal(target.getScoreboardName()));
        if (player!=null){
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                heartBeatSound = new HeartBeatSound(player,h.getBPM());
            });
        }
        if (RightItem.getStack().getItem() instanceof IBag iBag) {
            List<ItemStack> itemStacks = iBag.getItems(RightItem.getStack());
            RightItemsubWidgets.clear();

            int slotWidth = 16;
            int rows = 2;
            int total = itemStacks.size();
            int columns = (int) Math.ceil(total / (double) rows);

            // Centered above the main slot
            int centerX = start_x - 48 - 16 - 8;
            int centerY = start_y + 8;

            int totalWidth = (columns - 1) * slotWidth;
            int startX = centerX - totalWidth / 2;
            int startY = centerY - (rows * slotWidth) - 4; // small vertical gap

            for (int i = 0; i < total; i++) {
                int col = i % columns;  // horizontal index
                int row = i / columns;  // vertical index (0 = top, 1 = bottom)

                int x = startX + col * slotWidth;
                int y = startY + row * slotWidth;

                ItemWidget widget = new ItemWidget(x, y, itemStacks.get(i));
                RightItemsubWidgets.add(widget);
                addRenderableWidget(widget);
            }
        }
        if (LeftItem.getStack().getItem() instanceof IBag iBag){
            List<ItemStack> itemStacks = iBag.getItems(LeftItem.getStack());
            LeftItemsubWidgets.clear();

            int slotWidth = 16;
            int rows = 2;
            int total = itemStacks.size();
            int columns = (int) Math.ceil(total / (double) rows);

            // Centered above the main slot
            int centerX = start_x + 32 + 48 + 8;// +9 to roughly center by half slot
            int centerY = start_y + 8;

            int totalWidth = (columns - 1) * slotWidth;
            int startX = centerX - totalWidth / 2;
            int startY = centerY - (slotWidth * rows) - 4; // small vertical gap (4px)

            for (int i = 0; i < total; i++) {
                int col = i % columns;  // horizontal index
                int row = i / columns;  // vertical index (0 = top, 1 = bottom)

                int x = startX + col * slotWidth;
                int y = startY + row * slotWidth;

                ItemWidget widget = new ItemWidget(x, y, itemStacks.get(i));
                LeftItemsubWidgets.add(widget);
                addRenderableWidget(widget);
            }
            // Starting position: centered horizontally above the main slot
            // The main slot is at (start_x - 48 - 16 - 8, start_y + 8)

            // Total width of all columns

        }
        updateScreen();
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
        healthbox.setBGMode(BGmode);
        LeftItem.setBGMode(BGmode);
        RightItem.setBGMode(BGmode);
        for (ItemWidget itemWidget :LeftItemsubWidgets){
            itemWidget.setBGMode(BGmode);
        }
        for (ItemWidget itemWidget :RightItemsubWidgets){
            itemWidget.setBGMode(BGmode);
        }

        // render moodles for this target (ignoring hotbar constraints!)
        if (target != null) {
            List<AbstractMoodleVisual> visible = MoodleController.getVisibleMoodles(target);

            int startX = this.width / 2 - ((visible.size() * 20) / 2)+68; // center moodles
            int y = this.height - 40; // fixed height above bottom

            int x = startX;
            AbstractMoodleVisual hovered = null;

            for (AbstractMoodleVisual moodle : visible) {
                moodle.render(target, pGuiGraphics, pPartialTick, x, y);

                if (moodle.isMouseOver(pMouseX, pMouseY, x, y)) {
                    hovered = moodle;
                }

                x += 20;
            }

            if (hovered != null) {
                pGuiGraphics.renderTooltip(minecraft.font,hovered.getTooltip(minecraft.player), Optional.empty(),pMouseX,pMouseY);
            }
        }
        LimbWidget h = getHoveringWidget(pMouseX,pMouseY);

        if (h!=null){
            if (!BGmode)
                lastHovered = h;
        }
        actionButton.renderWidget(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
    }

    @Override
    public void tick() {
        super.tick();
        actionButton.tick();
        if (target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->h.getContiousness()<10).orElse(false)&&(target!=Minecraft.getInstance().player)){
            cprButton.visible= true;
        }else{
            cprButton.visible = false;
        }
        if (heartBeatSound != null && Minecraft.getInstance().player != null) {
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                float bpm = h.getBPM();
                heartBeatSound.setBPM(bpm);
            });
            heartBeatSound.tick();
        }
        if (target == null || !target.isAlive()) {
            onClose(); // target gone
            return;
        }

        Player viewer = Minecraft.getInstance().player;
        double distSq = viewer.distanceToSqr(target);
        double maxDist = 3.0D; // example, 8 blocks

        if (distSq > maxDist * maxDist) {
            Minecraft.getInstance().screen.onClose();
            return;
        }
        updateScreen();
        UpdateSubStacks();

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
            healthbox.setOpiates(health.getNetOpiodids());
            healthbox.setOxygen(health.getOxygen());
            healthbox.setDislocated(health.getLimbDislocated(lastHovered.getLimb()));
            healthbox.setFracture(health.getLimbFracture(lastHovered.getLimb()));
            healthbox.setBrain(health.getBrainHealth());
            healthbox.setTemp(health.getTemperature());
            healthbox.setImmunity(health.getImmunity());
            healthbox.setSickness(health.getSickness());
            healthbox.setThirst(health.getThirst());
        });
        if (!BGmode) {
            Minecraft.getInstance().player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                if (h.getContiousness() <= 4)
                    onClose();
            });
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void UpdateSubStacks(){
        if (RightItem.getStack().getItem() instanceof IBag iBag){
            List<ItemStack> itemStacks = iBag.getItems(RightItem.getStack());
            int total = Math.min(itemStacks.size(), RightItemsubWidgets.size());
            for (int i = 0; i < total; i++) {
                RightItemsubWidgets.get(i).setStack(itemStacks.get(i));
            }
        }
        if (LeftItem.getStack().getItem() instanceof IBag iBag){
            List<ItemStack> itemStacks = iBag.getItems(LeftItem.getStack());
            int total = Math.min(itemStacks.size(), LeftItemsubWidgets.size());
            for (int i = 0; i < total; i++) {
                LeftItemsubWidgets.get(i).setStack(itemStacks.get(i));
            }
        }
        if (BGmode){
            LeftItem.visible=false;
            RightItem.visible=false;
            for (ItemWidget widget :LeftItemsubWidgets){
                widget.visible = false;
            }
            for (ItemWidget widget :RightItemsubWidgets){
                widget.visible = false;
            }
        }else{
            LeftItem.visible=true;
            RightItem.visible=true;
            for (ItemWidget widget :LeftItemsubWidgets){
                widget.visible = true;
            }
            for (ItemWidget widget :RightItemsubWidgets){
                widget.visible = true;
            }
        }
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
                if(health.isAmputated(limb)){
                    widget.visible=false;
                    widget.setAmputated(true);
                    continue;
                }
                widget.setAmputated(false);
                if (limb==Limb.HEAD){
                    widget.setLeftEyeGone(health.isLeftEyeBlind());
                    widget.setMouthGone(health.isMouthRemoved());
                    widget.setRightEyeGone(health.isRightEyeBlind());
                }
                // collect values once
                float bleed = health.getLimbBleedRate(limb);
                boolean isBleeding = bleed > 0&& !health.getTourniquet(limb)&& !health.isOppositeToChestUnderTourniquet(limb);
                float pain = health.getLimbPain(limb);
                float skin = health.getLimbSkinHealth(limb);
                float muscle = health.getLimbMuscleHealth(limb);

                boolean infection = health.getLimbInfection(limb) > 25;
                boolean dislocated = health.isLimbDislocated(limb)>0;
                boolean splint = health.hasLimbSplint(limb);
                boolean shrapnel = health.hasLimbShrapnell(limb)>0;
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
            LimbWidget widget = getHoveringWidget(pMouseX,pMouseY);
                if (widget != null&&!widget.isAmputated()) {
                    Limb limb = widget.getLimb();
                    ItemStack itemstack = getItemstackForHand(HumanoidArm.RIGHT, minecraft.player);
                    if (itemstack.getItem() instanceof IMedicalMinigameUsable helper) {
                        helper.openMinigameScreen(target, itemstack, limb, getHand(HumanoidArm.RIGHT, minecraft.player));
                    }
                    if (itemstack.is(ModItemTags.CAUTERIZE)){
                        ModNetwork.CHANNEL.sendToServer(new CauterizeActionPacket(limb,target.getUUID()));
                    }
                    ModNetwork.CHANNEL.sendToServer(new UseMedItemPacket(itemstack, limb, target.getUUID(), getHand(HumanoidArm.RIGHT, minecraft.player) == InteractionHand.OFF_HAND));
                }
        }else if (LeftItem.isDragging()){
                LimbWidget widget = getHoveringWidget(pMouseX, pMouseY);
                if (widget != null&&!widget.isAmputated()) {
                    Limb limb = widget.getLimb();
                    ItemStack itemstack = getItemstackForHand(HumanoidArm.LEFT, minecraft.player);
                    if (itemstack.getItem() instanceof IMedicalMinigameUsable helper) {
                        helper.openMinigameScreen(target, itemstack, limb, getHand(HumanoidArm.LEFT, minecraft.player));
                    }
                    if (itemstack.is(ModItemTags.CAUTERIZE)){
                        ModNetwork.CHANNEL.sendToServer(new CauterizeActionPacket(limb,target.getUUID()));
                    }
                    ModNetwork.CHANNEL.sendToServer(new UseMedItemPacket(itemstack, limb, target.getUUID(), getHand(HumanoidArm.LEFT, minecraft.player) == InteractionHand.OFF_HAND));
                }
        }
        for (int i=0;i<RightItemsubWidgets.size();i++){
            if (!RightItemsubWidgets.get(i).isDragging())continue;
            LimbWidget widget = getHoveringWidget(pMouseX,pMouseY);
                if (widget != null&&!widget.isAmputated()) {
                    Limb limb = widget.getLimb();
                    ItemStack itemstack = RightItemsubWidgets.get(i).getStack();
                    ItemStack bagstack = getItemstackForHand(HumanoidArm.RIGHT, minecraft.player);
                    if (itemstack.getItem() instanceof IMedicalMinigameUsable helper) {
                        helper.openMinigameBagScreen(target, itemstack, bagstack, i, limb, getHand(HumanoidArm.RIGHT, minecraft.player));
                    }
                /*
                if (itemstack.getItem() instanceof SyringeItem){
                    Minecraft.getInstance().setScreen(new InjectMingameScreen(this,target,itemstack,bagstack,i,limb,getHand(HumanoidArm.RIGHT, minecraft.player)));
                }

                 */
                    ModNetwork.CHANNEL.sendToServer(new UseBagMedItemPacket(itemstack, bagstack, limb, target.getUUID(), getHand(HumanoidArm.RIGHT, minecraft.player) == InteractionHand.OFF_HAND, i));
                }
        }
        for (int i=0;i<LeftItemsubWidgets.size();i++){
            if (!LeftItemsubWidgets.get(i).isDragging())continue;
            LimbWidget widget = getHoveringWidget(pMouseX,pMouseY);
                if (widget != null&&!widget.isAmputated()) {
                    Limb limb = widget.getLimb();
                    ItemStack itemstack = LeftItemsubWidgets.get(i).getStack();
                    ItemStack bagstack = getItemstackForHand(HumanoidArm.LEFT, minecraft.player);
                    if (itemstack.getItem() instanceof IMedicalMinigameUsable helper) {
                        helper.openMinigameBagScreen(target, itemstack, bagstack, i, limb, getHand(HumanoidArm.LEFT, minecraft.player));
                    }
                /*
                if (itemstack.getItem() instanceof SyringeItem){
                    Minecraft.getInstance().setScreen(new InjectMingameScreen(this,target,itemstack,bagstack,i,limb,getHand(HumanoidArm.LEFT, minecraft.player)));
                }

                 */
                    ModNetwork.CHANNEL.sendToServer(new UseBagMedItemPacket(itemstack, bagstack, limb, target.getUUID(), getHand(HumanoidArm.LEFT, minecraft.player) == InteractionHand.OFF_HAND, i));
                }
        }
        RightItem.onRelease(pMouseX,pMouseY);
        LeftItem.onRelease(pMouseX,pMouseY);
        for (ItemWidget itemWidget : RightItemsubWidgets){
            itemWidget.onRelease(pMouseX,pMouseY);
        }
        for (ItemWidget itemWidget : LeftItemsubWidgets){
            itemWidget.onRelease(pMouseX,pMouseY);
        }
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

    private InteractionHand getHand(HumanoidArm arm, Player player) {
        HumanoidArm mainArm = player.getMainArm();

        // If we're asking for the player's dominant arm → MAIN_HAND
        if (arm == mainArm) {
            return InteractionHand.MAIN_HAND;
        } else {
            // Otherwise it's the opposite → OFF_HAND
            return InteractionHand.OFF_HAND;
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

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        LimbWidget widget = getHoveringWidget(pMouseX,pMouseY);
        actionButton.onClick(pMouseX,pMouseY);
        actionButton.setClickDelay(10);
        if (widget==null){
            actionButton.setAction(null);
            actionButton.setLimb(null);
        }
            if (widget != null) {
                if (!widget.isAmputated()) {
                    UpdateButtons(widget);
                }
            }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public void UpdateButtons(LimbWidget widget){
        if (widget!=null) {


            Limb limb = widget.getLimb();
            if (limb!=null){
                int offset = switch (widget.getLimb()){
                    case HEAD,CHEST -> 16;
                    case LEFT_LEG,RIGHT_LEG,RIGHT_FOOT,LEFT_FOOT,RIGHT_HAND,LEFT_HAND -> 8;
                    case RIGHT_ARM,LEFT_ARM -> 24;
                    default -> 0;
                };
                actionButton.setX(widget.getX()-32+offset);
                actionButton.setY(widget.getY()-30);
                actionButton.setLimb(limb);
            }


          if (widget.isSpritePresent(StatusSprites.SPLINT))actionButton.setAction(MedicalAction.REMOVE_SPLINT);
          else if (widget.isSpritePresent(StatusSprites.TOURNIQUET))actionButton.setAction(MedicalAction.REMOVE_TOURNIQUET);
          else if (widget.isSpritePresent(StatusSprites.SHRAPNEL))actionButton.setAction(MedicalAction.TRY_SHRAPNEL);
          else if (widget.isSpritePresent(StatusSprites.DISLOCATION))actionButton.setAction(MedicalAction.FIX_DISLOCATION);
          else actionButton.setAction(null);
          lastClicked = widget;
        }
    }

}
