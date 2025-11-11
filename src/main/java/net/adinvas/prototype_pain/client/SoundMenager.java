package net.adinvas.prototype_pain.client;

import net.adinvas.prototype_pain.ModSounds;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.client.ticksounds.PainDrone;
import net.adinvas.prototype_pain.client.ticksounds.TinnitusSound;
import net.adinvas.prototype_pain.config.ClientConfig;
import net.minecraft.client.Minecraft;

public class SoundMenager {
    public static PainDrone painDrone;
    public static TinnitusSound tinnitusSound;

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;
        if (!ClientConfig.EXPERIMENTAL_SOUNDS.get())return;

        boolean shouldPlay = mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA)
                .map(h -> h.getTotalPain() > 50)
                .orElse(false);

        if (shouldPlay && (painDrone == null || painDrone.isStopped())) {
            painDrone = new PainDrone(ModSounds.PAINDRONE.get());
            mc.getSoundManager().play(painDrone);
        }

        shouldPlay = mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA)
                .map(h -> h.getFlashHearingLoss() > 0)
                .orElse(false);
        if (shouldPlay && (tinnitusSound == null || tinnitusSound.isStopped())) {
            tinnitusSound = new TinnitusSound(ModSounds.RINGING.get());
            mc.getSoundManager().play(tinnitusSound);
        }

        if (painDrone != null) {
            painDrone.update(mc.player);
            if (painDrone.isStopped()) {
                painDrone = null;
            }
        }

        if (tinnitusSound !=null){
            tinnitusSound.update(mc.player);
            if (tinnitusSound.isStopped()){
                tinnitusSound = null;
            }
        }
    }
}
