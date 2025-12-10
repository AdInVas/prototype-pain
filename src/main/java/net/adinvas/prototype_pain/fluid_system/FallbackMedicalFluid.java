package net.adinvas.prototype_pain.fluid_system;

public class FallbackMedicalFluid extends MedicalFluid{
    public FallbackMedicalFluid(MedicalEffect medicalEffect, int color) {
        super(medicalEffect, color);
    }

    @Override
    public boolean showInJEI() {
        return false;
    }
}
