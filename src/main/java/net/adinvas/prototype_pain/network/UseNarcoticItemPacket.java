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

public class UseNarcoticItemPacket {
    private final ItemStack itemstack;
    private final float amountUsed;
    private final UUID target;
    private final boolean offHand;

    public UseNarcoticItemPacket(ItemStack itemstack, float amountUsed, UUID target, boolean offHand){
        this.itemstack=itemstack;
        this.amountUsed=amountUsed;
        this.target=target;
        this.offHand = offHand;
    }

    public UseNarcoticItemPacket(FriendlyByteBuf buf){
        this.itemstack = buf.readItem();
        this.amountUsed = buf.readFloat();
        this.target = buf.readUUID();
        this.offHand = buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeItem(itemstack);
        buf.writeFloat(amountUsed);
        buf.writeUUID(target);
        buf.writeBoolean(offHand);
    }

    public static void handle(UseNarcoticItemPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer source = ctx.get().getSender();
            ServerPlayer target = ctx.get().getSender().getServer().getPlayerList().getPlayer(msg.target);
            if (target==null||source==null)return;
            InteractionHand hand = msg.offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            ItemStack stack = source.getItemInHand(hand);
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(health->{
                boolean used = health.tryUseItem(msg.amountUsed,stack,source,target,hand);
                if (used){
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
