package net.adinvas.prototype_pain.client.moodles;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class OpiateMoodle extends AbstractMoodleVisual{
    @Override
    MoodleStatus calculateStatus(Player player) {
        Optional<Float> opioids = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getOpioids);
        if (opioids.orElse(0f)>80){
            return MoodleStatus.CRITICAL;
        } else if (opioids.orElse(0f)>50) {
            return MoodleStatus.HEAVY;
        }
        else if (opioids.orElse(0f)>20) {
            return MoodleStatus.NORMAL;
        }
        else if (opioids.orElse(0f)>5) {
            return MoodleStatus.LIGHT;
        }else {
            return MoodleStatus.NONE;
        }
    }

    @Override
    public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks, int x, int y) {
        ResourceLocation tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/opiate_moodle.png");
        ms.blit(tex, x, y, 0, 0, 16, 16, 16, 16);
        return tex;
    }
}
