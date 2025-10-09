package net.adinvas.prototype_pain.client.moodles;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class InfectionMoodle extends AbstractMoodleVisual{
    @Override
    MoodleStatus calculateStatus(Player player) {
        Optional<Double> infection = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getMaxInfection);

        if (infection.orElse(0d)>80){
            return MoodleStatus.CRITICAL;
        }else if (infection.orElse(0d)>60){
            return MoodleStatus.HEAVY;
        }else if (infection.orElse(0d)>40){
            return MoodleStatus.NORMAL;
        }else if (infection.orElse(0d)>25){
            return MoodleStatus.LIGHT;
        }else {
            return MoodleStatus.NONE;
        }
    }

    @Override
    public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks, int x, int y) {
        ResourceLocation tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/infection_moodle.png");
        ms.blit(tex, x, y, 0, 0, 16, 16, 16, 16);
        return tex;
    }
}
