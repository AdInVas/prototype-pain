package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public class ClientPacketHandlers {
    public static void handleSyncHealth(SyncHealthPacket msg) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer viewer = mc.player;
        if (viewer == null || mc.level == null) return;

        Player target = mc.level.getPlayerByUUID(msg.target);
        if (target == null) return;

        // If we're syncing our own data, make sure to update viewer capability too
        if (target == viewer) {
            viewer.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(cap -> {
                cap.deserializeNBT(msg.tag);
            });
        } else {
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(cap -> {
                cap.deserializeNBT(msg.tag);
            });
        }
    }
}
