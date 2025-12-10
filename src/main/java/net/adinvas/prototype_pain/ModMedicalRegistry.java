package net.adinvas.prototype_pain;

import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = PrototypePain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModMedicalRegistry {
    public static final ResourceLocation UPGRADE_TYPES_NAME = new ResourceLocation(PrototypePain.MOD_ID, "medical_fluids");
    public static final ResourceKey<Registry<MedicalFluid>> MEDICAL_FLUIDS_KEY =
            ResourceKey.createRegistryKey(UPGRADE_TYPES_NAME);

    public static Supplier<IForgeRegistry<MedicalFluid>> REGISTRY;

    @SubscribeEvent
    public static void registerRegistry(NewRegistryEvent event) {
        REGISTRY = event.create(
                new RegistryBuilder<MedicalFluid>()
                        .setName(MEDICAL_FLUIDS_KEY.location())
        );
    }
}
