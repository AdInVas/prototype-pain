package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.adinvas.prototype_pain.item.usable.LifeStrawItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClickedOnFluidPacket {
    private final MedicalFluid fluid;


    public ClickedOnFluidPacket(MedicalFluid fluid) {
        this.fluid = fluid;
    }

    public ClickedOnFluidPacket(FriendlyByteBuf buf){
        this.fluid = MedicalFluids.get(buf.readUtf());
    }

    public void write(FriendlyByteBuf buf){
        buf.writeUtf(fluid.getId());
    }

    public static void handle(ClickedOnFluidPacket msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayer serverPlayer = ctx.get().getSender();
            PrototypePain.LOGGER.info("ITEM {}",msg.fluid);
            if (serverPlayer==null)return;
            ItemStack stack = serverPlayer.getMainHandItem();

            if (stack.isEmpty()){
                msg.fluid.getMedicalEffect().applyIngested(serverPlayer,50);
            } else if (stack.getItem() instanceof LifeStrawItem straw) {
                ItemStack output = straw.drinkAction(stack,serverPlayer,msg.fluid,50);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
