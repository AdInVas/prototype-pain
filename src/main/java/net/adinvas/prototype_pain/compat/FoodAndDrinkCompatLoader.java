package net.adinvas.prototype_pain.compat;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FoodAndDrinkCompatLoader extends SimpleJsonResourceReloadListener {
    public static final FoodAndDrinkCompatLoader INSTANCE = new FoodAndDrinkCompatLoader();

    public FoodAndDrinkCompatLoader() {
        super(new Gson(), "prototype_pain");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager manager, ProfilerFiller profiler) {
        FoodAndDrinkCompat.clear();
        files.forEach((fileLocation, jsonElement) -> {
            if (!fileLocation.getPath().equals("food")) return; // only food.json files

            JsonObject json = jsonElement.getAsJsonObject();
            for (String key : json.keySet()) {
                try {
                    ResourceLocation itemId = new ResourceLocation(key);
                    JsonObject obj = json.getAsJsonObject(key);

                    FoodAndDrinkCompat.FoodEntry entry = new FoodAndDrinkCompat.FoodEntry();
                    entry.sickness = obj.has("sickness") ? obj.get("sickness").getAsFloat() : 0f;
                    entry.thirst = obj.has("thirst") ? obj.get("thirst").getAsFloat() : 0f;
                    entry.temperature = obj.has("temperature") ? obj.get("temperature").getAsFloat() : 0f;

                    FoodAndDrinkCompat.addEntry(itemId, entry);
                } catch (Exception e) {
                    System.err.println("[Prototype Pain] Failed to parse food compat entry: " + key);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onReload(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }
}
