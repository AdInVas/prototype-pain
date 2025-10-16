package net.adinvas.prototype_pain.client.moodles;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public List<Component> getTooltip(Player player) {
        List<Component> componentList = new ArrayList<>();
        switch (getMoodleStatus()){
            case LIGHT -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.infection.title1"));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.infection.description1").withStyle(ChatFormatting.GRAY));
            }
            case NORMAL -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.infection.title2").withStyle(ChatFormatting.YELLOW));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.infection.description2").withStyle(ChatFormatting.GRAY));
            }
            case HEAVY -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.infection.title3").withStyle(ChatFormatting.GOLD));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.infection.description3").withStyle(ChatFormatting.GRAY));
            }
            case CRITICAL -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.infection.title4").withStyle(ChatFormatting.RED));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.infection.description4").withStyle(ChatFormatting.GRAY));
            }
        }
        return componentList;
    }
}
