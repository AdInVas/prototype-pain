package net.adinvas.prototype_pain.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class GuiSyncTogglePacket {
    private final boolean enabled;
    private final UUID targetId;

    public GuiSyncTogglePacket(boolean enable, UUID targetId) {
        this.enabled = enable;
        this.targetId = targetId;
    }

    public GuiSyncTogglePacket(FriendlyByteBuf buf) {
        this.enabled = buf.readBoolean();
        this.targetId = buf.readUUID();
    }


    public void write(FriendlyByteBuf buf){
        buf.writeBoolean(enabled);
        buf.writeUUID(targetId);
    }

    public static void handle(GuiSyncTogglePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                if (msg.enabled) {
                    SyncTracker.add(player.getUUID(),msg.targetId);
                } else {
                    SyncTracker.remove(player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
