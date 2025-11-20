package net.adinvas.prototype_pain.events;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.adinvas.prototype_pain.network.BrainEventPacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BrainDamageClientController {

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
            float jitterPitch = (float) ((Math.random() - 0.5f) * intensity);
            float jitterYaw = (float) ((Math.random() - 0.5f) * intensity);
            event.setPitch(event.getPitch() + jitterPitch);
            event.setYaw(event.getYaw() + jitterYaw);
        }
    }

    public static int sinceLastevent = 1000;

    @SubscribeEvent
    public static void onclientTick(TickEvent.ClientTickEvent event){
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            setupAnims(player);

            if (sinceLastevent<0){
                sinceLastevent =1000;
                if (player==null)return;
                if (player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getBrainHealth).orElse(100f)>96)return;
                if (Math.random()>0.77){
                    runBrainEvent(player);
                    sinceLastevent =3000;
                }
                }
            }else {
                sinceLastevent ++;
        }
    }
    public static void runBrainEvent(Player player){
        Random random = new Random();
        BrainDamageClientController.BrainEvents E =  BrainDamageClientController.BrainEvents.values()[random.nextInt(BrainDamageClientController.BrainEvents.values().length)];
        switch (E){
            case FLASH -> {
                FlashAnimTick++;
            }
            case LOBOTOMY -> {
                LobotomyAnimTIck++;
            }
            default -> {
                ModNetwork.CHANNEL.sendToServer(new BrainEventPacket(E));
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

    public enum BrainEvents{LOBOTOMY,FLASH,TINNITUS,VOMIT}

    public static int FlashAnimTick = 0;
    public static int LobotomyAnimTIck = 0;
    public static void setupAnims(Player player){
        if (FlashAnimTick>0){
            FlashAnimTick++;
            PrototypePain.LOGGER.info("Flash {}",FlashAnimTick);
            if(FlashAnimTick>80){
                FlashAnimTick =0;
            }
        }
        if (LobotomyAnimTIck>0){
            LobotomyAnimTIck++;
            PrototypePain.LOGGER.info("Lobotomy {}",LobotomyAnimTIck);
            if(LobotomyAnimTIck>80){
                LobotomyAnimTIck =0;
            }
        }
    }


}
