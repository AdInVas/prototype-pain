package net.adinvas.prototype_pain.compat;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class FoodAndDrinkCompat {
    public static class FoodEntry{
        public float sickness;
        public float thirst;
        public float temperature;
    }
    private static final Map<Item, FoodEntry> FOOD_DATA = new HashMap<>();

    public static void clear() {
        FOOD_DATA.clear();
    }

    public static void addEntry(ResourceLocation itemId, FoodEntry entry) {
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        if (item != null) FOOD_DATA.put(item, entry);
    }

    public static FoodEntry get(Item item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (FOOD_DATA.get(item)==null){
            return getFallback(id,new ItemStack(item));
        }
        return FOOD_DATA.get(item);
    }

    public static FoodEntry getFallback(ResourceLocation id, ItemStack stack) {
        Item item = stack.getItem();
        String path = id.getPath().toLowerCase(); // registry name, e.g. "apple_pie" or "energy_drink"
        String displayName = stack.getHoverName().getString().toLowerCase(); // actual visible name

        FoodEntry entry = new FoodEntry();
        if (id.getNamespace().equals(PrototypePain.MOD_ID)) {
            return null;
        }
        // --- Drinks ---
        //TODO actuall values for thirst and shit
        if (stack.getUseAnimation()== UseAnim.DRINK) {
            boolean ishot = path.contains("tea") || path.contains("coffee")|| path.contains("hot");
            boolean iscold = path.contains("cold") || path.contains("ice");
            entry.sickness = 0.0f;
            entry.thirst = 0.0f;
            entry.temperature = (iscold ? -0.4f : -0.2f)+(ishot? 0.3f:0.0f) ;
            return entry;
        }

        // --- Food ---
        if (path.contains("food") || path.contains("meat") || path.contains("pie") ||
                path.contains("cake") || path.contains("apple") || displayName.contains("food")) {

            entry.sickness = 0.0f;
            entry.thirst = 0.0f;
            entry.temperature = 0.1f;
            return entry;
        }

        // --- Raw meat (bad for sickness) ---
        if (path.contains("raw") || displayName.contains("raw")) {
            entry.sickness = 0.0f;
            entry.thirst = 0.0f;
            entry.temperature = 0.0f;
            return entry;
        }

        // --- Cooked foods ---
        if (path.contains("cooked") || path.contains("grilled") || path.contains("baked")) {
            entry.sickness = 0.0f;
            entry.thirst = 0.0f;
            entry.temperature = 0.1f;
            return entry;
        }
        // If no match found, return null so it doesn't show anything
        return null;
    }
}
