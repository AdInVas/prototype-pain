package net.adinvas.prototype_pain.fluid_system.n;

import cpw.mods.util.Lazy;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.blocks.ModBlocks;
import net.adinvas.prototype_pain.fluid_system.MedicalEffect;
import net.adinvas.prototype_pain.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, PrototypePain.MOD_ID);

    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOW_RL = new ResourceLocation("block/water_flow");
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, PrototypePain.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,PrototypePain.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,PrototypePain.MOD_ID);


    private static RegistryObject<FluidType> register(String name, FluidType.Properties prop, MedicalEffect effect, int color){
        return FLUID_TYPES.register(name,()-> new MedicalFluidType(prop,WATER_STILL_RL,WATER_FLOW_RL,effect,color));
    }

    public record RegisteredMedicalFluid(
            RegistryObject<FluidType> type,
            RegistryObject<FlowingFluid> still,
            RegistryObject<FlowingFluid> flowing,
            RegistryObject<LiquidBlock> block,
            RegistryObject<Item> bucket,
            Lazy<ForgeFlowingFluid.Properties> properties
    ) {}


    public static void register(IEventBus bus){
        FLUIDS.register(bus);
        FLUID_TYPES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}
