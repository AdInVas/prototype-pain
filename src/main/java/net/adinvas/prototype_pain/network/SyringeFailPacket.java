package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class SyringeFailPacket {
    private final UUID target;
    private final Limb limb;
    public SyringeFailPacket(UUID target, Limb limb){
        this.target = target;
        this.limb = limb;
    }

    public SyringeFailPacket(FriendlyByteBuf buf){
        this.target = buf.readUUID();
        this.limb = buf.readEnum(Limb.class);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.target);
        buf.writeEnum(this.limb);
    }

    public static void handle(SyringeFailPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Entity entity = player.serverLevel().getEntity(msg.target);
                if (entity instanceof Player target){
                    target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        Random random = new Random();
                        h.setLimbPain(msg.limb, (float) (h.getLimbPain(msg.limb)+((random.nextFloat()+0.5f)*20)));
                        h.setLimbShrapnell(msg.limb,h.hasLimbShrapnell(msg.limb)+1);
                    });
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
