package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class DislocationTryPacket {
    private final UUID target;
    private final Limb limb;
    private final float dislocationvalue;


    public DislocationTryPacket(UUID target, Limb limb, float dislocationvalue) {
        this.target = target;
        this.limb = limb;
        this.dislocationvalue = dislocationvalue;
    }

    public DislocationTryPacket(FriendlyByteBuf buf){
        this.target = buf.readUUID();
        this.limb = buf.readEnum(Limb.class);
        this.dislocationvalue = buf.readFloat();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeUUID(target);
        buf.writeEnum(limb);
        buf.writeFloat(dislocationvalue);
    }

    public static void handle(DislocationTryPacket msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayer sender = ctx.get().getSender();
            if (sender!=null){
                Entity entity = sender.serverLevel().getEntity(msg.target);
                if (entity instanceof Player player){
                    player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        h.setLimbDislocation(msg.limb, msg.dislocationvalue);
                        h.setLimbPain(msg.limb, (float) (h.getLimbPain(msg.limb)+(Math.random()*20)+20));
                    });
                }
            }
        });
    }
}
