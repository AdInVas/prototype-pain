package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UseSyringePacket {
    private final String[] ids;
    private final float[] amounts;
    private final UUID target;
    private final Limb limb;

    public UseSyringePacket(String[] ids, float[] amounts, UUID target,Limb limb) {
        this.ids = ids;
        this.amounts = amounts;
        this.target = target;
        this.limb = limb;
    }

    public UseSyringePacket(FriendlyByteBuf buf){
        int idCount = buf.readVarInt();
        String[] ids = new String[idCount];
        for (int i = 0; i < idCount; i++) {
            ids[i] = buf.readUtf();
        }

        int amountCount = buf.readVarInt();
        float[] amounts = new float[amountCount];
        for (int i = 0; i < amountCount; i++) {
            amounts[i] = buf.readFloat();
        }

        UUID target = buf.readUUID();
        this.ids = ids;
        this.amounts = amounts;
        this.target = target;
        this.limb = buf.readEnum(Limb.class);
    }

    public void write(FriendlyByteBuf buf){
        buf.writeVarInt(this.ids.length);
        for (String id : this.ids){
            buf.writeUtf(id);
        }

        buf.writeVarInt(this.amounts.length);
        for (Float amount : this.amounts){
            buf.writeFloat(amount);
        }

        buf.writeUUID(this.target);
        buf.writeEnum(limb);
    }

    public static void handle(UseSyringePacket msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayer sender = ctx.get().getSender();
            if (sender!=null){
                Entity entity = sender.serverLevel().getEntity(msg.target);
                if (entity instanceof ServerPlayer player){
                    for (int i=0;i<msg.ids.length;i++){
                        MedicalFluid fluid = MedicalFluid.getFromId(msg.ids[i],sender.serverLevel());
                        float amount = msg.amounts[i];
                        if (fluid!=null&& !Float.isNaN(amount)){
                            fluid.getMedicalEffect().applyInjected(player,amount,msg.limb);
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
