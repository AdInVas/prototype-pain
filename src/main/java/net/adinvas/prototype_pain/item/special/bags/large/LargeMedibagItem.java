package net.adinvas.prototype_pain.item.special.bags.large;

import net.adinvas.prototype_pain.item.IBag;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LargeMedibagItem extends Item implements IBag {

    public LargeMedibagItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        pPlayer.level().playSound(null,pPlayer.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS,1,1);
        if (!pLevel.isClientSide()) {
            NetworkHooks.openScreen((ServerPlayer) pPlayer,
                    new SimpleMenuProvider(
                            (id, inv, ply) -> new LargeMedibagMenu(id, inv, itemStack),
                            Component.literal("Large Medibag")
                    ),
                    buf -> buf.writeBoolean(pUsedHand == InteractionHand.MAIN_HAND)
            );
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.large_medibag.discription").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public List<ItemStack> getItems(ItemStack bagStack) {
        List<ItemStack> items = new ArrayList<>();

        if (!bagStack.hasTag()) return items;
        CompoundTag rootTag = bagStack.getTag();
        if (rootTag == null || !rootTag.contains("StoredItems", Tag.TAG_COMPOUND)) return items;

        CompoundTag configTag = rootTag.getCompound("StoredItems");

        // --- Determine how many slots this bag has ---
        // You can replace this with a fixed number or a method call (e.g. getSlotCount())
        int slotCount = 12; // or hardcode: int slotCount = 12;

        // --- Read in order ---
        for (int i = 0; i < slotCount; i++) {
            String key = "Slot" + i;
            ItemStack stack = ItemStack.EMPTY;
            if (configTag.contains(key, Tag.TAG_COMPOUND)) {
                CompoundTag slotTag = configTag.getCompound(key);
                stack = ItemStack.of(slotTag);
            }
            items.add(stack);
        }

        return items;
    }

    @Override
    public void setItems(ItemStack bagStack, List<ItemStack> items) {
        if (bagStack == null || items == null) return;

        CompoundTag rootTag = bagStack.getOrCreateTag();
        CompoundTag configTag = new CompoundTag();

        // --- Always write all slots, even if empty ---
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            CompoundTag stackTag = new CompoundTag();

            if (!stack.isEmpty()) {
                stack.save(stackTag);
            }

            configTag.put("Slot" + i, stackTag);
        }

        rootTag.put("StoredItems", configTag);
        bagStack.setTag(rootTag);
    }
}
