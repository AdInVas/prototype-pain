package net.adinvas.prototype_pain.compat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TempCompatLoader extends SimpleJsonResourceReloadListener {
    public static final TempCompatLoader INSTANCE = new TempCompatLoader();

    public TempCompatLoader() {
        super(new Gson(), "prototype_pain");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager manager, ProfilerFiller profilerFiller) {
        TempCompat.clearBlock();
        TempCompat.clearBiome();
        files.forEach((fileLocation,jsonElement)->{
            if (!fileLocation.getPath().equals("temperature_blocks")&&!fileLocation.getPath().equals("temperature_biomes"))return;

            if (fileLocation.getPath().equals("temperature_blocks")) {
                JsonObject json = jsonElement.getAsJsonObject();
                for (String key : json.keySet()) {
                    try {
                        ResourceLocation blockId = new ResourceLocation(key);
                        JsonObject obj = json.getAsJsonObject(key);
                        float value = obj.has("temperature") ? obj.get("temperature").getAsFloat() : 0f;

                        TempCompat.addEntryBlock(blockId, value);
                    } catch (Exception e) {
                        System.err.println("[Prototype Pain] Failed to parse temperatureblock compat entry: " + key);
                    }
                }
            } else if (fileLocation.getPath().equals("temperature_biomes")) {
                JsonObject json = jsonElement.getAsJsonObject();
                for (String key : json.keySet()) {
                    try {
                        ResourceLocation blockId = new ResourceLocation(key);
                        JsonObject obj = json.getAsJsonObject(key);
                        float value = obj.has("temperature") ? obj.get("temperature").getAsFloat() : 0f;
                        PrototypePain.LOGGER.info("temp {}, biome {}",blockId,value);
                        TempCompat.addEntryBiome(blockId, value);
                    } catch (Exception e) {
                        System.err.println("[Prototype Pain] Failed to parse temperaturebiome compat entry: " + key);
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void onReload(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }
}
