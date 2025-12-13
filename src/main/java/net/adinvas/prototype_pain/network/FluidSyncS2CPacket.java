package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.blocks.medical_mixer.MedicalMixerBlockEntity;
import net.adinvas.prototype_pain.blocks.medical_mixer.MedicalMixerMenu;
import net.adinvas.prototype_pain.blocks.medical_mixer.MedicalMixerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FluidSyncS2CPacket {
    private final FluidStack fluid;
    private final int tankID;
    private final BlockPos pos;

    public FluidSyncS2CPacket(FluidStack stack, int tankID, BlockPos pos){
        this.fluid = stack;
        this.tankID = tankID;
        this.pos = pos;
    }
    public FluidSyncS2CPacket(FriendlyByteBuf buf){
        this.fluid = buf.readFluidStack();
        this.tankID = buf.readInt();
        this.pos = buf.readBlockPos();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeFluidStack(fluid);
        buf.writeInt(tankID);
        buf.writeBlockPos(pos);
    }

    public static void handle(FluidSyncS2CPacket msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if (Minecraft.getInstance().level.getBlockEntity(msg.pos) instanceof MedicalMixerBlockEntity blockEntity){
                blockEntity.setFluidInTank(msg.fluid, msg.tankID);

                if (Minecraft.getInstance().player.containerMenu instanceof MedicalMixerMenu Menu &&
                    Menu.getBlockEntity().getBlockPos().equals(msg.pos)){
                    Menu.setFluidInTank(msg.fluid,msg.tankID);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
