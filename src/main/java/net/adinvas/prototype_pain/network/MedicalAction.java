package net.adinvas.prototype_pain.network;

import net.minecraft.network.chat.Component;

public enum MedicalAction {
    REMOVE_TOURNIQUET,
    TRY_SHRAPNEL,
    REMOVE_SPLINT,
    FIX_DISLOCATION;



    public Component getTextComponents(){
        switch (this){
            case REMOVE_TOURNIQUET -> {
                return Component.translatable("prototype_pain.gui.tourniquet_button");
            }
            case FIX_DISLOCATION -> {
                return Component.translatable("prototype_pain.gui.dislocation_button");
            }
            case TRY_SHRAPNEL -> {
                return Component.translatable("prototype_pain.gui.shrapnel_button");
            }
            case REMOVE_SPLINT -> {
                return Component.translatable("prototype_pain.gui.splint_button");
            }
        }
        return Component.empty();
    }
}
