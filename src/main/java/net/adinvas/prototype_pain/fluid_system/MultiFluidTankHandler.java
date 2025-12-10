package net.adinvas.prototype_pain.fluid_system;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.List;

public class MultiFluidTankHandler implements IFluidHandlerItem {
    private final ItemStack container;
    private final MultiFluidTank tank;

    public MultiFluidTankHandler(ItemStack container, int capacity) {
        this.container = container;
        this.tank = new MultiFluidTank(capacity);

        // Load from NBT if present
        loadFromNBT();
    }

    public MultiFluidTank getTank() {
        return tank;
    }

    @Override
    public ItemStack getContainer() {
        return container;
    }

    @Override
    public int getTanks() {
        return 1; // your item exposes ONE TANK with multiple internal fluids
    }

    @Override
    public FluidStack getFluidInTank(int tankId) {
        // 🔥 Return the "topmost" fluid (the first entry)
        if (!tank.getFluids().isEmpty()) {
            return tank.getFluids().get(0);
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.tank.getCapacity(); // total shared capacity
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true; // allow any fluid
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int filled = tank.fill(resource, action);
        if (filled > 0 && action.execute()) saveToNBT();
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack out = tank.drain(resource, action);
        if (!out.isEmpty() && action.execute()) saveToNBT();
        return out;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack out = tank.drain(maxDrain, action);
        if (!out.isEmpty() && action.execute()) saveToNBT();
        return out;
    }

    void saveToNBT() {
        CompoundTag rootTag = container.getOrCreateTag();
        CompoundTag tankTag = new CompoundTag();
        ListTag list = new ListTag();

        for (FluidStack fs : tank.getFluids()) {
            CompoundTag t = new CompoundTag();
            fs.writeToNBT(t);
            list.add(t);
        }

        tankTag.put("Fluids", list);
        tankTag.putInt("Capacity", tank.getCapacity());

        rootTag.put("MultiFluidTank", tankTag);
    }

    private void loadFromNBT() {
        CompoundTag tag = container.getTagElement("MultiFluidTank");
        if (tag == null) return;

        ListTag list = tag.getList("Fluids", Tag.TAG_COMPOUND);
        tank.getFluids().clear();

        for (Tag element : list) {
            CompoundTag fsTag = (CompoundTag) element;
            FluidStack stack = FluidStack.loadFluidStackFromNBT(fsTag);
            if (!stack.isEmpty()) {
                tank.getFluids().add(stack);
            }
        }
    }

    public List<FluidStack> drainProportional(int totalDrain, FluidAction action) {
        List<FluidStack> drained = tank.drainProportional(totalDrain, action);
        if (!drained.isEmpty() && action.execute()) saveToNBT();
        return drained;
    }
}
