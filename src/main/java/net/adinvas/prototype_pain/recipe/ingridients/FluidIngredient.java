package net.adinvas.prototype_pain.recipe.ingridients;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class FluidIngredient {
    private final Fluid fluid;
    private final int amount;
    private final @Nullable CompoundTag nbt;

    public FluidIngredient(Fluid fluid, int amount, @Nullable CompoundTag nbt) {
        this.fluid = fluid;
        this.amount = amount;
        this.nbt = nbt;
    }

    public boolean matches(FluidStack stack) {
        if (stack.isEmpty()) return false;

        if (!stack.getFluid().isSame(this.fluid)) return false;

        if (stack.getAmount() < this.amount) return false;

        if (this.nbt != null) {
            if (!stack.hasTag()) return false;
            return this.nbt.equals(stack.getTag());
        }

        return true;
    }
}
