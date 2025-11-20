package net.adinvas.prototype_pain.visual.particles;

import com.mojang.serialization.Codec;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, PrototypePain.MOD_ID);

    public static final RegistryObject<ParticleType<VomitParticleOptions>> BLOOD_PARTICLE =
            PARTICLE_TYPES.register("blood_particle", () ->
                    new ParticleType<VomitParticleOptions>(false, VomitParticleOptions.DESERIALIZER) {
                        @Override
                        public Codec<VomitParticleOptions> codec() {
                            return VomitParticleOptions.CODEC;
                        }
                    }
            );



    public static void register(IEventBus bus){
        PARTICLE_TYPES.register(bus);
    }
}
