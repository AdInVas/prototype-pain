package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.fluid_system.n.INMedicalFluidContainer;
import net.adinvas.prototype_pain.fluid_system.n.MultiTankFluidItem;
import net.adinvas.prototype_pain.fluid_system.n.MultiTankHelper;
import net.adinvas.prototype_pain.item.IMedicalFluidContainer;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
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
                if (src.getItem() instanceof MultiTankFluidItem from && trg.getItem() instanceof MultiTankFluidItem to){
                    List<FluidStack> drained = MultiTankHelper.drain(src,msg.amount);
                    for (FluidStack stack : drained){
                        PrototypePain.LOGGER.info("fluid {}, amount {}",stack.getFluid(),stack.getAmount());
                       MultiTankHelper.addFluid(trg,stack.getAmount(),stack);
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
