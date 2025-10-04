package net.adinvas.prototype_pain;

import net.adinvas.prototype_pain.blocks.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> GLOW_FRUIT_PATCH =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(PrototypePain.MOD_ID, "glow_fruit_patch"));

    public static final ResourceKey<PlacedFeature> GLOW_FRUIT_PATCH_SWAMP =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(PrototypePain.MOD_ID, "glow_fruit_patch_swamp"));

    public static List<Block> stone_list = new ArrayList<>();
    static {
        stone_list.add(Blocks.STONE);
        stone_list.add(Blocks.DEEPSLATE);
        stone_list.add(Blocks.DIRT);
        stone_list.add(Blocks.ANDESITE);
        stone_list.add(Blocks.GRANITE);
        stone_list.add(Blocks.DIORITE);
    }
    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);


        context.register(GLOW_FRUIT_PATCH, new PlacedFeature(
                configured.getOrThrow(ModConfiguredFeatures.GLOW_FRUIT_CONFIG),
                List.of(
                        RarityFilter.onAverageOnceEvery(4), // much more common underground
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(40)), // caves
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(),stone_list) // must be on stone
                        )
                )
        ));
        context.register(GLOW_FRUIT_PATCH_SWAMP, new PlacedFeature(
                configured.getOrThrow(ModConfiguredFeatures.SWAMP_GLOW_FRUIT_CONFIG),
                List.of(
                        RarityFilter.onAverageOnceEvery(24), // rarer
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(60), VerticalAnchor.absolute(90)),
                        BiomeFilter.biome()
                )
        ));
    }
}
