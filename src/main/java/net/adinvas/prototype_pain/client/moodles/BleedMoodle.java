package net.adinvas.prototype_pain.client.moodles;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BleedMoodle extends AbstractMoodleVisual{

    @Override
    MoodleStatus calculateStatus(Player player) {
        AtomicReference<MoodleStatus> status = new AtomicReference<>(this.getMoodleStatus());
        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            float bleed = h.getCombinedBleed();
            if (bleed > 0.6f / 20 / 60) {
                status.set(MoodleStatus.CRITICAL);
            } else if (bleed > 0.3f / 20 / 60) {
                status.set(MoodleStatus.HEAVY);
            } else if (bleed > 0.15f / 20 / 60) {
                status.set(MoodleStatus.NORMAL);
            } else if (bleed > 0.05f / 20 / 60) {
                status.set(MoodleStatus.LIGHT);
            } else {
                status.set(MoodleStatus.NONE);
            }
        });
        if (status.get()!=null){
            return status.get();
        }
        return this.getMoodleStatus();

    }

    @Override
    public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks, int x, int y) {
        ResourceLocation tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/blood_moodle.png");
        ms.blit(tex, x, y, 0, 0, 16, 16, 16, 16);
        return tex;
    }

}
