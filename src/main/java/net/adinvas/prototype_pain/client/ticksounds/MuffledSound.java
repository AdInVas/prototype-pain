package net.adinvas.prototype_pain.client.ticksounds;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

public class MuffledSound implements SoundInstance {
    private final SoundInstance original;
    private final float volume;
    private final float pitch;

    public MuffledSound(SoundInstance original, float volume, float pitch) {
        this.original = original;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public ResourceLocation getLocation() {
        return original.getLocation();
    }

    @Override
    public @Nullable WeighedSoundEvents resolve(SoundManager soundManager) {
        return original.resolve(soundManager);
    }

    @Override
    public Sound getSound() {
        return original.getSound();
    }

    @Override
    public SoundSource getSource() {
        return original.getSource();
    }

    @Override
    public boolean isLooping() {
        return original.isLooping();
    }

    @Override
    public boolean isRelative() {
        return original.isRelative();
    }

    @Override
    public int getDelay() {
        return original.getDelay();
    }

    @Override
    public float getVolume() {
        return this.volume;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }

    @Override
    public double getX() {
        return original.getX();
    }

    @Override
    public double getY() {
        return original.getY();
    }

    @Override
    public double getZ() {
        return original.getZ();
    }

    @Override
    public Attenuation getAttenuation() {
        return original.getAttenuation();
    }
}
