package net.adinvas.prototype_pain.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExchangeItemInHandPacket {
    private final ItemStack target;
    private final boolean offhand;

    public ExchangeItemInHandPacket(ItemStack target, boolean offhand) {
        this.target = target;
        this.offhand = offhand;
    }

    public ExchangeItemInHandPacket(FriendlyByteBuf buf) {
        this.target = buf.readItem();
        this.offhand = buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeItem(this.target);
        buf.writeBoolean(this.offhand);
    }

    public static void handle(ExchangeItemInHandPacket msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayer sender = ctx.get().getSender();
            if (sender!=null){
                ItemStack trg = msg.target;
               if (trg!=null){
                   if (msg.offhand){
                       sender.setItemInHand(InteractionHand.OFF_HAND,trg);
                   }else {
                       sender.setItemInHand(InteractionHand.MAIN_HAND,trg);
                   }
               }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
