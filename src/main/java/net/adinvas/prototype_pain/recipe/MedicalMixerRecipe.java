package net.adinvas.prototype_pain.recipe;

import net.adinvas.prototype_pain.blocks.medical_mixer.MedicalMixerBlockEntity;
import net.adinvas.prototype_pain.recipe.ingridients.FluidIngredient;
import net.adinvas.prototype_pain.recipe.ingridients.ItemIngredient;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class MedicalMixerRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;

    private final List<ItemIngredient> itemInputs;
    private final List<FluidIngredient> fluidInputs;

    private final List<ItemStack> itemOutputs;
    private final List<FluidStack> fluidOutputs;

    private final int processingTime;

    public MedicalMixerRecipe(ResourceLocation id, List<ItemIngredient> itemInputs, List<FluidIngredient> fluidInputs, List<ItemStack> itemOutputs, List<FluidStack> fluidOutputs, int processingTime) {
        this.id = id;
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
        this.processingTime = processingTime;
    }

    @Override
    public boolean matches(SimpleContainer simpleContainer, Level level) {
        if (level.isClientSide) return false;

        for (ItemIngredient ingredient : itemInputs) {
            if (!ingredient.matches(simpleContainer, 0, 4)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(SimpleContainer simpleContainer, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MEDICAL_MIXER_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MEDICAL_MIXER_RECIPE.get();
    }

    public List<ItemIngredient> getItemInputs() {
        return itemInputs;
    }

    public List<FluidIngredient> getFluidInputs() {
        return fluidInputs;
    }

    public List<ItemStack> getItemOutputs() {
        return itemOutputs;
    }

    public List<FluidStack> getFluidOutputs() {
        return fluidOutputs;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public boolean matches(IItemHandler currentItems, List<FluidStack> currentFluids){
        for (ItemIngredient ingredient : itemInputs) {
            if (!ingredient.matches(currentItems, 0, 4)) {
                return false;
            }
        }
        for (FluidIngredient ingredient : fluidInputs){
            boolean matches = false;
            for (FluidStack fs : currentFluids){
                if (ingredient.matches(fs)){
                    matches = true;
                    break;
                }
            }
            if (!matches)return false;
        }
        return true;
    }
}
