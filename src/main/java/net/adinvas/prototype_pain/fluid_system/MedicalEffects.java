package net.adinvas.prototype_pain.fluid_system;


import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class MedicalEffects {

    public static final MedicalEffect OPIUM = new MedicalEffect() {
        @Override
        public void applyIngested(ServerPlayer player, float ml) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                float op = ml * 0.3f;
                h.setOpioids(h.getOpioids() + op);
            });
        }

        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                float op = ml * 0.5f;
                h.setOpioids(h.getOpioids() + op);
            });
        }
    };

    public static final MedicalEffect MORPHINE = new MedicalEffect() {
        @Override
        public void applyIngested(ServerPlayer player, float ml) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                float op = ml * 0.7f;
                h.setOpioids(h.getOpioids() + op);
            });
        }

        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                float op = ml * 1.1f;
                h.setOpioids(h.getOpioids() + op);
            });
        }
    };

    public static final MedicalEffect HEROIN = new MedicalEffect() {

        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                float op = ml * 1.5f;
                h.setOpioids(h.getOpioids() + op);
            });
        }
    };

    public static final MedicalEffect FENTANYL = new MedicalEffect() {
        @Override
        public void applyIngested(ServerPlayer player, float ml) {
            player.addEffect(new MobEffectInstance(MobEffects.WITHER,300,5));
            player.addEffect(new MobEffectInstance(MobEffects.POISON,300,5));
        }

        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                float op = ml * 50f;
                h.setOpioids(h.getOpioids() + op);
            });
        }
    };

    public static final MedicalEffect WATER = new MedicalEffect() {

    };





}
