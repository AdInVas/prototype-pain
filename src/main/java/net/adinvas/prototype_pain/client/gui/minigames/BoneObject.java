package net.adinvas.prototype_pain.client.gui.minigames;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.network.DislocationTryPacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2d;
import org.joml.Vector2f;

public class BoneObject extends GrabObject{

    private float dislocationValue;
    private final Vector2d velocity = new Vector2d();
    private Vector2d correctPos;
    private Vector2d targetPos;
    private boolean canBeHit = true;
    private boolean EndCondition = false;

    private float fakeDislocation;

    final float HIT_THRESHOLD = 3.0f;       // min hand velocity to count as a hit
    final float HIT_FORCE = 16.0f;           // push force
    final float AIM_ASSIST = 0.5f;         // 0–1. higher = more help
    final float EASE_OUT = 0.3f;            // smoothing factor for bone animation
    final float SNAP_RADIUS = 5f;         // how close to snap to correct position
    final float MAX_OFFSET = 100f;
    private static final float FRICTION = 0.7f;

    public BoneObject(int x, int y, float dislocationValue) {
        super(x, y, 16, 16, 144, 48,
                new ResourceLocation(PrototypePain.MOD_ID, "textures/gui/limbs/bone.png"),
                160, 80, 1);
        this.dislocationValue = dislocationValue;

        // Compute offset first
        double baseX = x;
        double baseY = y;

        double angle = Math.random() * Math.PI * 2;
        double offset = (dislocationValue / 100.0) * MAX_OFFSET;
        double startX = baseX + Math.cos(angle) * offset;
        double startY = baseY + Math.sin(angle) * offset;

        this.correctPos = new Vector2d(baseX, baseY);  // the real target
        this.targetPos  = new Vector2d(startX, startY);
        this.x = startX;
        this.y = startY;
        this.fakeDislocation = dislocationValue;
    }

    public boolean isEndCondition() {
        return EndCondition;
    }

    public void onHit(Vector2d velocity, Player target, Limb limb){
        if (velocity.length() < HIT_THRESHOLD||!canBeHit)
            return;
        Minecraft.getInstance().player.playSound(SoundEvents.SHIELD_BLOCK,1,0.7f);

        canBeHit = false;
        if (targetPos.equals(correctPos))return;

        // Direction toward correct position
        Vector2d assistDir = new Vector2d(correctPos).sub(x, y).normalize();

        // Blend between hand direction and assist direction
        Vector2d hitDir = new Vector2d(velocity.mul(2)).normalize  ().lerp(assistDir, AIM_ASSIST).normalize();

        // Compute how far to move the bone (scaled by hit force)
        Vector2d hitOffset = new Vector2d(hitDir).mul(HIT_FORCE*(Math.random()*1+1));

        // Compute new target position (do not directly modify x/y)
        Vector2d newTarget = new Vector2d(x + hitOffset.x, y + hitOffset.y);

        targetPos.set(newTarget);

        // Clamp near correctPos if it’s close enough
        Vector2d toCorrect = new Vector2d(correctPos).sub(x,y);

        if (toCorrect.length() < SNAP_RADIUS * 2) {
            targetPos.set(correctPos);
            EndCondition = true;
        }
        float newval = (float) new Vector2d(targetPos).sub(correctPos).length();
        dislocationValue = EndCondition?0:newval;

        ModNetwork.CHANNEL.sendToServer(new DislocationTryPacket(target.getUUID(),limb,dislocationValue));
    }


    public float getFakeDislocation() {
        return fakeDislocation;
    }

    int tick = 0;
    public void update() {
        if (!canBeHit){
            if (tick++>30){
                canBeHit= true;
                tick =0;
            }
        }

        fakeDislocation = (float) new Vector2d(x,y).sub(correctPos).length();
        // Easing movement toward the target position
        double dx = targetPos.x - x;
        double dy = targetPos.y - y;

        // Apply a smooth approach
        x += dx * EASE_OUT;
        y += dy * EASE_OUT;

        // Apply friction to internal velocity (optional visual smoothing)
        velocity.mul(FRICTION);

        // Snap to correct position when close enough
        Vector2d toCorrect = new Vector2d(correctPos).sub(x, y);
    }



    public void applyImpulse(Vector2d vector2f){
        velocity.add(vector2f);
    }

    public void updateCorrectPos(float x, float y){
        this.correctPos = new Vector2d(x,y);
    }

    @Override
    public void render(GuiGraphics guiGraphics) {
        super.render(guiGraphics);
    }

}
