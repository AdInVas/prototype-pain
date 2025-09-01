package net.adinvas.prototype_pain.item.bags.small;

import net.adinvas.prototype_pain.ModMenus;
import net.adinvas.prototype_pain.item.bags.MedicalSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SmallMedibagMenu extends AbstractContainerMenu {
    private final ItemStack bagStack;
    private final Container medibagInventory = new SimpleContainer(4);

    public SmallMedibagMenu(int id, Inventory playerInv,ItemStack bagStack){
        super(ModMenus.SMALL_MEDIBAG.get(),id);
        this.bagStack = bagStack;

        loadConfigFromItem(bagStack);

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                this.addSlot(new MedicalSlot(medibagInventory, col + row * 2, 72 + col * 18, 24 + row * 18));
            }
        }

        // Player inventory
        int startY = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, startY + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, startY + 58));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            int containerSlots = medibagInventory.getContainerSize(); // number of chest slots
            int totalSlots = this.slots.size();

            if (i < containerSlots) {
                // Moving from chest -> player inventory
                if (!this.moveItemStackTo(stackInSlot, containerSlots, totalSlots, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory -> chest
                if (!this.moveItemStackTo(stackInSlot, 0, containerSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    private void saveConfigToItem() {
        CompoundTag rootTag = bagStack.getOrCreateTag();
        CompoundTag configTag = new CompoundTag();

        for (int i = 0; i < medibagInventory.getContainerSize(); i++) {
            ItemStack stack = medibagInventory.getItem(i);
            if (!stack.isEmpty()) {
                configTag.put("Slot"+i, stack.save(new CompoundTag()));
            }
        }

        rootTag.put("StoredItems", configTag);
    }

    private void loadConfigFromItem(ItemStack droneStack) {
        CompoundTag rootTag = droneStack.getTag();
        if (rootTag == null || !rootTag.contains("StoredItems", Tag.TAG_COMPOUND)) return;

        CompoundTag configTag = rootTag.getCompound("StoredItems");
        for (int i = 0; i < medibagInventory.getContainerSize(); i++) {
            if (configTag.contains("Slot"+i, Tag.TAG_COMPOUND)) {
                ItemStack loaded = ItemStack.of(configTag.getCompound("Slot"+i));
                medibagInventory.setItem(i, loaded);
            }
        }
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        saveConfigToItem();
    }
}
