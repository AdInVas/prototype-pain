package net.adinvas.prototype_pain.visual.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class BloodParticle extends TextureSheetParticle {
    protected BloodParticle(ClientLevel level, double x, double y, double z,
                           double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z, dx, dy, dz);
        this.setSize(0.1F, 0.1F);
        this.gravity = 4f;
        this.friction = 0.01f;
        this.lifetime = (int) (10 + Math.floor(Math.random()*10));
        this.pickSprite(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
    }

    public static class Provider implements ParticleProvider<SimpleParticleType>{
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double v, double v1, double v2, double v3, double v4, double v5) {
            return new BloodParticle(clientLevel,v,v1,v2,v3,v4,v5,this.spriteSet);
        }
    }
}
