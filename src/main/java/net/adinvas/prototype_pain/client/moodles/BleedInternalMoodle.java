package net.adinvas.prototype_pain.client.moodles;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BleedInternalMoodle extends AbstractMoodleVisual{
    @Override
    public MoodleStatus calculateStatus(Player player) {
        Optional<Float> internal = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getInternalBleeding);
        float bleed = internal.orElse(0f);
        if (bleed>0.3/20/60){
            return MoodleStatus.CRITICAL;
        } else if (bleed>0.03/20/60) {
            return MoodleStatus.HEAVY;
        }else {
            return MoodleStatus.NONE;
        }
    }

    @Override
    public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks, int x, int y) {
        ResourceLocation tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/internalblood_moodle.png");
        ms.blit(tex, x, y, 0, 0, 16, 16, 16, 16);
        return tex;
    }

    @Override
    public List<Component> getTooltip(Player player) {
        List<Component> componentList = new ArrayList<>();
        componentList.add(Component.translatable("prototype_pain.gui.moodle.internal_bleeding.title3").withStyle(ChatFormatting.GOLD));
        componentList.add(Component.translatable("prototype_pain.gui.moodle.internal_bleeding.description3").withStyle(ChatFormatting.GRAY));
        return componentList;
    }
}
