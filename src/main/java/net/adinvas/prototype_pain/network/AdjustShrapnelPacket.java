package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class AdjustShrapnelPacket {
    private final UUID target;
    private final Limb limb;
    private final int amount;
    public AdjustShrapnelPacket(UUID target, Limb limb, int amount){
        this.target = target;
        this.limb = limb;
        this.amount = amount;
    }

    public AdjustShrapnelPacket(FriendlyByteBuf buf){
        this.target = buf.readUUID();
        this.limb = buf.readEnum(Limb.class);
        this.amount = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.target);
        buf.writeEnum(this.limb);
        buf.writeInt(this.amount);
    }

    public static void handle(AdjustShrapnelPacket msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayer sender = ctx.get().getSender();
            if (sender!=null){
                Entity entity = sender.serverLevel().getEntity(msg.target);
                if (entity instanceof Player player){
                    player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        h.setLimbShrapnell(msg.limb,msg.amount);
                    });
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
