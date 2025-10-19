package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class ShrapnelFailPacket {
    private final UUID target;
    private final Limb limb;
    public ShrapnelFailPacket(UUID target, Limb limb){
        this.target = target;
        this.limb = limb;
    }

    public ShrapnelFailPacket(FriendlyByteBuf buf){
        this.target = buf.readUUID();
        this.limb = buf.readEnum(Limb.class);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.target);
        buf.writeEnum(this.limb);
    }

    public static void handle(ShrapnelFailPacket msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayer sender = ctx.get().getSender();
            if (sender!=null){
                Entity entity = sender.serverLevel().getEntity(msg.target);
                if (entity instanceof Player player){
                    player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        Random random = new Random();
                        h.setLimbPain(msg.limb, h.getLimbPain(msg.limb)+(random.nextFloat()+1)*6);
                        h.setLimbMuscleHealth(msg.limb,h.getLimbMuscleHealth(msg.limb)-(random.nextFloat()+0.5f)*2.5f);
                        h.setLimbSkinHealth(msg.limb,h.getLimbSkinHealth(msg.limb)-(random.nextFloat()+0.5f)*3.5f);
                        h.applyBleedDamage(msg.limb,(random.nextFloat()+0.5f)/5,null);
                    });
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
