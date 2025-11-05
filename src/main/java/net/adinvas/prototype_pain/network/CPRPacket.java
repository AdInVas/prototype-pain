package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class CPRPacket {
    public enum Success{LOW,MEDIUM,HIGH}
    private final UUID target;
    private final Success success;

    public CPRPacket(UUID target, Success success){
        this.target=target;
        this.success = success;
    }

    public CPRPacket(FriendlyByteBuf buf){
        this.target = buf.readUUID();
        this.success = buf.readEnum(Success.class);
    }

    public void write(FriendlyByteBuf buf){
        buf.writeUUID(target);
        buf.writeEnum(success);
    }

    public static void handle(CPRPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer source = ctx.get().getSender();
            ServerPlayer target = ctx.get().getSender().getServer().getPlayerList().getPlayer(msg.target);
            if (target==null||source==null)return;
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(health->{
                Random random = new Random();

                switch (msg.success){
                    case LOW -> {
                        if (health.getLimbMuscleHealth(Limb.CHEST)<=5)
                            health.setOxygen(Math.max(health.getOxygen(),((random.nextFloat())/2+0.5f)*3));
                        else
                            health.setOxygen(Math.max(health.getOxygen(),((random.nextFloat())/2+0.5f)*6));

                        health.setLimbPain(Limb.CHEST,health.getLimbPain(Limb.CHEST)+ (random.nextFloat()+0.5f)*30);
                        if (random.nextInt(8)==0){
                            health.setLimbFracture(Limb.CHEST, health.getLimbFracture(Limb.CHEST)+10);
                        }
                        if (random.nextInt(2)==0){
                            health.setLimbMuscleHealth(Limb.CHEST, health.getLimbMuscleHealth(Limb.CHEST)-(random.nextFloat()+0.5f)*8);
                        }
                    }
                    case MEDIUM -> {
                        if (health.getLimbMuscleHealth(Limb.CHEST)<=5)
                            health.setOxygen(Math.max(health.getOxygen(),((random.nextFloat())/2+0.5f)*4));
                        else
                            health.setOxygen(Math.max(health.getOxygen(),((random.nextFloat())/2+0.5f)*8));
                        health.setLimbPain(Limb.CHEST,health.getLimbPain(Limb.CHEST)+ (random.nextFloat()+0.5f)*20);
                        if (random.nextInt(6)==0){
                            health.setLimbFracture(Limb.CHEST, health.getLimbFracture(Limb.CHEST)+10);
                        }
                        if (random.nextInt(4)==0){
                            health.setLimbMuscleHealth(Limb.CHEST, health.getLimbMuscleHealth(Limb.CHEST)-(random.nextFloat()+0.5f)*5);
                        }
                    }
                    case HIGH -> {
                        if (health.getLimbMuscleHealth(Limb.CHEST)<=5)
                            health.setOxygen(Math.max(health.getOxygen(),((random.nextFloat())/2+0.5f)*6));
                        else
                            health.setOxygen(Math.max(health.getOxygen(),((random.nextFloat())/2+0.5f)*12));
                        health.setLimbPain(Limb.CHEST,health.getLimbPain(Limb.CHEST)+ (random.nextFloat()+0.5f)*10);
                        if (random.nextInt(4)==0){
                            health.setLimbFracture(Limb.CHEST, health.getLimbFracture(Limb.CHEST)+10);
                        }
                        if (random.nextInt(8)==0){
                            health.setLimbMuscleHealth(Limb.CHEST, health.getLimbMuscleHealth(Limb.CHEST)-(random.nextFloat()+0.5f)*1);
                        }
                    }
                }
                health.setContiousness(health.getContiousness()-5);
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
