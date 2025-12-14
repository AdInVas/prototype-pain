package net.adinvas.prototype_pain.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.ModFluids;
import net.adinvas.prototype_pain.recipe.ingridients.FluidIngredient;
import net.adinvas.prototype_pain.recipe.ingridients.ItemIngredient;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MedicalMixerRecipeBuilder {

    private final List<ItemIngredient> itemInputs = new ArrayList<>();
    private final List<FluidIngredient> fluidInputs = new ArrayList<>();
    private final List<ItemStack> itemOutputs = new ArrayList<>();
    private final List<FluidStack> fluidOutputs = new ArrayList<>();
    private int processingTime = 100;

    public static MedicalMixerRecipeBuilder mixer() {
        return new MedicalMixerRecipeBuilder();
    }

    /* ------------------- INPUTS ------------------- */

    public MedicalMixerRecipeBuilder input(ItemIngredient ingredient) {
        this.itemInputs.add(ingredient);
        return this;
    }

    public MedicalMixerRecipeBuilder input(ItemStack stack) {
        this.itemInputs.add(new ItemIngredient(stack, stack.getCount()));
        return this;
    }

    public MedicalMixerRecipeBuilder input(Item item, int count) {
        this.itemInputs.add(new ItemIngredient(new ItemStack(item), count));
        return this;
    }

    public MedicalMixerRecipeBuilder input(Ingredient ingredient, int count) {
        this.itemInputs.add(new ItemIngredient(ingredient.getItems()[0], count));
        return this;
    }

    public MedicalMixerRecipeBuilder input(FluidIngredient ingredient) {
        this.fluidInputs.add(ingredient);
        return this;
    }

    public MedicalMixerRecipeBuilder input(FluidStack fluidStack) {
        this.fluidInputs.add(new FluidIngredient(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getTag()));
        return this;
    }

    public MedicalMixerRecipeBuilder input(MedicalFluid fluid, int amount) {
        FluidStack stack = new FluidStack(ModFluids.SRC_MEDICAL.get(),amount);
        stack.getOrCreateTag().putString("MedicalId",fluid.getRegistryId().toString());
        this.fluidInputs.add(new FluidIngredient(stack.getFluid(), stack.getAmount(),stack.getTag()));
        return this;
    }

    public MedicalMixerRecipeBuilder input(TagKey<Item> tagKey, int amount){
        this.itemInputs.add(new ItemIngredient(tagKey,amount));
        return this;
    }
    public MedicalMixerRecipeBuilder inputF(TagKey<Fluid> tagKey, int amount){
        this.fluidInputs.add(new FluidIngredient(tagKey,amount,null));
        return this;
    }
    public MedicalMixerRecipeBuilder inputM(TagKey<MedicalFluid> tagKey,int amount){
        this.fluidInputs.add(new FluidIngredient(tagKey,amount));
        return this;
    }


    public MedicalMixerRecipeBuilder input(net.minecraft.world.level.material.Fluid fluid, int amount) {
        this.fluidInputs.add(new FluidIngredient(fluid, amount, null));
        return this;
    }

    /* ------------------- OUTPUTS ------------------- */

    public MedicalMixerRecipeBuilder output(ItemStack stack) {
        this.itemOutputs.add(stack);
        return this;
    }

    public MedicalMixerRecipeBuilder output(Item item, int count) {
        this.itemOutputs.add(new ItemStack(item, count));
        return this;
    }

    public MedicalMixerRecipeBuilder output(FluidStack stack) {
        this.fluidOutputs.add(stack);
        return this;
    }

    public MedicalMixerRecipeBuilder output(MedicalFluid fluid, int amount) {
        FluidStack stack = new FluidStack(ModFluids.SRC_MEDICAL.get(),amount);
        stack.getOrCreateTag().putString("MedicalId",fluid.getRegistryId().toString());
        this.fluidOutputs.add(stack);
        return this;
    }

    /* ------------------- PROCESSING TIME ------------------- */

    public MedicalMixerRecipeBuilder processingTime(int ticks) {
        this.processingTime = ticks;
        return this;
    }

    /* ------------------- SAVE ------------------- */

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject json) {
                // Item inputs
                JsonArray itemInputArray = new JsonArray();
                for (ItemIngredient ing : itemInputs) {
                    itemInputArray.add(ing.toJson());
                }
                json.add("item_inputs", itemInputArray);

                // Fluid inputs
                JsonArray fluidInputArray = new JsonArray();
                for (FluidIngredient ing : fluidInputs) {
                    fluidInputArray.add(ing.toJson());
                }
                json.add("fluid_inputs", fluidInputArray);

                // Item outputs
                JsonArray itemOutputArray = new JsonArray();
                for (ItemStack stack : itemOutputs) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
                    obj.addProperty("count", stack.getCount());
                    itemOutputArray.add(obj);
                }
                json.add("item_outputs", itemOutputArray);

                // Fluid outputs
                JsonArray fluidOutputArray = new JsonArray();
                for (FluidStack stack : fluidOutputs) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
                    obj.addProperty("amount", stack.getAmount());
                    if (stack.hasTag()) {
                        obj.addProperty("nbt", stack.getTag().copy().getAsString());
                    }
                    fluidOutputArray.add(obj);
                }
                json.add("fluid_outputs", fluidOutputArray);

                json.addProperty("processingTime", processingTime);
            }

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return ModRecipes.MEDICAL_MIXER_RECIPE_SERIALIZER.get();
            }

            @Nullable
            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId() {
                return null;
            }
        });
    }
}
