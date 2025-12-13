package net.adinvas.prototype_pain.datagen;

import net.adinvas.prototype_pain.blocks.ModBlocks;
import net.adinvas.prototype_pain.item.ModItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    protected ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropOther(ModBlocks.BROWN_CAP.get(), ModItems.BrownCap.get());
        this.dropOther(ModBlocks.GLOW_FRUIT_BUSH.get(), ItemStack.EMPTY.getItem());
        this.dropSelf(ModBlocks.SCAV_BLOCK.get());
        this.dropSelf(ModBlocks.MEDICAL_MIXER.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
