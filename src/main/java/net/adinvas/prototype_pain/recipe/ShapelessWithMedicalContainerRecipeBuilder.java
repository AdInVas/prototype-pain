package net.adinvas.prototype_pain.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import javax.xml.transform.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ShapelessWithMedicalContainerRecipeBuilder implements RecipeBuilder {
    private final ItemStack result;
    private final CraftingBookCategory category;
    private final List<MedicalIngredientEntry> ingredients = new ArrayList<>();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private @Nullable String resultFluidId;
    private float resultFluidAmount = 0;

    public ShapelessWithMedicalContainerRecipeBuilder(@Nullable ItemLike result) {
        this.result = result != null ? new ItemStack(result) : ItemStack.EMPTY;
        this.category = CraftingBookCategory.MISC;
    }

    public static ShapelessWithMedicalContainerRecipeBuilder shapeless(@Nullable ItemLike result) {
        return new ShapelessWithMedicalContainerRecipeBuilder(result);
    }

    public ShapelessWithMedicalContainerRecipeBuilder requires(Ingredient ingredient) {
        ingredients.add(new MedicalIngredientEntry(ingredient));
        return this;
    }

    public ShapelessWithMedicalContainerRecipeBuilder requires(ItemLike item) {
        return requires(Ingredient.of(item));
    }

    public ShapelessWithMedicalContainerRecipeBuilder requires(ItemLike item, boolean isFluidContainer, boolean consume) {
        return requires(Ingredient.of(item), isFluidContainer, consume, null, null, 0, false);
    }

    public ShapelessWithMedicalContainerRecipeBuilder requires(Ingredient ingredient, boolean isFluidContainer, boolean consume,
                                                               @Nullable String fluidId, @Nullable String fluidTag,
                                                               float fluidAmount, boolean copy) {
        ingredients.add(new MedicalIngredientEntry(ingredient, isFluidContainer, consume, fluidId, fluidTag, fluidAmount, copy));
        return this;
    }

    public ShapelessWithMedicalContainerRecipeBuilder resultFluid(String id, float amount) {
        this.resultFluidId = id;
        this.resultFluidAmount = amount;
        return this;
    }

    public ShapelessWithMedicalContainerRecipeBuilder requiresFluidTag(ItemLike item, String fluidTag, float amount, boolean consume, boolean copy) {
        return requires(Ingredient.of(item), true, consume, null, fluidTag, amount, copy);
    }

    // 1. Require using a TagKey<Item>
    public ShapelessWithMedicalContainerRecipeBuilder requires(TagKey<Item> tag) {
        return requires(Ingredient.of(tag));
    }

    // 2. Require using a TagKey<Item> with custom flags
    public ShapelessWithMedicalContainerRecipeBuilder requires(TagKey<Item> tag, boolean isFluidContainer, boolean consume, boolean copy) {
        return requires(Ingredient.of(tag), isFluidContainer, consume, null, null, 0, copy);
    }

    // 3. Convenience method for a string tag ID like "forge:nuggets/iron"
    public ShapelessWithMedicalContainerRecipeBuilder requiresTag(String tagId) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, new ResourceLocation(tagId));
        return requires(tag);
    }

    // 4. Full control variant (string tagId + flags)
    public ShapelessWithMedicalContainerRecipeBuilder requiresTag(String tagId, boolean isFluidContainer, boolean consume, boolean copy) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, new ResourceLocation(tagId));
        return requires(tag, isFluidContainer, consume, copy);
    }

    public ShapelessWithMedicalContainerRecipeBuilder requiresFluid(ItemLike item, String fluidId, float amount, boolean isFluidContainer, boolean consume, boolean copy) {
        return requires(Ingredient.of(item), isFluidContainer, consume, fluidId, null, amount, copy);
    }

    // Convenience: item + fluid (common case)
    public ShapelessWithMedicalContainerRecipeBuilder requiresFluid(ItemLike item, String fluidId, float amount) {
        return requiresFluid(item, fluidId, amount, true, true, false);
    }

    // Convenience: set fluid for the last added ingredient by id
    public ShapelessWithMedicalContainerRecipeBuilder requiresFluid(String fluidId, float amount) {
        if (ingredients.isEmpty()) throw new IllegalStateException("No ingredient to attach fluid to. Call requires(...) first.");
        MedicalIngredientEntry last = ingredients.get(ingredients.size() - 1);
        // replace last entry with same properties but with fluid id/amount
        ingredients.set(ingredients.size() - 1,
                new MedicalIngredientEntry(last.ingredient(), last.isFluidContainer(), last.consume(), fluidId, last.fluidTag(), amount, last.copy()));
        return this;
    }

    // Convenience: set fluid tag for the last added ingredient
    public ShapelessWithMedicalContainerRecipeBuilder requiresFluidTag(String fluidTag, float amount) {
        if (ingredients.isEmpty()) throw new IllegalStateException("No ingredient to attach fluid tag to. Call requires(...) first.");
        MedicalIngredientEntry last = ingredients.get(ingredients.size() - 1);
        ingredients.set(ingredients.size() - 1,
                new MedicalIngredientEntry(last.ingredient(), last.isFluidContainer(), last.consume(), last.fluidId(), fluidTag, amount, last.copy()));
        return this;
    }

    // Full explicit requires with fluid tag on a specific item
    public ShapelessWithMedicalContainerRecipeBuilder requiresFluidTag(ItemLike item, String fluidTag, float amount) {
        return requires(Ingredient.of(item), true, true, null, fluidTag, amount, false);
    }

    @Override
    public ShapelessWithMedicalContainerRecipeBuilder unlockedBy(String name, net.minecraft.advancements.CriterionTriggerInstance trigger) {
        this.advancement.addCriterion(name, trigger);
        return this;
    }

    @Override
    public ShapelessWithMedicalContainerRecipeBuilder group(@Nullable String groupName) {
        // Not used here
        return this;
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        this.advancement.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(RequirementsStrategy.OR);

        consumer.accept(new Result(
                id,
                this.result,
                this.ingredients,
                this.resultFluidId,
                this.resultFluidAmount,
                this.advancement,
                new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath())
        ));
    }

    private record MedicalIngredientEntry(Ingredient ingredient,
                                          boolean isFluidContainer,
                                          boolean consume,
                                          @Nullable String fluidId,
                                          @Nullable String fluidTag,
                                          float fluidAmount,
                                          boolean copy) {
        public MedicalIngredientEntry(Ingredient ingredient) {
            this(ingredient, false, true, null, null, 0f, false);
        }
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack result;
        private final List<MedicalIngredientEntry> ingredients;
        private final String resultFluidId;
        private final float resultFluidAmount;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, ItemStack result, List<MedicalIngredientEntry> ingredients,
                      String resultFluidId, float resultFluidAmount,
                      Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.ingredients = ingredients;
            this.resultFluidId = resultFluidId;
            this.resultFluidAmount = resultFluidAmount;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("type", PrototypePain.MOD_ID+":"+ShapelessWithMedicalContainerRecipe.Type.ID);
            json.addProperty("category", "misc");

            JsonArray arr = new JsonArray();
            for (MedicalIngredientEntry e : ingredients) {
                JsonObject ing = e.ingredient().toJson().getAsJsonObject();
                if (e.isFluidContainer) ing.addProperty("isFluidContainer", true);
                if (!e.consume) ing.addProperty("consume", false);
                if (e.copy) ing.addProperty("copy", true);
                if (e.fluidId != null || e.fluidTag != null) {
                    JsonObject f = new JsonObject();
                    if (e.fluidId != null) f.addProperty("id", e.fluidId);
                    if (e.fluidTag != null) f.addProperty("tag", e.fluidTag);
                    f.addProperty("amount", e.fluidAmount);
                    ing.add("fluid", f);
                }
                arr.add(ing);
            }
            json.add("ingredients", arr);
            // --- 🔹 handle nullable result ---
            JsonObject resultObj = new JsonObject();
            boolean hasItem = result != null && !result.isEmpty();

            if (hasItem)
                resultObj = itemStackToJson(result);

            if (resultFluidId != null) {
                JsonObject f = new JsonObject();
                f.addProperty("id", resultFluidId);
                f.addProperty("amount", resultFluidAmount);
                resultObj.add("fluid", f);
            }

            // Only add "result" if something is there
            if (hasItem || resultFluidId != null)
                json.add("result", resultObj);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return net.adinvas.prototype_pain.recipe.ModRecipes.SHAPELESS_FUN_SER.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return advancementId;
        }

        private static JsonObject itemStackToJson(ItemStack stack) {
            JsonObject json = new JsonObject();
            json.addProperty("item", net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
            if (stack.getCount() > 1) {
                json.addProperty("count", stack.getCount());
            }
            if (stack.hasTag()) {
                json.addProperty("nbt", stack.getTag().toString());
            }
            return json;
        }
    }


}
