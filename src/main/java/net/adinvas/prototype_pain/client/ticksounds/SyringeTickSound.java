package net.adinvas.prototype_pain.client.ticksounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class SyringeTickSound extends AbstractTickableSoundInstance {

    private boolean done = false;

    public SyringeTickSound(SoundEvent sound) {
        super(sound, SoundSource.PLAYERS, RandomSource.create());
        this.looping = true;          // <— keep looping
        this.delay = 0;               // no delay between loops
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.x = (float) Minecraft.getInstance().player.getX();
        this.y = (float) Minecraft.getInstance().player.getY();
        this.z = (float) Minecraft.getInstance().player.getZ();
    }

    @Override
    public void tick() {
        if (done) {
            stop();
            return;
        }
        this.x = (float) Minecraft.getInstance().player.getX();
        this.y = (float) Minecraft.getInstance().player.getY();
        this.z = (float) Minecraft.getInstance().player.getZ();
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setPitch(float v){
        this.pitch = v;
    }

}
