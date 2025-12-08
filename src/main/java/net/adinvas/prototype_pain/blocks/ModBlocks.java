package net.adinvas.prototype_pain.blocks;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PrototypePain.MOD_ID);


    public static final RegistryObject<Block> GLOW_FRUIT_BUSH = BLOCKS.register("glow_fruit_bush",() -> new GlowFruitBushBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)
            .randomTicks()
            .instabreak()
            .noCollission()));

    public static final RegistryObject<Block> BROWN_CAP = BLOCKS.register("brown_cap", ()-> new BrownCapBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM).randomTicks()));

    public static final RegistryObject<Block> SCAV_BLOCK = BLOCKS.register("scav_plushie",()-> new ScavBlock(
            BlockBehaviour.Properties.of().instabreak().noOcclusion().noCollission()
    ));



}
