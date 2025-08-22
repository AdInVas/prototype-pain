package net.adinvas.prototype_pain.events;

import net.adinvas.prototype_pain.Keybinds;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.client.ContiousnessOverlay;
import net.adinvas.prototype_pain.client.OverlayController;
import net.adinvas.prototype_pain.client.PainOverlay;
import net.adinvas.prototype_pain.client.gui.HealthScreen;
import net.adinvas.prototype_pain.network.LegUsePacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PrototypePain.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.ClientTickEvent event){
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player==null)return;
        if (event.side== LogicalSide.CLIENT) {
            if (Keybinds.OPEN_PAIN_GUI.isDown()) {
                Keybinds.OPEN_PAIN_GUI.consumeClick();

                Player target = ModEvents.getLookedAtPlayer(player, 2);
                boolean self = target == null || player.isShiftKeyDown();

                if (self) {
                    Minecraft.getInstance().setScreen(new HealthScreen(player.getUUID()));
                } else {
                    Minecraft.getInstance().setScreen(new HealthScreen(target.getUUID()));
                }

            }
        }

        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                double pain = h.getTotalPain();
                float contiousness = (100-h.getContiousness())/100;
                pain = pain/100;
                if (contiousness>99){
                    pain = 0;
                }
                PainOverlay overlay = OverlayController.getOverlay(PainOverlay.class);
                overlay.setIntensity((float) pain);
                ContiousnessOverlay conc = OverlayController.getOverlay(ContiousnessOverlay.class);
                conc.setIntensity(contiousness);
                if (contiousness<=4){
                    mc.player.setYRot(mc.player.yRotO); // reset yaw
                    mc.player.setXRot(mc.player.xRotO); // reset pitch
                }
            });
    }

    @SubscribeEvent
    public static void onInputUpdate(MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        if (player!=null){
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                if (h.getContiousness()<5) { // your condition here
                    event.getInput().down = false;
                    event.getInput().forwardImpulse = 0;
                    event.getInput().jumping = false;
                    event.getInput().up = false;
                    event.getInput().left = false;
                    event.getInput().leftImpulse= 0;
                    event.getInput().right =false;
                    event.getInput().shiftKeyDown = false;
                }
                if (event.getInput().leftImpulse!=0||event.getInput().forwardImpulse!=0){
                    ModNetwork.CHANNEL.sendToServer(new LegUsePacket());
                }
            });
        }
    }
}