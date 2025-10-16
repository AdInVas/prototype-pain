package net.adinvas.prototype_pain.recipe;

import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;

public class MedicalIngridient {
    public final Ingredient ingredient;
    public final boolean isFluidContainer;
    public final boolean consume;
    @Nullable public final String fluidId;
    @Nullable public final String fluidTag;
    public final float fluidAmount;
    public final boolean copy;

    public MedicalIngridient(Ingredient ingredient, boolean isFluidContainer, boolean consume, String fluidId, @Nullable String fluidTag, float fluidAmount, boolean copy) {
        this.ingredient = ingredient;
        this.isFluidContainer = isFluidContainer;
        this.consume = consume;
        this.fluidId = fluidId;
        this.fluidTag = fluidTag;
        this.fluidAmount = fluidAmount;
        this.copy = copy;
    }
}
