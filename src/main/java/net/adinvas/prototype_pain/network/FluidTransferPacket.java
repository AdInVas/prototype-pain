package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.fluid_system.items.IMedicalFluidContainer;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class FluidTransferPacket {
    private final ItemStack source;
    private final ItemStack target;
    private final int helperSlot;
    private final float amount;


    public FluidTransferPacket(ItemStack source, ItemStack target,int helperSlot, float amount) {
        this.source = source;
        this.target = target;
        this.helperSlot = helperSlot;
        this.amount = amount;
    }

    public FluidTransferPacket(FriendlyByteBuf buf) {
        this.source = buf.readItem();
        this.target = buf.readItem();
        this.helperSlot = buf.readInt();
        this.amount = buf.readFloat();
    }


    public void write(FriendlyByteBuf buf){
        buf.writeItem(source);
        buf.writeItem(target);
        buf.writeInt(helperSlot);
        buf.writeFloat(amount);
    }

    public static void handle(FluidTransferPacket msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayer sender = ctx.get().getSender();
            if (sender!=null){
                ItemStack src = msg.source;
                ItemStack trg = msg.target;
                if (src.getItem() instanceof IMedicalFluidContainer from && trg.getItem() instanceof IMedicalFluidContainer to){
                    Map<MedicalFluid,Float> map = from.drain(src, msg.amount);
                    for (MedicalFluid fluid : map.keySet()){
                        to.addFluid(trg,map.get(fluid),fluid);
                    }
                    sender.containerMenu.setCarried(ItemStack.EMPTY);
                    sender.containerMenu.broadcastChanges();
                    sender.containerMenu.setCarried(src);
                    sender.getInventory().setItem(msg.helperSlot,trg);
                    sender.containerMenu.broadcastChanges();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
