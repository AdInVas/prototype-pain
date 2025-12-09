package net.adinvas.prototype_pain.fluid_system.n;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class MultiFluidTank {
    private final List<FluidStack> fluids = new ArrayList<>();
    private final int capacity;

    public MultiFluidTank(int capacity) {
        this.capacity = capacity;
    }

    public List<FluidStack> getFluids() {
        return fluids;
    }

    public int getTotalFluid() {
        return fluids.stream().mapToInt(FluidStack::getAmount).sum();
    }

    public int getFreeSpace() {
        return capacity - getTotalFluid();
    }

    // Normal fill
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty()) return 0;

        int free = getFreeSpace();
        if (free <= 0) return 0;

        int fill = Math.min(free, resource.getAmount());

        // Try merge first
        for (FluidStack existing : fluids) {
            if (existing.isFluidEqual(resource)) {
                if (action.execute()) {
                    existing.grow(fill);
                }
                return fill;
            }
        }

        // Otherwise add new entry
        if (action.execute()) {
            FluidStack copy = resource.copy();
            copy.setAmount(fill);
            fluids.add(copy);
        }

        return fill;
    }

    // Normal drain by type
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        for (FluidStack existing : fluids) {
            if (existing.isFluidEqual(resource)) {
                int drained = Math.min(existing.getAmount(), resource.getAmount());
                FluidStack out = new FluidStack(existing.getFluid(), drained);

                if (action.execute()) {
                    existing.shrink(drained);
                    if (existing.isEmpty()) fluids.remove(existing);
                }

                return out;
            }
        }
        return FluidStack.EMPTY;
    }
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (fluids.isEmpty() || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }

        // The topmost fluid is always index 0
        FluidStack existing = fluids.get(0);

        int drainedAmt = Math.min(existing.getAmount(), maxDrain);
        FluidStack out = new FluidStack(existing.getFluid(), drainedAmt);

        if (drainedAmt > 0 && action.execute()) {
            existing.shrink(drainedAmt);
            if (existing.isEmpty()) {
                fluids.remove(0);
            }
        }

        return out;
    }

    // --- NEW METHOD ---------------------------------------------------
    // Drain total amount distributed proportionally across all fluids
    public List<FluidStack> drainProportional(int totalDrain, IFluidHandler.FluidAction action) {
        int totalCurrent = getTotalFluid();
        if (totalCurrent == 0 || totalDrain <= 0) return List.of();

        totalDrain = Math.min(totalDrain, totalCurrent);

        List<FluidStack> result = new ArrayList<>();

        for (FluidStack existing : new ArrayList<>(fluids)) {
            double ratio = (double) existing.getAmount() / totalCurrent;
            int drain = (int) Math.floor(totalDrain * ratio);

            // Avoid rounding to zero for small amounts
            drain = Math.min(drain, existing.getAmount());
            if (drain <= 0) continue;

            FluidStack out = new FluidStack(existing.getFluid(), drain);
            result.add(out);

            if (action.execute()) {
                existing.shrink(drain);
                if (existing.isEmpty())
                    fluids.remove(existing);
            }
        }

        return result;
    }

    public int getCapacity() {
        return capacity;
    }
}
