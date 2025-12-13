package net.adinvas.prototype_pain.blocks;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.blocks.medical_mixer.MedicalMixerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PrototypePain.MOD_ID);


    public static final RegistryObject<BlockEntityType<MedicalMixerBlockEntity>> MEDICAL_MIXER_BE =
            BLOCK_ENTITIES.register("medical_mixer_be",()->BlockEntityType.Builder.of(MedicalMixerBlockEntity::new,
                    ModBlocks.MEDICAL_MIXER.get()).build(null));



    public static void register(IEventBus bus){
        BLOCK_ENTITIES.register(bus);
    }
}
