package net.adinvas.prototype_pain.events;

import net.adinvas.prototype_pain.Keybinds;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.client.HealthScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PrototypePain.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if (Keybinds.OPEN_PAIN_GUI.isDown()) {
            Keybinds.OPEN_PAIN_GUI.consumeClick();

            Player target = ModEvents.getLookedAtPlayer(event.player, 2);
            boolean self = target == null || event.player.isShiftKeyDown();
            if (event.side== LogicalSide.CLIENT) {
                if (self) {
                    Minecraft.getInstance().setScreen(new HealthScreen(event.player.getUUID()));
                } else {
                    Minecraft.getInstance().setScreen(new HealthScreen(target.getUUID()));
                }
            }
        }
    }
}