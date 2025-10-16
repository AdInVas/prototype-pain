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

public class ConsiousnessMoodle extends AbstractMoodleVisual{
    public boolean fullyUNC = false;

    @Override
    MoodleStatus calculateStatus(Player player) {
        Optional<Float> cons = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getContiousness);
        if (cons.orElse(100f)<5) {
            fullyUNC = true;
            return MoodleStatus.CRITICAL;
        }
        else if (cons.orElse(100f)<30){
            fullyUNC = false;
            return MoodleStatus.CRITICAL;
        }else if (cons.orElse(100f)<55){
            fullyUNC = false;
            return MoodleStatus.HEAVY;
        }else if (cons.orElse(100f)<75){
            fullyUNC = false;
            return MoodleStatus.NORMAL;
        }else if (cons.orElse(100f)<90){
            fullyUNC = false;
            return MoodleStatus.LIGHT;
        }else {
            fullyUNC = false;
            return MoodleStatus.NONE;
        }
    }

    @Override
    public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks, int x, int y) {
        ResourceLocation tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/consious_moodle.png");
        if (fullyUNC){
            tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/unconsious_moodle.png");
        }
        ms.blit(tex, x, y, 0, 0, 16, 16, 16, 16);
        return tex;
    }

    @Override
    public List<Component> getTooltip(Player player) {
        List<Component> componentList = new ArrayList<>();
        switch (getMoodleStatus()){
            case LIGHT -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.consiousness.title1"));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.consiousness.description1").withStyle(ChatFormatting.GRAY));
            }
            case NORMAL -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.consiousness.title2").withStyle(ChatFormatting.YELLOW));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.consiousness.description2").withStyle(ChatFormatting.GRAY));
            }
            case HEAVY -> {
                componentList.add(Component.translatable("prototype_pain.gui.moodle.consiousness.title3").withStyle(ChatFormatting.GOLD));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.consiousness.description3").withStyle(ChatFormatting.GRAY));
            }
            case CRITICAL -> {
                if (fullyUNC){
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.consiousness.title5").withStyle(ChatFormatting.RED));
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.consiousness.description5").withStyle(ChatFormatting.GRAY));
                }else{
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.consiousness.title4").withStyle(ChatFormatting.RED));
                    componentList.add(Component.translatable("prototype_pain.gui.moodle.consiousness.description4").withStyle(ChatFormatting.GRAY));
                }
            }
        }
        return componentList;
    }
}
