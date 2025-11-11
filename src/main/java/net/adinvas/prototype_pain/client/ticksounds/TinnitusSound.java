package net.adinvas.prototype_pain.client.ticksounds;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

import java.util.Random;

public class TinnitusSound extends AbstractTickableSoundInstance {
    public TinnitusSound(SoundEvent p_235076_) {
        super(p_235076_, SoundSource.PLAYERS, RandomSource.create());
        this.looping= true;
        this.delay = 0;               // no delay between loops
        this.volume = 0.1f;
        this.pitch = 1.0f;
        this.x = (float) Minecraft.getInstance().player.getX();
        this.y = (float) Minecraft.getInstance().player.getY();
        this.z = (float) Minecraft.getInstance().player.getZ();
    }

    @Override
    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        this.x = (float) mc.player.getX();
        this.y = (float) mc.player.getY();
        this.z = (float) mc.player.getZ();

        this.volume = Mth.lerp(0.01f, this.volume,0);
        if (this.volume <= 0.01f) {
            this.stop();
        }
    }

    public void update(Player player){
        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            float painscale = h.getFlashHearingLoss();
            if (painscale>0){
                this.volume = 1;
            }
        });
    }
}
