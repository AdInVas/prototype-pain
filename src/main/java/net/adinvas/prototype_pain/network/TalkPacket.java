package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TalkPacket {
    public TalkPacket() {}

    // Decoder (from bytes)
    public TalkPacket(FriendlyByteBuf buf) {
        // No payload to read
    }

    // Encoder (to bytes)
    public void toBytes(FriendlyByteBuf buf) {
        // No payload to write
    }

    // Handle on server
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                    if (h.getLimbDislocated(Limb.HEAD)>0){
                        h.setLimbPain(Limb.HEAD,h.getLimbPain(Limb.HEAD)+0.3f);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
