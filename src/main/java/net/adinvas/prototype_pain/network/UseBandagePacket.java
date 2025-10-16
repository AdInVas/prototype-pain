package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.item.IMedicalMinigameUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UseBandagePacket {
    private final ItemStack itemStack;
    private final UUID target;
    private final float durability;
    private final Limb limb;

    public UseBandagePacket(ItemStack itemStack,UUID target, float durability,Limb limb){
        this.itemStack = itemStack;
        this.target = target;
        this.durability = durability;
        this.limb =limb;
    }

    public UseBandagePacket(FriendlyByteBuf buf){
        this.itemStack = buf.readItem();
        this.target = buf.readUUID();
        this.durability = buf.readFloat();
        this.limb = buf.readEnum(Limb.class);
    }

    public void write(FriendlyByteBuf buf){
        buf.writeItem(itemStack);
        buf.writeUUID(target);
        buf.writeFloat(durability);
        buf.writeEnum(limb);
    }


    public static void handle(UseBandagePacket msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayer sender = ctx.get().getSender();
            if (sender!=null){
                Entity entity = sender.serverLevel().getEntity(msg.target);
                if (entity instanceof Player target){
                    ItemStack stack = msg.itemStack;
                    if (stack.getItem() instanceof IMedicalMinigameUsable medicalMinigameUsable){
                        medicalMinigameUsable.useMinigameAction(msg.durability,target,msg.limb);
                    }
                }
            }
        });
    }
}
