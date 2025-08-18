package net.adinvas.prototype_pain.network;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import java.util.*;


public class SyncTracker {
    private static final Map<UUID, UUID> syncing = new HashMap<>();

    public static void add(UUID viewer, UUID targetId) {
        syncing.put(viewer, targetId);
    }
    public static void remove(ServerPlayer player) {
        syncing.remove(player.getUUID());
    }
    public static void tick(MinecraftServer server) {
        for (ServerPlayer viewer : server.getPlayerList().getPlayers()) {
            UUID targetId = syncing.get(viewer.getUUID());
            if (targetId != null) {
                ServerPlayer target = server.getPlayerList().getPlayer(targetId);
                if (target != null) {
                    target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(cap -> {
                        CompoundTag tag = cap.serializeNBT(new CompoundTag());
                        //PrototypePain.LOGGER.info("Syncing {} -> {}, tag={}", viewer.getScoreboardName(), target.getScoreboardName(), tag);
                        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), new SyncHealthPacket(tag, target.getUUID())
                        );
                    });
                }
            }
        }
    }


}