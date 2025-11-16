package net.adinvas.prototype_pain.visual.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class BloodParticle extends TextureSheetParticle {

    private boolean resting = false;
    private int restTime = 0;

    protected BloodParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double dx,
            double dy,
            double dz,
            SpriteSet sprites,
            float scale,
            float r, float g, float b,
            float alpha
    ) {
        super(level, x, y, z, dx, dy, dz);

        // Size
        this.quadSize *= scale;

        // Color
        this.setColor(r, g, b);
        this.setAlpha(alpha);

        // Physics
        this.setSize(0.1F * scale, 0.1F * scale);
        this.gravity = 0.7f;
        this.friction = 1f;

        this.lifetime = 80 + random.nextInt(10);

        this.pickSprite(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();

        // If touching the ground, stop movement and begin "rest"
        if (this.onGround) {
            this.xd = 0;
            this.yd = 0;
            this.zd = 0;
            this.gravity = 0;

            if (!resting) {
                resting = true;
                restTime = 40 + this.random.nextInt(20); // stays 2–3 seconds
            }

            // Countdown until removal
            if (restTime-- <= 0) {
                this.remove();
            }
        }
    }

    // Provider class
    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x, double y, double z,
                double dx, double dy, double dz
        ) {
            // Default settings (if not using custom)
            float scale = 1.0f;
            float r = 1.0f, g = 1.0f, b = 1.0f; // red
            float alpha = 1.0f;

            return new BloodParticle(
                    level, x, y, z,
                    dx, dy, dz,
                    spriteSet,
                    scale,
                    r, g, b,
                    alpha
            );
        }
    }
}
