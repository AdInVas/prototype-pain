package net.adinvas.prototype_pain.fluid_system;


import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.network.chat.Component;

public class MedicalFluid {
    private final String id;
    private final MedicalEffect medicalEffect;
    private final int color;


    public MedicalFluid(String id, MedicalEffect medicalEffect, int color) {
        this.id = id;
        this.medicalEffect = medicalEffect;
        this.color = color;
    }

    public MedicalEffect getMedicalEffect() {
        return medicalEffect;
    }

    public String getId() {
        return PrototypePain.MOD_ID+":"+id;
    }

    public Component getDisplayName(){
        return Component.translatable("prototype_pain.fluid."+id);
    }

    public Component getDescription(){
        return Component.translatable("prototype_pain.fluid."+id+".description");
    }

    public int getColor() {
        return color;
    }
}
