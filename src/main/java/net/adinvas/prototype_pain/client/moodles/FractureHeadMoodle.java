package net.adinvas.prototype_pain.client.moodles;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FractureHeadMoodle extends AbstractMoodleVisual{
    public static List<Limb> checkList = new ArrayList<>();
    static {
        checkList.add(Limb.HEAD);
    }
    @Override
    public MoodleStatus calculateStatus(Player player) {
        Optional<Boolean> fractured = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->{
            for (Limb limb: checkList){
                if (h.getLimbFracture(limb)>0){
                    return true;
                }
            }
            return false;
        });

        if (fractured.orElse(false)){
            return MoodleStatus.HEAVY;
        }else {
            return MoodleStatus.NONE;
        }
    }

    @Override
    public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks, int x, int y) {
        ResourceLocation tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/fractured_neck_moodle.png");
        ms.blit(tex, x, y, 0, 0, 16, 16, 16, 16);
        return tex;
    }

    @Override
    public List<Component> getTooltip(Player player) {
        List<Component> componentList = new ArrayList<>();
        componentList.add(Component.translatable("prototype_pain.gui.moodle.fracture_head.title3").withStyle(ChatFormatting.GOLD));
        componentList.add(Component.translatable("prototype_pain.gui.moodle.fracture_head.description3").withStyle(ChatFormatting.GRAY));
        return componentList;
    }
}
