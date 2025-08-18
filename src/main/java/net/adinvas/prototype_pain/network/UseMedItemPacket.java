package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UseMedItemPacket {
    private final ItemStack itemstack;
    private final Limb limb ;
    private final UUID target;
    private final boolean offHand;

    public UseMedItemPacket(ItemStack itemstack, Limb limb, UUID target, boolean offHand){
        this.itemstack=itemstack;
        this.limb=limb;
        this.target=target;
        this.offHand = offHand;
    }

    public UseMedItemPacket(FriendlyByteBuf buf){
        this.itemstack = buf.readItem();
        this.limb = Limb.valueOf(buf.readUtf());
        this.target = buf.readUUID();
        this.offHand = buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeItem(itemstack);
        buf.writeUtf(limb.name());
        buf.writeUUID(target);
        buf.writeBoolean(offHand);
    }

    public static void handle(UseMedItemPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer source = ctx.get().getSender();
            ServerPlayer target = ctx.get().getSender().getServer().getPlayerList().getPlayer(msg.target);
            if (target==null||source==null)return;
            Limb limb = msg.limb;
            InteractionHand hand = msg.offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            ItemStack stack = source.getItemInHand(hand);
            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(health->{
                boolean used = health.tryUseItem(limb,stack,source,target,hand);
                if (used){
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

    public static ItemStack manualHurt(ItemStack stack, int amount) {
        if (stack == null || stack.isEmpty() || amount <= 0 || !stack.isDamageableItem()) {
            return stack;
        }

        int newDamage = stack.getDamageValue() + amount;
        int max = stack.getMaxDamage();

        if (newDamage >= max) {
            // broken -> return an empty stack so the caller can replace it
            return ItemStack.EMPTY;
        }

        // update damage value on the existing ItemStack
        stack.setDamageValue(newDamage);
        return stack;
    }
}
