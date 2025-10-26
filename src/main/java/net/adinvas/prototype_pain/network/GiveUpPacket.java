package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GiveUpPacket {
    public GiveUpPacket() {}

    // Decoder (from bytes)
    public GiveUpPacket(FriendlyByteBuf buf) {
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
                player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA)
                        .ifPresent(h->{

                            h.killPlayer(player,true);
                        });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
