package net.adinvas.prototype_pain.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class LimbWidget extends AbstractWidget {
    private final Limb limb;
    private final ResourceLocation borderTxt;
    private final ResourceLocation baseTxt;
    Random random = new Random();
    private float shake =0;
    private float border_red =0;
    private float base_red =0;
    private boolean amputated;
    private int txt_height;
    private int txt_width;

    private boolean LeftEyeGone= false;
    private boolean RightEyeGone = false;
    private boolean MouthGone = false;

    public void setMouthGone(boolean mouthGone) {
        MouthGone = mouthGone;
    }

    public void setRightEyeGone(boolean rightEyeGone) {
        RightEyeGone = rightEyeGone;
    }

    public void setLeftEyeGone(boolean leftEyeGone) {
        LeftEyeGone = leftEyeGone;
    }

    public void setAmputated(boolean amputated) {
        this.amputated = amputated;
    }

    public boolean isAmputated() {
        return amputated;
    }

    private final Map<StatusSprites, SubSprite> subSprites = new EnumMap<>(StatusSprites.class);
    private boolean expanded = false;


    public LimbWidget(int x, int y, int witdh, int height, Component title, Limb limb) {
        super(x, y, witdh, height, title);
        this.limb = limb;
        switch (limb){
            case RIGHT_FOOT,LEFT_FOOT,RIGHT_HAND,LEFT_HAND ->{
                borderTxt = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/end_border.png");
                baseTxt = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/end_base.png");
                txt_height = 16;
                txt_width = 16;
            }
            case LEFT_ARM,RIGHT_ARM -> {
                borderTxt = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/limb_horizontal_border.png");
                baseTxt = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/limb_horizontal_base.png");
                txt_height = 16;
                txt_width = 48;
            }
            case LEFT_LEG,RIGHT_LEG -> {
                borderTxt = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/limb_vertical_border.png");
                baseTxt = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/limb_vertical_base.png");
                txt_height = 48;
                txt_width = 16;
            }
            case CHEST -> {
                borderTxt = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/body_border.png");
                baseTxt = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/body_base.png");
                txt_height = 64;
                txt_width = 32;
            }
            case HEAD -> {
                borderTxt = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/head_border.png");
                baseTxt = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/head_base.png");
                txt_height = 32;
                txt_width = 32;
            }
            default -> {
                borderTxt = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/border.png");
                baseTxt = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/base.png");
                txt_height = 64;
                txt_width = 64;
            }
        }
    }

    public Limb getLimb() {
        return limb;
    }

    public void populate_sprites(){
        for (StatusSprites spriteType : StatusSprites.values()) {
            subSprites.put(spriteType, new SubSprite(spriteType,getX(), getY()));
        }
    }

    public void setShake(float shake) {
        this.shake = shake;
    }

    public void setBase_red(float base_red) {
        this.base_red = base_red;
    }

    public void setBorder_red(float border_red) {
        this.border_red = border_red;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (amputated)return;
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bindForSetup(baseTxt);

        float shakex = (float) (getX()+((random.nextFloat() * 2 - 1) * 2));
        float shakey = (float) (getY()+((random.nextFloat() * 2 - 1) * 2));
        if (random.nextFloat()>Math.pow(shake,3)) shakex = getX();
        if (random.nextFloat()>Math.pow(shake,3)) shakey = getY();

        RenderSystem.setShaderColor(
                base_red,
                0,
                0,
                1.0f
        );
        guiGraphics.blit(baseTxt, (int) shakex, (int) shakey,0,0,this.width,this.height,this.txt_width,this.txt_height);


        mc.getTextureManager().bindForSetup(borderTxt);

        RenderSystem.setShaderColor(
                1.0f,
                1-border_red,
                1-border_red,
                1.0f
        );
        guiGraphics.blit(borderTxt, (int) shakex, (int) shakey,0,0,this.width,this.height,txt_width,txt_height);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        expanded = isHoveredOrFocused();
        updateSubSpritePositions();
        if (LeftEyeGone){
            guiGraphics.blit(new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/left_eye.png"), (int) shakex, (int) shakey,0,0,this.width,this.height,this.txt_width,this.txt_height);
        }
        if(RightEyeGone){
            guiGraphics.blit(new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/right_eye.png"), (int) shakex, (int) shakey,0,0,this.width,this.height,this.txt_width,this.txt_height);
        }
        if (MouthGone){
            guiGraphics.blit(new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/mouth.png"), (int) shakex, (int) shakey,0,0,this.width,this.height,this.txt_width,this.txt_height);
        }
    }

    public void renderSprites(GuiGraphics guiGraphics){
        for (SubSprite sprite : subSprites.values()) {
            sprite.render(guiGraphics);
        }
    }



    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }

    private void updateSubSpritePositions() {
        if (amputated)return;
        List<SubSprite> visibleSprites = subSprites.values().stream()
                .filter(SubSprite::isVisible)
                .toList();

        int spacing = 12;
        if (expanded) {
            int centerIndex = visibleSprites.size() / 2;
            for (int i = 0; i < visibleSprites.size(); i++) {
                SubSprite s = visibleSprites.get(i);
                int offset = (int) ((i - centerIndex) * spacing * s.getScale());

                s.setTargetPosition(getX() + offset +this.width/2-8, getY()-1+this.height/2-8);
            }
        } else {
            for (SubSprite s : visibleSprites) {
                s.setTargetPosition(getX()+this.width/2-8, getY()+this.height/2-8);
            }
        }

        // Update all visible sprites (handles bobbing and smooth movement)
        visibleSprites.forEach(SubSprite::update);
    }

    public void setScaleOf(StatusSprites type,float value){
        subSprites.get(type).setTargetScale(value);
    }

    public void setSubSpriteVisible(StatusSprites type, boolean visible) {
        SubSprite sprite = subSprites.get(type);
        if (sprite != null) {
            sprite.setVisible(visible);
        }
    }

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        super.onClick(pMouseX, pMouseY);
    }

    public boolean isSpritePresent(StatusSprites sprite){
        return subSprites.get(sprite).isVisible();
    }

}
