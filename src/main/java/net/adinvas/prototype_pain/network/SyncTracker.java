package net.adinvas.prototype_pain.network;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
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
                        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), new SyncHealthPacket(tag, target.getUUID())
                        );
                    });
                }
            }
        }
    }

    static int tickCounter = 0;
    public static void tickEveryone(MinecraftServer server){
        tickCounter++;
        if (tickCounter<3){
            return;
        }else{
            tickCounter = 0;
        }
        for (ServerPlayer viewer : server.getPlayerList().getPlayers()) {
            UUID targetId = syncing.get(viewer.getUUID());
            if (targetId==viewer.getUUID())return;
            targetId = viewer.getUUID();
            if (targetId != null) {
                ServerPlayer target = server.getPlayerList().getPlayer(targetId);
                if (target != null) {
                    target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(cap -> {
                        CompoundTag tag = cap.serializeNBT(new CompoundTag());
                        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), new SyncHealthPacket(tag, target.getUUID())
                        );
                    });
                }
            }
        }
    }

    static int tickCounterReduced = 0;
    public static void tickEveryoneReducedBroadcast(MinecraftServer server){
        tickCounterReduced++;
        if (tickCounterReduced<20){
            return;
        }else{
            tickCounterReduced = 0;
        }
        for (ServerPlayer viewer : server.getPlayerList().getPlayers()) {
            for (ServerPlayer target : server.getPlayerList().getPlayers()) {
                boolean isDirty = target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->h.isReducedDirty).orElse(false);
                if (!isDirty)continue;
                target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(cap -> {
                    CompoundTag tag = cap.serilizeReducedNbt(new CompoundTag());
                    ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), new SyncHealthPacket(tag, target.getUUID())
                    );
                });
            }
        }
    }

    public static void onJoin(ServerPlayer viewer,MinecraftServer server){
        for (ServerPlayer target : server.getPlayerList().getPlayers()) {
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(cap -> {
                CompoundTag tag = cap.serilizeReducedNbt(new CompoundTag());
                ModNetwork.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> viewer),
                        new SyncHealthPacket(tag, target.getUUID())
                );
            });
        }

// Then send viewer's data TO everyone else
        viewer.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(cap -> {
            CompoundTag tag = cap.serilizeReducedNbt(new CompoundTag());
            for (ServerPlayer target : server.getPlayerList().getPlayers()) {
                if (target != viewer) {
                    ModNetwork.CHANNEL.send(
                            PacketDistributor.PLAYER.with(() -> target),
                            new SyncHealthPacket(tag, viewer.getUUID())
                    );
                }
            }
        });
    }


}