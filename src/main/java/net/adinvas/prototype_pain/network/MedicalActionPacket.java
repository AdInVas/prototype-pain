package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MedicalActionPacket {
    private final MedicalAction action;
    private final Limb limb ;
    private final UUID target;

    public MedicalActionPacket(Limb limb, UUID target,MedicalAction action) {
        this.action = action;
        this.limb = limb;
        this.target = target;
    }
    public MedicalActionPacket(FriendlyByteBuf buf){
        this.action = MedicalAction.valueOf(buf.readUtf());
        this.limb = Limb.valueOf(buf.readUtf());
        this.target = buf.readUUID();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeUtf(action.name());
        buf.writeUtf(limb.name());
        buf.writeUUID(target);
    }

    public static void handle(MedicalActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer source = ctx.get().getSender();
            ServerPlayer target = ctx.get().getSender().getServer().getPlayerList().getPlayer(msg.target);
            if (target==null||source==null)return;
            Limb limb = msg.limb;
            MedicalAction action = msg.action;
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.medicalAction(action,limb,source);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
