package net.adinvas.prototype_pain.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BrainDamageClientController {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event){
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player!=null){
            float brain= player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getBrainHealth).orElse(100f);

            if (brain < 95 && mc.level != null && mc.player != null) {
                if (mc.level.getGameTime() % 400 == 0&&mc.level.random.nextFloat()>1-brain/120f) { // every 20s roughly
                    float alpha = 0.2f + mc.level.random.nextFloat() * 0.3f;
                    GuiGraphics g = event.getGuiGraphics();
                    g.fill(0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(),
                            ((int) (alpha * 255) << 24));
                }
            }
        }
    }


    private static float time = 0f;
    private static float currentPitchOffset = 0f;
    private static float currentYawOffset = 0f;

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        float brain= mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getBrainHealth).orElse(100f);
        if (brain >= 95) return;

        // Delta time-like increment (ensures consistent speed)
        time += 0.01f;

        // Base amplitude grows as brain health drops
        float strength = (1f - brain / 100f); // 0..1
        float maxAngle = 5f * strength;       // up to 3 degrees at very low brain

        // Combine two gentle sine waves of different frequencies for organic motion
        float targetPitch = (float) (
                Math.sin(time * 0.6f) * 0.7f +   // slow, large wave
                        Math.sin(time * 1.7f) * 0.3f     // faster, smaller wave
        ) * maxAngle;

        float targetYaw = (float) (
                Math.cos(time * 0.4f) * 0.5f +
                        Math.sin(time * 1.3f) * 0.3f
        ) * maxAngle * 0.7f;

        // Smoothly interpolate (lerp) from current to target
        float smooth = 0.05f; // smaller = smoother
        currentPitchOffset += (targetPitch - currentPitchOffset) * smooth;
        currentYawOffset += (targetYaw - currentYawOffset) * smooth;

        // Apply to camera
        event.setPitch(event.getPitch() + currentPitchOffset);
        event.setYaw(event.getYaw() + currentYawOffset);

        // Optional: a very subtle micro jitter when brain <80
        if (brain < 80) {
            float intensity = (80f - brain) / 80f * 0.2f; // subtle
            float jitterPitch = (mc.level.random.nextFloat() - 0.5f) * intensity;
            float jitterYaw = (mc.level.random.nextFloat() - 0.5f) * intensity;
            event.setPitch(event.getPitch() + jitterPitch);
            event.setYaw(event.getYaw() + jitterYaw);
        }
    }

    private static Double baseSensitivity = -1d;

    @SubscribeEvent
    public static void onclientTick(TickEvent.ClientTickEvent event){
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player==null)return;
            float brain = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getBrainHealth).orElse(100f);

            if (baseSensitivity == -1f)
                baseSensitivity = mc.options.sensitivity().get();

            if (brain < 80) {
                double t = mc.level.getGameTime();
                double factor = 1.0 + Math.sin(t * 0.1) * 0.05; // ±5%
                mc.options.sensitivity().set((baseSensitivity * factor));
            } else {
                mc.options.sensitivity().set(baseSensitivity);
            }


        }
    }

    @SubscribeEvent
    public static void onTooltip(RenderTooltipEvent.GatherComponents event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player==null)return;
        if (player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getBrainHealth).orElse(100f)< 60) {
            event.getTooltipElements().clear();
        }
    }


}
