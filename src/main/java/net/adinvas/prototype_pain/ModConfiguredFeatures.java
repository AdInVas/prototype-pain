package net.adinvas.prototype_pain;

import net.adinvas.prototype_pain.blocks.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLOW_FRUIT_CONFIG =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(PrototypePain.MOD_ID, "glow_fruit_config"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> SWAMP_GLOW_FRUIT_CONFIG =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(PrototypePain.MOD_ID, "glow_swamp_fruit_config"));

    public static final ResourceKey<ConfiguredFeature<?,?>> BROWN_CAP =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(PrototypePain.MOD_ID, "brown_cap_config"));

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context){
        context.register(GLOW_FRUIT_CONFIG,
                new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                        new RandomPatchConfiguration(
                                64,  // tries per patch
                                10,   // xz spread
                                4,   // y spread
                                PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.GLOW_FRUIT_BUSH.get())))
                        )));

        register(context,SWAMP_GLOW_FRUIT_CONFIG,Feature.FLOWER,new RandomPatchConfiguration(64,10,10,PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.GLOW_FRUIT_BUSH.get())))));
        register(context,BROWN_CAP,Feature.FLOWER,new RandomPatchConfiguration(80,10,10,PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.BROWN_CAP.get())))));

    }

    public static <FC extends FeatureConfiguration,F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?,?>> context,ResourceKey<ConfiguredFeature<?,?>> key, F feature, FC configuration){
        context.register(key,new ConfiguredFeature<>(feature,configuration));
    }

}
