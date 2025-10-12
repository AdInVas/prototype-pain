package net.adinvas.prototype_pain.client.gui.minigames;

import com.mojang.math.Axis;
import net.adinvas.prototype_pain.ModSounds;
import net.adinvas.prototype_pain.item.INbtDrivenDurability;
import net.adinvas.prototype_pain.item.bandages.PlasticDressingItem;
import net.adinvas.prototype_pain.item.bandages.SterilizedDressingItem;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.adinvas.prototype_pain.network.UseBandagePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BandageObject extends GrabObject{
    private final int centerX;
    private final int centerY;
    private final float radius=70;

    private float angle = 0;
    private float rotation = 0f;    // sprite roll
    private float scaleFactor = 1f; // shrink as it rolls
    private float progress = 0f;

    private ItemStack itemStack;

    private boolean EndCondition =false;

    private float durabilitySincePacket = 0;


    public boolean isEndCondition() {
        return EndCondition;
    }

    public BandageObject(int x, int y, int hitX, int hitY, int hitWidth, int hitHeight, ResourceLocation tex, int texWidth, int texHeight, float scale, int centerX, int centerY,ItemStack stack) {
        super(x, y, hitX, hitY, hitWidth, hitHeight, tex, texWidth, texHeight, scale);
        this.centerX = centerX;
        this.centerY = centerY;
        this.itemStack =stack;
        calculateScale(stack);
        float scaleoffsetX = (float) (Math.cos(angle)*(-texWidth/2f*(1-scaleFactor)));
        float scaleoffsetY = (float) (Math.sin(angle)*(-texHeight/2f*(1-scaleFactor)));

        this.x = (int) (centerX +scaleoffsetX+ Math.cos(angle) * radius - texWidth * scale / 2f);
        this.y = (int) (centerY +scaleoffsetY+ Math.sin(angle) * radius - texHeight * scale / 2f);
    }


    public ItemStack getItemStack() {
        return itemStack;
    }

    private int tickCounter = 0;
    public void update(ItemStack stack, Player target, Limb limb){
        calculateScale(stack);
        if (stack.getItem() instanceof INbtDrivenDurability nbt){
            if (nbt.getNbtDurability(stack)<=0){
                itemStack = ItemStack.EMPTY;
                ModNetwork.CHANNEL.sendToServer(new UseBandagePacket(stack,target.getUUID(),durabilitySincePacket,limb));
                durabilitySincePacket = 0;
                EndCondition = true;
            }
        }
        if (tickCounter++>5&&!EndCondition){
            tickCounter=0;
            ModNetwork.CHANNEL.sendToServer(new UseBandagePacket(stack,target.getUUID(),durabilitySincePacket,limb));
            durabilitySincePacket = 0;
        }
    }

    public void calculateScale(ItemStack stack){
        if (stack.getItem() instanceof INbtDrivenDurability nbtDrivenDurability){
            scaleFactor = 1.0f - 0.7f * (1-nbtDrivenDurability.getNbtDurabilityRatio(stack));
        }
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button) {
        if (dragging && button == 0) {
            // Compute mouse angle relative to circle center
            double dx = mouseX - centerX;
            double dy = mouseY - centerY;
            float newAngle = (float) Math.atan2(dy, dx);

            // Compute angular difference
            float diff = newAngle - angle;


            // Normalize to -π..π range
            while (diff < -Math.PI) diff += (float) (2 * Math.PI);
            while (diff > Math.PI) diff -= (float) (2 * Math.PI);

            // Allow only clockwise motion (negative diff = CCW)
            if (diff > 0) {
                float durabilityneg = (float) ((Math.toDegrees(diff)/360)*10);
                if (itemStack.getItem() instanceof  INbtDrivenDurability nbtDrivenDurability){
                    nbtDrivenDurability.setNbtDurability(itemStack,
                            nbtDrivenDurability.getNbtDurability(itemStack)-durabilityneg);
                    durabilitySincePacket+=durabilityneg;
                }
                angle += diff;
                rotation += diff * 6f; // spin effect multiplier
                progress += Math.abs(diff);
            }

            // Wrap around full circle
            if (angle > Math.PI * 2){
                Minecraft.getInstance().player.playSound(ModSounds.BANDAGE_USE.get());
                angle -= Math.PI * 2;
            }

            // Update position along circle
            float scaleoffsetX = (float) (Math.cos(angle)*(-texWidth/2f*(1-scaleFactor)));
            float scaleoffsetY = (float) (Math.sin(angle)*(-texHeight/2f*(1-scaleFactor)));

            this.x = (int) (centerX +scaleoffsetX+ Math.cos(angle) * radius - texWidth * scale / 2f);
            this.y = (int) (centerY +scaleoffsetY+ Math.sin(angle) * radius - texHeight * scale / 2f);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics) {
        var pose = guiGraphics.pose();
        pose.pushPose();

        // Move to center of sprite for rotation
        pose.translate(x + texWidth * scale / 2f, y + texHeight * scale / 2f, 0);
        pose.mulPose(Axis.ZP.rotation(rotation/2));
        pose.scale(scale * scaleFactor, scale * scaleFactor, 1f);
        pose.translate(-texWidth / 2f, -texHeight / 2f, 0);
        if (itemStack.getItem() instanceof PlasticDressingItem) {
            guiGraphics.setColor(0.5f,0.5f,1f,1);
        }else if (itemStack.getItem() instanceof SterilizedDressingItem) {
            guiGraphics.setColor(0.6f,0.6f,0.6f,1);
        }
        guiGraphics.blit(tex, 0, 0, 0, 0, texWidth, texHeight, texWidth, texHeight);
        guiGraphics.setColor(1f,1f,1f,1);
        pose.popPose();

    }
}
