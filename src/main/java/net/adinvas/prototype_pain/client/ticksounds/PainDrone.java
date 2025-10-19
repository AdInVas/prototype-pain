package net.adinvas.prototype_pain.client.ticksounds;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public class PainDrone extends AbstractTickableSoundInstance {
    public PainDrone(SoundEvent p_235076_) {
        super(p_235076_, SoundSource.PLAYERS, Minecraft.getInstance().level.getRandom());
        this.looping= true;
        this.delay = 0;               // no delay between loops
        this.volume = 0.1f;
        this.pitch = 1.0f;
        this.x = (float) Minecraft.getInstance().player.getX();
        this.y = (float) Minecraft.getInstance().player.getY();
        this.z = (float) Minecraft.getInstance().player.getZ();
    }

    public float targetVol = 0;

    @Override
    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        this.x = (float) mc.player.getX();
        this.y = (float) mc.player.getY();
        this.z = (float) mc.player.getZ();

        if (this.targetVol==0){
            this.volume = Mth.lerp(0.05f, this.volume, this.targetVol);
        }else{
            this.volume = Mth.lerp(0.1f, this.volume, this.targetVol);
        }
        if (this.volume <= 0.01f && this.targetVol <= 0.01f) {
            this.stop();
        }
    }

    public void update(Player player){
        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            float painscale = (float) ((h.getTotalPain())/100f);
            painscale = Mth.clamp(painscale,0.6f,1.1f);
            if (h.getTotalPain() < 50) {
                this.targetVol = 0;
                return;
            }
            this.targetVol = painscale;
        });
    }
}
