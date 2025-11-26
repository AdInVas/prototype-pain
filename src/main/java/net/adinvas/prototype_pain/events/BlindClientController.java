package net.adinvas.prototype_pain.events;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.adinvas.prototype_pain.ModGamerules;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.adinvas.prototype_pain.visual.ClientGamerules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlindClientController {
    @SubscribeEvent
    public static void fogEvent(ViewportEvent.RenderFog event){
        Minecraft mc = Minecraft.getInstance();

        if (mc.player==null)return;
        boolean leftEye = mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::isLeftEyeBlind).orElse(false);
        boolean rightEye = mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::isRightEyeBlind).orElse(false);
        if (leftEye&&rightEye) {
            event.setFarPlaneDistance(ClientGamerules.blindnessViewDistance);
            event.setNearPlaneDistance(0);
            event.setFogShape(FogShape.SPHERE);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void fogEvent2(ViewportEvent.ComputeFogColor event){
        Minecraft mc = Minecraft.getInstance();

        if (mc.player==null)return;
        boolean leftEye = mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::isLeftEyeBlind).orElse(false);
        boolean rightEye = mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::isRightEyeBlind).orElse(false);
        if (leftEye&&rightEye) {
            event.setBlue(1);
            event.setRed(1);
            event.setGreen(1);
        }

    }

}
