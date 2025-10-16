package net.adinvas.prototype_pain.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.adinvas.prototype_pain.item.IMedicalFluidContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShapelessWithMedicalContainerRecipe implements CraftingRecipe {
    private final ResourceLocation id;
    final CraftingBookCategory category;
    final NonNullList<MedicalIngridient> ingredients;
    final ItemStack result;

    public String resultFluidId;
    public float resultFluidAmount;

    public ShapelessWithMedicalContainerRecipe(ResourceLocation id, CraftingBookCategory category, NonNullList<MedicalIngridient> ingredients1, ItemStack result) {
        this.id = id;
        this.category = category;
        this.ingredients = ingredients1;
        this.result = result;
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack stack = craftingContainer.getItem(i);
            if (!stack.isEmpty()) inputs.add(stack);
        }

        // Early fail if not enough items
        if (inputs.size() != ingredients.size()) return false;

        // Basic vanilla shapeless check
        if (RecipeMatcher.findMatches(inputs, ingredients.stream().map(mi -> mi.ingredient).toList()) == null)
            return false;

        // Copy of remaining inputs to keep track of which ones were used
        List<ItemStack> remaining = new ArrayList<>(inputs);

        // For each ingredient, find a matching stack and check fluid conditions
        for (MedicalIngridient mi : ingredients) {
            ItemStack matchStack = null;

            for (ItemStack stack : remaining) {
                if (mi.ingredient.test(stack)) {
                    matchStack = stack;
                    break;
                }
            }

            if (matchStack == null) return false; // ingredient not found
            remaining.remove(matchStack);

            // --- Fluid checks ---
            if (mi.isFluidContainer && matchStack.getItem() instanceof IMedicalFluidContainer container) {
                if (mi.fluidId != null && mi.fluidAmount != 0) {
                    MedicalFluid fluid = MedicalFluids.get(mi.fluidId);
                    float currAmount = container.getAmountOfFluid(matchStack, fluid);

                    if (mi.fluidAmount < 0 && currAmount < -mi.fluidAmount) {
                        return false; // Not enough fluid to drain
                    }
                    if (mi.fluidAmount > 0 && currAmount + mi.fluidAmount > container.getCapacity(matchStack)) {
                        return false; // Would overflow
                    }
                } else if (mi.fluidTag != null && mi.fluidAmount != 0) {
                    boolean matched = false;
                    for (MedicalFluid f : MedicalFluids.getTag(mi.fluidTag)) {
                        float currAmount = container.getAmountOfFluid(matchStack, f);
                        if (mi.fluidAmount < 0 && currAmount >= Math.abs(mi.fluidAmount)) {
                            matched = true;
                            break;
                        }
                        if (mi.fluidAmount > 0 && currAmount + mi.fluidAmount <= container.getCapacity(matchStack)) {
                            matched = true;
                            break;
                        }
                    }
                    if (!matched) return false;
                }
            }
        }

        return true;
    }

    public NonNullList<MedicalIngridient> getMedIngredients() {
        return ingredients;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public static class Type implements RecipeType<ShapelessWithMedicalContainerRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "shapeless_with_medical_container";
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> remainings = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        List<MedicalIngridient> pending = new ArrayList<>(ingredients);

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;

            // Find matching ingredient
            MedicalIngridient match = null;
            for (MedicalIngridient mi : pending) {
                if (mi.ingredient.test(stack)) {
                    match = mi;
                    pending.remove(mi);
                    break;
                }
            }
            if (match == null) continue;

            ItemStack copy = stack.copy();

            // Handle fluids if applicable
            if (match.isFluidContainer && copy.getItem() instanceof IMedicalFluidContainer container) {
                // Fluid ID
                if (match.fluidId != null) {
                    MedicalFluid fluid = MedicalFluids.get(match.fluidId);
                    if (match.fluidAmount < 0) container.removeFluid(copy, -match.fluidAmount, fluid);
                    else container.addFluid(copy, match.fluidAmount, fluid);
                }
                // Fluid Tag
                else if (match.fluidTag != null) {
                    for (MedicalFluid fluid : MedicalFluids.getTag(match.fluidTag)) {
                        float amount = container.getAmountOfFluid(copy, fluid);
                        if (match.fluidAmount < 0 && amount >= -match.fluidAmount) {
                            container.removeFluid(copy, -match.fluidAmount, fluid);
                            break;
                        } else if (match.fluidAmount > 0 && amount + match.fluidAmount <= container.getCapacity(copy)) {
                            container.addFluid(copy, match.fluidAmount, fluid);
                            break;
                        }
                    }
                }
            }

            remainings.set(i, match.consume ? ItemStack.EMPTY : copy);
        }

        return remainings;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        ItemStack out = result.copy();

        // Find first ingredient with Copy = true
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;

            MedicalIngridient mi = null;
            for (MedicalIngridient ing : ingredients) {
                if (ing.ingredient.test(stack) && ing.copy) {
                    mi = ing;
                    break;
                }
            }

            if (mi != null && stack.getItem() instanceof IMedicalFluidContainer container) {
                ItemStack copy = stack.copy();

                // Apply fluid drains/adds first
                if (mi.fluidId != null) {
                    MedicalFluid fluid = MedicalFluids.get(mi.fluidId);
                    if (mi.fluidAmount < 0) container.removeFluid(copy, -mi.fluidAmount, fluid);
                    else container.addFluid(copy, mi.fluidAmount, fluid);
                } else if (mi.fluidTag != null) {
                    for (MedicalFluid fluid : MedicalFluids.getTag(mi.fluidTag)) {
                        float amount = container.getAmountOfFluid(copy, fluid);
                        if (mi.fluidAmount < 0 && amount >= -mi.fluidAmount) {
                            container.removeFluid(copy, -mi.fluidAmount, fluid);
                            break;
                        } else if (mi.fluidAmount > 0 && amount + mi.fluidAmount <= container.getCapacity(copy)) {
                            container.addFluid(copy, mi.fluidAmount, fluid);
                            break;
                        }
                    }
                }

                // Copy NBT to result after drains
                out.setTag(copy.getTag() != null ? copy.getTag().copy() : null);
                break; // only use first Copy=true ingredient
            }
        }

        // Apply result fluid modifications if needed
        if (out.getItem() instanceof IMedicalFluidContainer container && resultFluidId != null) {
            MedicalFluid fluid = MedicalFluids.get(resultFluidId);
            float filled = container.getFilledTotal(out) + resultFluidAmount;

            if (filled > container.getCapacity(out)) return ItemStack.EMPTY;
            container.addFluid(out, resultFluidAmount, fluid);
        }

        return out;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return i * i1 >= this.ingredients.size();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SHAPELESS_FUN_SER.get();
    }


    public static class Serializer implements RecipeSerializer<ShapelessWithMedicalContainerRecipe> {
        public Serializer() {
        }

        @Override
        public ShapelessWithMedicalContainerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            // CraftingBookCategory
            String categoryName = GsonHelper.getAsString(pJson, "category", "misc");
            CraftingBookCategory category = CraftingBookCategory.CODEC.byName(categoryName, CraftingBookCategory.MISC);

            // Ingredients
            JsonArray ingArray = GsonHelper.getAsJsonArray(pJson, "ingredients");
            NonNullList<MedicalIngridient> ingredients = NonNullList.create();
            for (JsonElement el : ingArray) {
                JsonObject obj = el.getAsJsonObject();
                Ingredient ing = Ingredient.fromJson(obj);

                boolean isFluidContainer = GsonHelper.getAsBoolean(obj, "isFluidContainer", false);
                boolean consume = GsonHelper.getAsBoolean(obj, "consume", true);
                String fluidId = null;
                String fluidTag = null;
                float fluidAmount = 0f;

                boolean copy = GsonHelper.getAsBoolean(obj, "copy", false);
                if (obj.has("fluid")) {
                    JsonObject f = obj.getAsJsonObject("fluid");
                    if (f.has("id")) fluidId = GsonHelper.getAsString(f, "id");
                    if (f.has("tag")) fluidTag = GsonHelper.getAsString(f, "tag");
                    fluidAmount = GsonHelper.getAsFloat(f, "amount");
                }

                ingredients.add(new MedicalIngridient(ing, isFluidContainer, consume, fluidId,fluidTag, fluidAmount,copy));
            }

            // Result item
            JsonObject resultObj = GsonHelper.getAsJsonObject(pJson, "result");
            ItemStack result = ShapedRecipe.itemStackFromJson(resultObj);

            // Optional result fluid
            String resultFluidId = null;
            float resultFluidAmount = 0f;
            if (resultObj.has("fluid")) {
                JsonObject f = resultObj.getAsJsonObject("fluid");
                resultFluidId = GsonHelper.getAsString(f, "id");
                resultFluidAmount = GsonHelper.getAsFloat(f, "amount");
            }

            // Build the recipe instance
            ShapelessWithMedicalContainerRecipe recipe = new ShapelessWithMedicalContainerRecipe(
                    pRecipeId,
                    category,
                    ingredients,
                    result
            );

            // Store result fluid info in the recipe (add fields to class)
            recipe.resultFluidId = resultFluidId;
            recipe.resultFluidAmount = resultFluidAmount;

            return recipe;
        }

        @Override
        public ShapelessWithMedicalContainerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            int size = buf.readVarInt();
            NonNullList<MedicalIngridient> ingredients = NonNullList.create();

            for (int i = 0; i < size; i++) {
                Ingredient ing = Ingredient.fromNetwork(buf);
                boolean isFluidContainer = buf.readBoolean();
                boolean consume = buf.readBoolean();
                boolean copy = buf.readBoolean();

                boolean hasFluid = buf.readBoolean();
                String fluidId = null;
                String fluidTag = null;
                float fluidAmount = 0f;
                if (hasFluid) {
                    boolean hasId = buf.readBoolean();
                    boolean hasTag = buf.readBoolean();
                    if (hasId) fluidId = buf.readUtf();
                    if (hasTag) fluidTag = buf.readUtf();
                    fluidAmount = buf.readFloat();
                }

                ingredients.add(new MedicalIngridient(ing, isFluidContainer, consume, fluidId, fluidTag, fluidAmount, copy));
            }

            ItemStack result = buf.readItem();
            boolean hasResultFluid = buf.readBoolean();
            String resultFluidId = null;
            float resultFluidAmount = 0f;
            if (hasResultFluid) {
                resultFluidId = buf.readUtf();
                resultFluidAmount = buf.readFloat();
            }

            ShapelessWithMedicalContainerRecipe recipe =
                    new ShapelessWithMedicalContainerRecipe(id, category, ingredients, result);
            recipe.resultFluidId = resultFluidId;
            recipe.resultFluidAmount = resultFluidAmount;
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ShapelessWithMedicalContainerRecipe recipe) {
            buf.writeEnum(recipe.category);
            buf.writeVarInt(recipe.ingredients.size());

            for (MedicalIngridient mi : recipe.ingredients) {
                mi.ingredient.toNetwork(buf);
                buf.writeBoolean(mi.isFluidContainer);
                buf.writeBoolean(mi.consume);
                buf.writeBoolean(mi.copy);

                boolean hasFluid = mi.fluidId != null || mi.fluidTag != null;
                buf.writeBoolean(hasFluid);
                if (hasFluid) {
                    buf.writeBoolean(mi.fluidId != null);
                    buf.writeBoolean(mi.fluidTag != null);
                    if (mi.fluidId != null) buf.writeUtf(mi.fluidId);
                    if (mi.fluidTag != null) buf.writeUtf(mi.fluidTag);
                    buf.writeFloat(mi.fluidAmount);
                }
            }

            buf.writeItem(recipe.result);

            boolean hasResultFluid = recipe.resultFluidId != null;
            buf.writeBoolean(hasResultFluid);
            if (hasResultFluid) {
                buf.writeUtf(recipe.resultFluidId);
                buf.writeFloat(recipe.resultFluidAmount);
            }
        }
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }
}
