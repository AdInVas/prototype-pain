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

public class OxygenMoodle extends AbstractMoodleVisual{
    @Override
    public MoodleStatus calculateStatus(Player player) {
        Optional<Float> oxygen = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getOxygen);
        float ox = oxygen.orElse(100f);
        if (ox<5){
            return MoodleStatus.CRITICAL;
        } else if (ox<30) {
            return MoodleStatus.HEAVY;
        } else if (ox<60) {
            return MoodleStatus.NORMAL;
        } else if (ox<90) {
            return MoodleStatus.LIGHT;
        }else {
            return MoodleStatus.NONE;
        }
    }

    @Override
    public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks, int x, int y) {
        ResourceLocation tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/oxygen_moodle.png");
        if (this.getMoodleStatus()==MoodleStatus.CRITICAL){
            tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/heartstop_moodle.png");
        }
        ms.blit(tex, x, y, 0, 0, 16, 16, 16, 16);
        return tex;
    }

    @Override
    public List<Component> getTooltip(Player player) {
        List<Component> componentList = new ArrayList<>();
        switch (getMoodleStatus()){
            case LIGHT -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.oxygen.title1"));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.oxygen.description1").withStyle(ChatFormatting.GRAY));
            }
            case NORMAL -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.oxygen.title2").withStyle(ChatFormatting.YELLOW));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.oxygen.description2").withStyle(ChatFormatting.GRAY));
            }
            case HEAVY -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.oxygen.title3").withStyle(ChatFormatting.GOLD));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.oxygen.description3").withStyle(ChatFormatting.GRAY));
            }
            case CRITICAL -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.oxygen.title4").withStyle(ChatFormatting.RED));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.oxygen.description4").withStyle(ChatFormatting.GRAY));
            }
        }
        return componentList;
    }
}
