package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.visual.ClientGamerules;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BlindnessViewSyncPacket {
    private final int value;

    public BlindnessViewSyncPacket(int value) {
        this.value = value;
    }

    public static BlindnessViewSyncPacket decode(FriendlyByteBuf buf) {
        return new BlindnessViewSyncPacket(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(value);
    }

    public static void handle(BlindnessViewSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientGamerules.blindnessViewDistance = msg.value;
        });
        ctx.get().setPacketHandled(true);
    }
}