package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.events.BrainDamageClientController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BrainEventPacket {
    private final BrainDamageClientController.BrainEvents event;

    public BrainEventPacket(BrainDamageClientController.BrainEvents event) {
        this.event = event;
    }

    public BrainEventPacket(FriendlyByteBuf buf) {
        this.event = buf.readEnum(BrainDamageClientController.BrainEvents.class);
    }
    public void write(FriendlyByteBuf buf){
        buf.writeEnum(event);
    }

    public static void handle(BrainEventPacket msg, Supplier<NetworkEvent.Context> ctx){
        NetworkEvent.Context c = ctx.get();
        c.enqueueWork(()->{
            ServerPlayer player = c.getSender();
            if (player==null)return;
            switch (msg.event){
                case TINNITUS -> {
                    player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        h.setFlashHearingLoss(1);
                    });
                }
                case VOMIT -> {
                    player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        h.setVomit(1);
                    });
                }
            }
        });
        c.setPacketHandled(true);
    }

}
