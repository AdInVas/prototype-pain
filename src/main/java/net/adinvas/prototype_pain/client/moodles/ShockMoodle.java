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

public class ShockMoodle extends AbstractMoodleVisual{
    @Override
    public MoodleStatus calculateStatus(Player player) {
        float Shock = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getShock).orElse(0f);
        if (Shock>0.3){
            return MoodleStatus.HEAVY;
        }else {
            return MoodleStatus.NONE;
        }
    }

    @Override
    public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks, int x, int y) {
        ResourceLocation tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/shock.png");
        ms.blit(tex, x, y, 0, 0, 16, 16, 16, 16);
        return tex;
    }

    @Override
    public List<Component> getTooltip(Player player) {
        List<Component> componentList = new ArrayList<>();
        componentList.add(Component.translatable("prototype_pain.gui.moodle.shock.title3").withStyle(ChatFormatting.GOLD));
        componentList.add(Component.translatable("prototype_pain.gui.moodle.shock.description3").withStyle(ChatFormatting.GRAY));
        return componentList;
    }
}
