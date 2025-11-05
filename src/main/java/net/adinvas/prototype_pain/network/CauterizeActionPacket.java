package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class CauterizeActionPacket {
    private final Limb limb ;
    private final UUID target;

    public CauterizeActionPacket(Limb limb, UUID target){
        this.limb=limb;
        this.target=target;
    }

    public CauterizeActionPacket(FriendlyByteBuf buf){
        this.limb = Limb.valueOf(buf.readUtf());
        this.target = buf.readUUID();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeUtf(limb.name());
        buf.writeUUID(target);
    }

    public static void handle(CauterizeActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer source = ctx.get().getSender();
            ServerPlayer target = ctx.get().getSender().getServer().getPlayerList().getPlayer(msg.target);
            if (target==null||source==null)return;
            Limb limb = msg.limb;
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(health->{
                Random random = new Random();
                health.setLimbPain(limb,health.getLimbPain(limb)+(random.nextFloat()+1)*80);
                health.setLimbBleedRate(limb,health.getLimbBleedRate(limb)*0.4f);
                health.setLimbMuscleHealth(limb, health.getLimbMuscleHealth(limb)-(random.nextFloat()+1)*15);
                health.setLimbSkinHealth(limb, health.getLimbSkinHealth(limb)-(random.nextFloat()+1)*25);
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
