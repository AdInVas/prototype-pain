package net.adinvas.prototype_pain.item.bags;

import net.adinvas.prototype_pain.item.ISimpleMed;
import net.adinvas.prototype_pain.item.INarcoticUsable;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MedicalSlot extends Slot {
    public MedicalSlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return pStack.getItem() instanceof ISimpleMed ||pStack.getItem() instanceof INarcoticUsable;
    }
}
