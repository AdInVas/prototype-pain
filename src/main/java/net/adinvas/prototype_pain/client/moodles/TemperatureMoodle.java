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

public class TemperatureMoodle extends AbstractMoodleVisual{
    boolean low= false;
    @Override
    MoodleStatus calculateStatus(Player player) {
        float temp = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getTemperature).orElse(36.6f);
        if (temp<36.6){
            low = true;
        }else{
            low = false;
        }
        if (temp<=28||temp>=41.5){
            return MoodleStatus.CRITICAL;
        }else if (temp<=32.5||temp>=40.25){
            return MoodleStatus.HEAVY;
        }else if (temp<=34||temp>=39){
            return MoodleStatus.NORMAL;
        }else if (temp<=35.5||temp>=38){
            return MoodleStatus.LIGHT;
        }else {
            return MoodleStatus.NONE;
        }
    }

    @Override
    public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks, int x, int y) {
        ResourceLocation tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/temphigh.png");
        if (low){
            tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/templow.png");
        }

        ms.blit(tex, x, y, 0, 0, 16, 16, 16, 16);
        return tex;
    }

    @Override
    public List<Component> getTooltip(Player player) {
        List<Component> componentList = new ArrayList<>();
        switch (getMoodleStatus()){
            case LIGHT -> {
                if (low) {
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.low_temp.title1"));
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.low_temp.description1").withStyle(ChatFormatting.GRAY));
                }else{
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.high_temp.title1"));
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.high_temp.description1").withStyle(ChatFormatting.GRAY));
                }
            }
            case NORMAL -> {
                if (low) {
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.low_temp.title2").withStyle(ChatFormatting.YELLOW));
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.low_temp.description2").withStyle(ChatFormatting.GRAY));
                }else{
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.high_temp.title2").withStyle(ChatFormatting.YELLOW));
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.high_temp.description2").withStyle(ChatFormatting.GRAY));
                }
            }
            case HEAVY -> {
                if (low) {
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.low_temp.title3").withStyle(ChatFormatting.GOLD));
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.low_temp.description3").withStyle(ChatFormatting.GRAY));
                }else{
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.high_temp.title3").withStyle(ChatFormatting.GOLD));
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.high_temp.description3").withStyle(ChatFormatting.GRAY));
                }
            }
            case CRITICAL -> {
                if (low) {
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.low_temp.title4").withStyle(ChatFormatting.RED));
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.low_temp.description4").withStyle(ChatFormatting.GRAY));
                }else{
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.high_temp.title4").withStyle(ChatFormatting.RED));
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.high_temp.description4").withStyle(ChatFormatting.GRAY));
                }
            }
        }
        return componentList;
    }
}
