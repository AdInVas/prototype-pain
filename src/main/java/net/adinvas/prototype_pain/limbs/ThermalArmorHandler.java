package net.adinvas.prototype_pain.limbs;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class ThermalArmorHandler {
    private static final Map<Item, Float> THERMAL_CACHE = new HashMap<>();

    private static float getCachedThermalValue(ItemStack stack, Level level) {
        return THERMAL_CACHE.computeIfAbsent(stack.getItem(), item -> computeThermalValue(item, level));
    }

    public static float getArmorInsulation(Player player) {
        float total = 0f;
        for (ItemStack stack : player.getArmorSlots()) {
            if (!stack.isEmpty()) {
                total += getCachedThermalValue(stack, player.level());
            }
        }
        return total;
    }

    private static float computeThermalValue(Item item, Level level) {
        float fromRecipe = getThermalFromRecipe(item, level);
        if (fromRecipe != 0f) return fromRecipe;

        float fromName = getThermalFromName(item);
        if (fromName != 0f) return fromName;

        float fromMaterial = getThermalFromMaterial(item);
        return fromMaterial;
    }

    private static float getThermalFromRecipe(Item item, Level level) {
        if (level == null) return 0f;
        RecipeManager manager = level.getRecipeManager();

        // find first crafting recipe whose result is this item
        Optional<CraftingRecipe> recipeOpt = manager.getAllRecipesFor(RecipeType.CRAFTING)
                .stream()
                .filter(r -> {
                    ItemStack result = r.getResultItem(level.registryAccess());
                    return !result.isEmpty() && result.getItem() == item;
                })
                .findFirst();

        if (recipeOpt.isEmpty()) return 0f;

        Recipe<?> recipe = recipeOpt.get();
        float total = 0f;
        int count = 0;

        for (Ingredient ing : recipe.getIngredients()) {
            for (ItemStack ingStack : ing.getItems()) {
                Item ingItem = ingStack.getItem();
                total += getThermalFromName(ingItem);
                count++;
            }
        }
        return count > 0 ? total / count : 0f;
    }

    private static float getThermalFromName(Item item) {
        String name = item.toString().toLowerCase(Locale.ROOT);

        if (name.contains("leather") || name.contains("fur") || name.contains("wool")) return 3.0f;
        if (name.contains("cloth") || name.contains("fabric")) return 2.5f;
        if (name.contains("iron") || name.contains("steel")) return 2.0f;
        if (name.contains("gold")) return 1.8f;
        if (name.contains("diamond")) return 1.5f;
        if (name.contains("netherite")) return 2.5f;
        if (name.contains("chain")) return 1.2f;
        if (name.contains("obsidian")) return 3.0f;
        if (name.contains("ice") || name.contains("frost") || name.contains("snow")) return -2.0f;
        if (name.contains("slime") || name.contains("gel")) return -1.0f;
        if (name.contains("plant") || name.contains("wood")) return 0.5f;

        return 0f;
    }

    private static float getThermalFromMaterial(Item item) {
        if (!(item instanceof ArmorItem armor)) return 0f;
        ArmorMaterials material;
        try {
            // works for vanilla enums
            material = ArmorMaterials.valueOf(armor.getMaterial().toString().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return 0f; // modded material, skip
        }

        return switch (material) {
            case LEATHER -> 3.0f;
            case CHAIN -> 1.0f;
            case IRON -> 2.0f;
            case GOLD -> 1.8f;
            case DIAMOND -> 1.5f;
            case NETHERITE -> 2.5f;
            default -> 0f;
        };
    }

    public static void clearCache() {
        THERMAL_CACHE.clear();
    }
}
