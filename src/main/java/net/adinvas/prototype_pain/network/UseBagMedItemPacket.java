package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.item.IBag;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class UseBagMedItemPacket {
    private final ItemStack itemstack;
    private final ItemStack bagstack;
    private final int slot;
    private final Limb limb ;
    private final UUID target;
    private final boolean offHand;

    public UseBagMedItemPacket(ItemStack itemstack, ItemStack bagstack, Limb limb, UUID target, boolean offHand,int slot){
        this.itemstack=itemstack;
        this.bagstack = bagstack;
        this.limb=limb;
        this.target=target;
        this.offHand = offHand;
        this.slot = slot;
    }

    public UseBagMedItemPacket(FriendlyByteBuf buf){
        this.itemstack = buf.readItem();
        this.limb = Limb.valueOf(buf.readUtf());
        this.target = buf.readUUID();
        this.offHand = buf.readBoolean();
        this.bagstack = buf.readItem();
        this.slot = buf.readInt();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeItem(itemstack);
        buf.writeUtf(limb.name());
        buf.writeUUID(target);
        buf.writeBoolean(offHand);
        buf.writeItem(bagstack);
        buf.writeInt(slot);
    }

    public static void handle(UseBagMedItemPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer source = ctx.get().getSender();
            if (source == null) return;

            ServerPlayer targetPlayer = source.getServer().getPlayerList().getPlayer(msg.target);
            if (targetPlayer == null) return;

            // We don’t have a specific hand (it’s inside a bag),
            // but we can still treat it as “internal use”:
            ItemStack itemstack = msg.itemstack.copy();

            // Verify the bag is in the player’s hands before allowing this

            targetPlayer.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(health -> {
                // Perform the same logic as tryUseItem, but no hand reference
                ItemStack used = health.tryUseItem(msg.limb, itemstack, source, targetPlayer);
                if (msg.bagstack.getItem() instanceof IBag iBag) {
                    List<ItemStack> items = iBag.getItems(msg.bagstack);

                    // Find the used item and remove/damage it
                    items.set(msg.slot,used);

                    // Save the updated inventory back to the bag
                    iBag.setItems(msg.bagstack, items);

                    // Update the bag in the player hand
                   source.setItemInHand(msg.offHand?InteractionHand.OFF_HAND:InteractionHand.MAIN_HAND, msg.bagstack);
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
