package net.adinvas.prototype_pain.client;

import net.adinvas.prototype_pain.ModSounds;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.client.ticksounds.PainDrone;
import net.adinvas.prototype_pain.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoundMenager {
    public static PainDrone painDrone;

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

        if (painDrone != null) {
            painDrone.update(mc.player);
            if (painDrone.isStopped()) {
                painDrone = null;
            }
        }
    }
}
