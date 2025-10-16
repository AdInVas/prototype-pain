package net.adinvas.prototype_pain.client.gui.minigames;

import com.mojang.math.Axis;
import net.adinvas.prototype_pain.ModSounds;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.client.ticksounds.SyringeTickSound;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.item.fluid_vials.SyringeItem;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.adinvas.prototype_pain.network.UseSyringePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SyringeObject extends GrabObject{
    private float fullness = 1f;
    private int color = 0xFF000000;

    private boolean sticked = false;
    private int stickX,stickY;
    private float angle;

    private final int minstickY;
    private final int maxYDrop = 50;

    private final float MaxDrainml = 200;
    private float speed =0;

    private final int originalHitX,originalHW;

    private boolean isSnapped = false;

    public SyringeObject(int x, int y, float scale, int minstickY) {
        super(x,y,4,21,24,79,
                new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/limbs/syringe.png"),32,160, scale);
        this.minstickY = minstickY;
        originalHitX = this.hitX;
        originalHW = this.hitWidth;
    }

    public void setColor(ItemStack stack) {
        if (stack.getItem() instanceof SyringeItem syringeItem){
           int newcol = Util.mixColors(syringeItem.getFuildAndRatio(stack));
           this.color = (200<<24)|newcol;
        }
    }

    public void setFullness(ItemStack stack) {
        if (stack.getItem() instanceof SyringeItem syringeItem){
            fullness = syringeItem.getFilledTotal(stack)/syringeItem.getCapacity(stack);
        }
    }

    public void drainStack(ItemStack stack,float ml,Player player,Limb limb){
        if (stack.getItem() instanceof SyringeItem syringeItem){
            Map<MedicalFluid,Float> fulidmap = syringeItem.drain(stack,ml);
            List<String> ids = new ArrayList<>();
            List<Float> amounts = new ArrayList<>();
            for (MedicalFluid fluid:fulidmap.keySet()){
                ids.add(fluid.getId());
                amounts.add(fulidmap.get(fluid));
            }

            float[] array = new float[amounts.size()];
            for (int i = 0; i < amounts.size(); i++) {
                array[i] = amounts.get(i);
            }
            ModNetwork.CHANNEL.sendToServer(new UseSyringePacket(ids.toArray(String[]::new),array,player.getUUID(),limb));
        }
    }

    private SyringeTickSound tickSound;

    public void update(ItemStack stack, Player target, Limb limb){
        setFullness(stack);
        if (sticked){
            drainStack(stack,speed*MaxDrainml/20,target,limb);
        }

        if (sticked&&fullness>=0.01){
            if (tickSound==null){
                tickSound = new SyringeTickSound(ModSounds.SYRINGE_LOOP.get());
                Minecraft.getInstance().getSoundManager().play(tickSound);
            }
            tickSound.setPitch(0.5f+speed*1.5f);
        }else {
            if (tickSound!=null){
                tickSound.setDone(true);
                if (tickSound.isStopped()) tickSound = null;
            }
        }


        float returnSpeed = 0.1f; // how fast it straightens (0.05 = slow, 0.2 = fast)

        // when dragging: only auto-correct if not actively twisting
        if (dragging) {
            // if near vertical, ease back
            if (Math.abs(angle) < Math.toRadians(45)) {
                angle += (0 - angle) * returnSpeed;
            }
        } else if (!sticked){
            // when released: always straighten
            angle += (0 - angle) * returnSpeed;
        }
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button) {
        if (dragging && button == 0) {
            // Normal drag position
            this.x = (int) mouseX - dragOffsetX;
            this.y = (int) mouseY - dragOffsetY;

            if (!sticked&&y>=minstickY){
                sticked=true;
                stickX = (int) (mouseX);
            }else if (y<minstickY){
                sticked=false;
            }
            if (sticked){
                stickY = (int) (mouseY+160);
                if (y>minstickY+maxYDrop){
                    y= minstickY+maxYDrop;
                }
                double dx = mouseX - stickX;
                double dy = mouseY - stickY;

                angle = (float) (Math.atan2(dy,dx)+Math.toRadians(90));
                speed = (float) (y - minstickY) /(minstickY+maxYDrop);

                if (Math.toDegrees(angle)>0){
                    hitX = (int) (originalHitX-Math.toDegrees(angle));
                    hitWidth = (int) (originalHW+Math.toDegrees(angle));
                }else {
                    hitWidth = (int) (originalHW-Math.toDegrees(angle));
                }

                if (Math.abs(Math.toDegrees(angle))>12){

                    isSnapped = true;
                }

            }else {
                hitWidth = originalHW;
                hitX = originalHitX;
            }

        }
    }

    public boolean isSnapped() {
        return isSnapped;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public void render(GuiGraphics guiGraphics) {
        var pose = guiGraphics.pose();
        pose.pushPose();

        // Translate to object position
        pose.translate(x, y, 0);
        pose.mulPose(Axis.ZP.rotation( (angle)));
        // Draw texture at 0,0 in translated coordinates
        guiGraphics.fill(6, (int) (26+(72*(1-fullness))),26,98,color);
        guiGraphics.blit(tex,
                0, 0, // top-left at translated origin
                0f, 0f,
                (int) (texWidth * scale), (int) (texHeight * scale),
                (int) (texWidth * scale), (int) (texHeight * scale));


        pose.popPose();
    }

    public void stop() {
        if (tickSound != null){
            Minecraft.getInstance().getSoundManager().stop(tickSound);
            tickSound.setDone(true);
            tickSound = null;
        }
    }
}
