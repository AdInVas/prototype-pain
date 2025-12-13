package net.adinvas.prototype_pain.recipe.ingridients;

import net.minecraft.world.item.crafting.Ingredient;

public class ItemIngredient {
    private final Ingredient ingredient;
    private final int count;

    public ItemIngredient(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }


    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getCount() {
        return count;
    }


    public boolean matches
}
