package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.visual.ClientGamerules;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AmputateRescrictionSyncPacket {
    private final boolean value;

    public AmputateRescrictionSyncPacket(boolean value) {
        this.value = value;
    }

    public static AmputateRescrictionSyncPacket decode(FriendlyByteBuf buf) {
        return new AmputateRescrictionSyncPacket(buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(value);
    }

    public static void handle(AmputateRescrictionSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientGamerules.AmputateRestriction = msg.value;
        });
        ctx.get().setPacketHandled(true);
    }
}