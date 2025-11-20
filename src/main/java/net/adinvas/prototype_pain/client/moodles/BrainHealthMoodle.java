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

public class BrainHealthMoodle extends AbstractMoodleVisual{
    @Override
    public MoodleStatus calculateStatus(Player player) {
        float brain = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getBrainHealth).orElse(100f);
        if (brain<=30){
            return MoodleStatus.CRITICAL;
        }else if (brain<=60){
            return MoodleStatus.HEAVY;
        }else if (brain<=80){
            return MoodleStatus.NORMAL;
        }else if (brain<=95){
            return MoodleStatus.LIGHT;
        }else {
            return MoodleStatus.NONE;
        }
    }

    @Override
    public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks, int x, int y) {
        ResourceLocation tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/brainhealth.png");
        ms.blit(tex, x, y, 0, 0, 16, 16, 16, 16);
        return tex;
    }

    @Override
    public List<Component> getTooltip(Player player) {
        List<Component> componentList = new ArrayList<>();
        switch (getMoodleStatus()){
            case LIGHT -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.brain_health.title1"));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.brain_health.description1").withStyle(ChatFormatting.GRAY));
            }
            case NORMAL -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.brain_health.title2").withStyle(ChatFormatting.YELLOW));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.brain_health.description2").withStyle(ChatFormatting.GRAY));
            }
            case HEAVY -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.brain_health.title3").withStyle(ChatFormatting.GOLD));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.brain_health.description3").withStyle(ChatFormatting.GRAY));
            }
            case CRITICAL -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.brain_health.title4").withStyle(ChatFormatting.RED));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.brain_health.description4").withStyle(ChatFormatting.GRAY));
            }
        }
        return componentList;
    }


}
