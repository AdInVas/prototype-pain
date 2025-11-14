package net.adinvas.prototype_pain.compat;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class TempCompat {
    private static final Map<ResourceLocation, Float> BIOME_TEMPERATURE_BY_ID = new HashMap<>();
    private static final Map<Block,Float> BLOCK_TEMPERATURE_DATA = new HashMap<>();

    public static void clearBlock() {
        BLOCK_TEMPERATURE_DATA.clear();
    }
    public static void clearBiome() {
        BIOME_TEMPERATURE_BY_ID.clear();
    }


    public static void addEntryBlock(ResourceLocation id, Float value){
        Block block = ForgeRegistries.BLOCKS.getValue(id);
        if (block!=null) BLOCK_TEMPERATURE_DATA.put(block,value);
    }

    public static void addEntryBiome(ResourceLocation id, Float value){
        if (id!=null) BIOME_TEMPERATURE_BY_ID.put(id,value);
    }

    public static Float get(Block block){
        return BLOCK_TEMPERATURE_DATA.get(block);
    }

    public static Float getForPosition(Level level, BlockPos pos) {
        return level.getBiome(pos).unwrapKey() // Optional<ResourceKey<Biome>>
                .map(key -> BIOME_TEMPERATURE_BY_ID.get(key.location()))
                .orElse(null);
    }

}
