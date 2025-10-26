package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.item.IBag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class ExchangeItemInBagPacket {
    private final ItemStack target;
    private final ItemStack bagStack;
    private final int slot;
    private final boolean offhand;

    public ExchangeItemInBagPacket(ItemStack target, boolean offhand,ItemStack bagstack,int slot) {
        this.target = target;
        this.offhand = offhand;
        this.bagStack = bagstack;
        this.slot = slot;
    }

    public ExchangeItemInBagPacket(FriendlyByteBuf buf) {
        this.target = buf.readItem();
        this.offhand = buf.readBoolean();
        this.bagStack = buf.readItem();
        this.slot =buf.readInt();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeItem(this.target);
        buf.writeBoolean(this.offhand);
        buf.writeItem(this.bagStack);
        buf.writeInt(this.slot);
    }

    public static void handle(ExchangeItemInBagPacket msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayer sender = ctx.get().getSender();
            if (sender!=null){
                ItemStack trg = msg.target;
                if (msg.bagStack.getItem() instanceof IBag iBag) {
                    List<ItemStack> items = iBag.getItems(msg.bagStack);

                    // Find the used item and remove/damage it
                    items.set(msg.slot,trg);

                    // Save the updated inventory back to the bag
                    iBag.setItems(msg.bagStack, items);

                    // Update the bag in the player hand
                    sender.setItemInHand(msg.offhand?InteractionHand.OFF_HAND:InteractionHand.MAIN_HAND, msg.bagStack);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
