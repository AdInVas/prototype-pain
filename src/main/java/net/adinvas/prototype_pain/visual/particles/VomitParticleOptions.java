package net.adinvas.prototype_pain.visual.particles;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class VomitParticleOptions implements ParticleOptions {

    public static final Codec<VomitParticleOptions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("scale").forGetter(o -> o.scale),
                    Codec.FLOAT.fieldOf("r").forGetter(o -> o.r),
                    Codec.FLOAT.fieldOf("g").forGetter(o -> o.g),
                    Codec.FLOAT.fieldOf("b").forGetter(o -> o.b),
                    Codec.FLOAT.fieldOf("alpha").forGetter(o -> o.alpha),
                    Vec3.CODEC.fieldOf("velocity").forGetter(o -> o.velocity)
            ).apply(instance, VomitParticleOptions::new)
    );

    public static final ParticleOptions.Deserializer<VomitParticleOptions> DESERIALIZER =
            new ParticleOptions.Deserializer<>() {

                @Override
                public VomitParticleOptions fromCommand(ParticleType<VomitParticleOptions> particleType, com.mojang.brigadier.StringReader stringReader) throws CommandSyntaxException {
                    throw new UnsupportedOperationException("Can't parse from command");
                }

                @Override
                public VomitParticleOptions fromNetwork(ParticleType<VomitParticleOptions> type, FriendlyByteBuf buf)
                {
                    float scale = buf.readFloat();
                    float r = buf.readFloat();
                    float g = buf.readFloat();
                    float b = buf.readFloat();
                    float alpha = buf.readFloat();
                    Vec3 vel = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());

                    return new VomitParticleOptions(scale, r, g, b, alpha, vel);
                }
            };

    public final float scale;
    public final float r, g, b, alpha;
    public final Vec3 velocity;

    public VomitParticleOptions(float scale, float r, float g, float b, float alpha, Vec3 velocity)
    {
        this.scale = scale;
        this.r = r;
        this.g = g;
        this.b = b;
        this.alpha = alpha;
        this.velocity = velocity;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.BLOOD_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(scale);
        buf.writeFloat(r);
        buf.writeFloat(g);
        buf.writeFloat(b);
        buf.writeFloat(alpha);
        buf.writeDouble(velocity.x);
        buf.writeDouble(velocity.y);
        buf.writeDouble(velocity.z);
    }

    @Override
    public String writeToString() {
        return "blood_particle";
    }
}
