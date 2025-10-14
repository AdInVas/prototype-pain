package net.adinvas.prototype_pain.fluid_system;


import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class MedicalEffects {

    public static final MedicalEffect OPIUM = new MedicalEffect() {
        @Override
        public void applyIngested(ServerPlayer player, float ml) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                float op = ml * 0.3f;
                h.setPendingOpioids(h.getPendingOpioids() + op);
            });
        }

        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                float op = ml * 0.5f;
                h.setPendingOpioids(h.getPendingOpioids() + op);
            });
        }
    };

    public static final MedicalEffect MORPHINE = new MedicalEffect() {
        @Override
        public void applyIngested(ServerPlayer player, float ml) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                float op = ml * 0.7f;
                h.setPendingOpioids(h.getPendingOpioids() + op);
            });
        }

        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                float op = ml * 1.1f;
                h.setPendingOpioids(h.getPendingOpioids() + op);
            });
        }
    };

    public static final MedicalEffect HEROIN = new MedicalEffect() {

        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                float op = ml * 1.5f;
                h.setPendingOpioids(h.getPendingOpioids() + op);
            });
        }
    };

    public static final MedicalEffect FENTANYL = new MedicalEffect() {
        @Override
        public void applyIngested(ServerPlayer player, float ml) {
            player.addEffect(new MobEffectInstance(MobEffects.WITHER,300,5));
            player.addEffect(new MobEffectInstance(MobEffects.POISON,300,5));
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.setTemperature(h.getTemperature()-0.005f*ml);
            });
        }

        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                float op = ml * 50f;
                h.setPendingOpioids(h.getPendingOpioids() + op);
            });
        }
    };

    public static final MedicalEffect WATER = new MedicalEffect() {
        @Override
        public void applyIngested(ServerPlayer player, float ml) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.setTemperature(h.getTemperature()-0.005f*ml);
            });
        }
    };

    public static final MedicalEffect PAINKILLERS = new MedicalEffect() {
        @Override
        public void applyIngested(ServerPlayer player, float ml) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setPendingOpioids(h.getPendingOpioids()+(1.4f*ml));
            });
        }

        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setPendingOpioids(h.getPendingOpioids()+(0.1f*ml));
            });
        }
    };

    public static final MedicalEffect STREPTOKINASE = new MedicalEffect() {
        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                for (Limb limb1 : Limb.values()){
                    h.setLimbBleedRate(limb1, h.getLimbBleedRate(limb1)*(1f+ml*0.014f));
                }
                h.setInternalBleeding(h.getInternalBleeding()*(1f+ml*0.0147f));
                h.setBloodViscosity(h.getBloodViscosity()-1.5f*ml);
            });
        }
    };

    public static final MedicalEffect PROCOAGULANT = new MedicalEffect() {
        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                for (Limb limb1 : Limb.values()){
                    h.setLimbBleedRate(limb1, h.getLimbBleedRate(limb1)*(1f-ml*0.016f));
                }
                h.setInternalBleeding(h.getInternalBleeding()*(1f-ml*0.017f));
                h.setBloodViscosity(h.getBloodViscosity()+0.6f*ml);
            });
        }
    };

    public static final MedicalEffect NALOXONE = new MedicalEffect() {
        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setOpioids(h.getOpioids()-(4f*ml));
                h.setPendingOpioids(h.getPendingOpioids()-(4f*ml));
            });
        }
    };

    public static final MedicalEffect ALCOHOL = new MedicalEffect() {
        @Override
        public void applyIngested(ServerPlayer player, float ml) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, (int) (1*ml),1));
            player.addEffect(new MobEffectInstance(MobEffects.POISON, (int) (1*ml),2));
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.setTemperature(h.getTemperature()-0.005f*ml);
            });
        }

        @Override
        public void applyOnSkin(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setLimbPain(limb, h.getLimbPain(limb)+0.1f*ml);
                h.setLimbDesinfected(limb,Math.max(h.getLimbDesinfected(limb),700*ml));
            });
        }
    };

    public static final MedicalEffect BRAINGROW = new MedicalEffect() {
        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setLimbPain(Limb.HEAD, h.getLimbPain(Limb.HEAD)+10*ml);
                h.setBrainHealth(h.getBrainHealth()-ml/2);
            });
        }

        @Override
        public void applyIngested(ServerPlayer player, float ml) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
               h.setBrainHealth(h.getBrainHealth()+ml);
                h.setLimbPain(Limb.HEAD, h.getLimbPain(Limb.HEAD)+5*ml);
            });
        }
    };

    public static final MedicalEffect ANTISEPTIC = new MedicalEffect() {
        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setLimbPain(limb, h.getLimbPain(limb)+5*ml);
                h.setLimbDesinfected(limb,h.getLimbDesinfected(limb)+300*ml);
            });
        }

        @Override
        public void applyIngested(ServerPlayer player, float ml) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, (int) (1*ml),2));
            player.addEffect(new MobEffectInstance(MobEffects.POISON, (int) (1*ml),3));
        }

        @Override
        public void applyOnSkin(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setLimbPain(limb, h.getLimbPain(limb)+3.5f*ml);
                h.setLimbDesinfected(limb,Math.max(h.getLimbDesinfected(limb),1200*ml));
            });
        }
    };

    public static final MedicalEffect RELIEF_CREAM = new MedicalEffect() {
        @Override
        public void applyOnSkin(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setLimbDesinfected(limb,Math.max(h.getLimbDesinfected(limb),636*ml));
                h.setLimbPain(limb, h.getLimbPain(limb)*0.01f);
            });
        }
    };

    public static final MedicalEffect SALINE = new MedicalEffect() {
        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setBloodViscosity(h.getBloodViscosity()+0.01f*ml);
                h.setBloodVolume(h.getBloodVolume()+ml*0.001f);
            });
        }
    };

    public static final MedicalEffect ANTIBIOTICS = new MedicalEffect() {
        @Override
        public void applyIngested(ServerPlayer player, float ml) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setAntibiotic_timer(Math.max(h.getAntibiotic_timer(),4000));
            });
        }
    };

    public static final MedicalEffect ANTISERUM = new MedicalEffect() {
        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setAntibiotic_timer(Math.max(h.getAntibiotic_timer(),6000));
                h.setLimbDesinfected(limb,h.getLimbDesinfected(limb)+70*ml);
                h.setBloodVolume(h.getBloodVolume()+0.001f*ml);
            });
        }
    };

    public static final MedicalEffect CEFTRAIAXONE = new MedicalEffect() {
        @Override
        public void applyInjected(ServerPlayer player, float ml, Limb limb) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setAntibiotic_timer(h.getAntibiotic_timer()+225*ml);
                h.setLimbPain(Limb.CHEST, h.getLimbPain(Limb.CHEST)+1.5f*ml);
            });
        }
    };

}
