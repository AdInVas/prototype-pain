package net.adinvas.prototype_pain.client.moodles;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.List;

public abstract class AbstractMoodleVisual implements Cloneable{
    private MoodleStatus moodleStatus;
    private MoodleStatus lastStatus;
    private int sinceChange = 0;

    private long lastJumpTime = -1;
    private final long animationDuration = 300; // ms
    private final float jumpHeight = -7;        // pixels up

    public void triggerJump() {
        lastJumpTime = System.currentTimeMillis();
    }

    public List<Component> getTooltip(Player player) {
        return Collections.emptyList();
    }

    public boolean isMouseOver(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + 16 &&
                mouseY >= y && mouseY < y + 16;
    }

    public void render(Player target,GuiGraphics ms, float partialTicks,int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level==null)return;
        double time = (mc.level.getGameTime() + partialTicks) / 20.0;

        setMoodleStatus(calculateStatus(target));

        if (lastStatus!=getMoodleStatus()){
            if (sinceChange++>5){
                sinceChange=0;
                lastStatus = getMoodleStatus();
                triggerJump();
            }
        }

        float animatedOffset = 0;
        if (lastJumpTime > 0) {
            long elapsed = System.currentTimeMillis() - lastJumpTime;
            if (elapsed < animationDuration) {
                float t = (float) elapsed / animationDuration;
                animatedOffset = jumpHeight * (1.0f - t * t); // easing
            } else {
                lastJumpTime = -1; // finished
            }
        }

        int finaly = y + (int) animatedOffset;


        if (moodleStatus==MoodleStatus.CRITICAL){
            int color = getCriticalColor();
            int endcolor = getCrilticalEndColor();
            float pulse = (float) Math.sin(time*Math.PI);

            ms.fillGradient(x+1, (int) (finaly-20+(10*pulse)),x+15, finaly+3,endcolor,color);
        }
        renderBackground(ms,partialTicks,x,finaly);
        renderIcon(ms,partialTicks,x,finaly);
    }

    public boolean shouldRender() {
        return moodleStatus!=MoodleStatus.NONE;
    }

    public int getCriticalColor(){
        return 0x66991d1d;
    }
    public int getCrilticalEndColor(){
        return 0x00991d1d;
    }

    public abstract MoodleStatus calculateStatus(Player player);

    public MoodleStatus getMoodleStatus() {
        return moodleStatus;
    }

    public void setMoodleStatus(MoodleStatus moodleStatus) {
        if (this.moodleStatus != moodleStatus&&moodleStatus!=null) {
            this.moodleStatus = moodleStatus;
        }
    }

    public void renderBackground(GuiGraphics guiGraphics,float partialTicks,int x,int y){
        guiGraphics.blit(moodleStatus.getTex(),x,y,0,0,16,16,16,16);
        guiGraphics.blit(getRingTex(),x,y,0,0,16,16,16,16);
    }

    private ResourceLocation getRingTex(){
        return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/moodle_ring.png");
    }

    abstract public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks,int x, int y);

    @Override
    public AbstractMoodleVisual clone() {
        try {
            return (AbstractMoodleVisual) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
