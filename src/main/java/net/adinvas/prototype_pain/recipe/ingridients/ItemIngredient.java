package net.adinvas.prototype_pain.recipe.ingridients;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class ItemIngredient {
    @Nullable
    private final ItemStack item;
    @Nullable
    private final TagKey<Item> tag;
    private final int count;

    /* ---------- Constructors ---------- */

    public ItemIngredient(ItemStack item, int count) {
        this.item = item;
        this.tag = null;
        this.count = count;
    }

    public ItemIngredient(TagKey<Item> tag, int count) {
        this.item = null;
        this.tag = tag;
        this.count = count;
    }

    /* ---------- Info ---------- */

    public boolean isTagged() {
        return tag != null;
    }

    public int getCount() {
        return count;
    }

    @Nullable
    public ItemStack getItem() {
        return item;
    }

    @Nullable
    public TagKey<Item> getTag() {
        return tag;
    }

    /* ---------- Matching ---------- */

    public boolean matches(SimpleContainer inventory, int startSlot, int endSlot) {
        return countMatching(inventory, startSlot, endSlot) >= count;
    }

    public boolean matches(IItemHandler inventory, int startSlot, int endSlot) {
        return countMatching(inventory, startSlot, endSlot) >= count;
    }

    private int countMatching(SimpleContainer inventory, int start, int end) {
        int found = 0;
        for (int i = start; i <= end; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;

            if (item != null && ItemStack.isSameItemSameTags(item, stack)) {
                found += stack.getCount();
            } else if (tag != null && stack.is(tag)) {
                found += stack.getCount();
            }

            if (found >= count) return found; // early exit if enough found
        }
        return found;
    }

    private int countMatching(IItemHandler inventory, int start, int end) {
        int found = 0;
        for (int i = start; i <= end; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            if (item != null && ItemStack.isSameItemSameTags(item, stack)) {
                found += stack.getCount();
            } else if (tag != null && stack.is(tag)) {
                found += stack.getCount();
            }

            if (found >= count) return found;
        }
        return found;
    }

    public void consume(IItemHandler inventory, int startSlot, int endSlot) {
        int remaining = count;

        for (int slot = startSlot; slot <= endSlot && remaining > 0; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            if ((item != null && ItemStack.isSameItemSameTags(item, stack)) ||
                    (tag != null && stack.is(tag))) {
                int toRemove = Math.min(stack.getCount(), remaining);
                inventory.extractItem(slot, toRemove, false);
                remaining -= toRemove;
            }
        }
    }

    /* ---------- JSON ---------- */

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        if (item != null) {
            json.addProperty("item",
                    ForgeRegistries.ITEMS.getKey(item.getItem()).toString());
        } else if (tag != null) {
            json.addProperty("tag", tag.location().toString());
        }

        json.addProperty("count", count);
        return json;
    }

    public static ItemIngredient fromJson(JsonObject obj) {
        int count = GsonHelper.getAsInt(obj, "count", 1);

        if (obj.has("item")) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(obj, "item")));
            return new ItemIngredient(new ItemStack(item), count);
        } else if (obj.has("tag")) {
            TagKey<Item> tag = TagKey.create(Registries.ITEM, new ResourceLocation(GsonHelper.getAsString(obj, "tag")));
            return new ItemIngredient(tag, count);
        }

        throw new IllegalArgumentException("Invalid item ingredient JSON: " + obj);
    }
}
