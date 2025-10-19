package net.adinvas.prototype_pain;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_GLOW_FRUIT = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,new ResourceLocation(PrototypePain.MOD_ID,"add_glow_fruit"));
    public static final ResourceKey<BiomeModifier> ADD_GLOW_FRUIT_SWAMP = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,new ResourceLocation(PrototypePain.MOD_ID,"add_glow_fruit_swamp"));
    public static final ResourceKey<BiomeModifier> ADD_BROWN_CAP = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,new ResourceLocation(PrototypePain.MOD_ID,"add_brown_cap"));

    public static void BootStrap(BootstapContext<BiomeModifier> context){
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_GLOW_FRUIT,new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GLOW_FRUIT_PATCH)),
                GenerationStep.Decoration.UNDERGROUND_DECORATION
        ));
        context.register(ADD_GLOW_FRUIT_SWAMP,new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GLOW_FRUIT_PATCH_SWAMP)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));
        context.register(ADD_BROWN_CAP,new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BROWN_CAP)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));
    }




}
