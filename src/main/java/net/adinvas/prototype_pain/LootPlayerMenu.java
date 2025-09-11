package net.adinvas.prototype_pain;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class LootPlayerMenu extends AbstractContainerMenu {
    private final Player targetPlayer;

    protected LootPlayerMenu(int pContainerId, Inventory actorInventory, Player target) {
        super(ModMenus.LOOT_PLAYER.get(), pContainerId);
        this.targetPlayer = target;
        Inventory targetInv = target.getInventory();
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(targetInv, col + row * 9 + 9, 8 + col * 18, 18 + row * 18));
            }
        }
        // hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(targetInv, col, 8 + col * 18, 76 ));
        }

        // actor’s inventory slots
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(actorInventory, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(actorInventory, col, 8 + col * 18, 162));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            int targetSize = 36; // 9 hotbar + 27 main slots
            int actorStart = targetSize;
            int actorEnd = actorStart + targetSize;

            if (index < actorStart) {
                // slot belongs to target's inventory
                if (!this.moveItemStackTo(stackInSlot, actorStart, actorEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // slot belongs to actor's inventory
                if (!this.moveItemStackTo(stackInSlot, 0, targetSize, false)) {
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
        float distance = player.distanceTo(targetPlayer);
        if (distance>3){
            return false;
        }
        return true;
    }
}
