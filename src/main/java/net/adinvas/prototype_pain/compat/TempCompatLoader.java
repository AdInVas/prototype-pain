package net.adinvas.prototype_pain.compat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
                        float value = obj.has("temperature") ? obj.get("temperature").getAsFloat() : 36.6f;

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
                        float day = obj.has("temperature") ? obj.get("temperature").getAsFloat() : null;
                        float night = obj.has("nightChange") ? obj.get("nightChange").getAsFloat() : 0;
                        float spring = obj.has("spring") ? obj.get("spring").getAsFloat() : day;
                        float summer = obj.has("summer") ? obj.get("summer").getAsFloat() : day;
                        float fall = obj.has("fall") ? obj.get("fall").getAsFloat() : day;
                        float winter = obj.has("winter") ? obj.get("winter").getAsFloat() : day;

                        if (!Float.isNaN(day)){
                            TempCompat.BiomeTemperatureEntry entry = new TempCompat.BiomeTemperatureEntry();
                            entry.temperature = day;
                            entry.nightChange = night;
                            entry.summer = summer;
                            entry.fall = fall;
                            entry.spring = spring;
                            entry.winter = winter;
                            TempCompat.addEntryBiome(blockId,entry);
                        }
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
