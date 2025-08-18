package net.adinvas.prototype_pain.client;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public enum StatusSprites {
    BLEED,
    INFECTION,
    FRACTURE,
    DISLOCATION,
    SHRAPNEL,
    SPLINT,
    DISINFECTION,
    TOURNIQUET;

    public ResourceLocation getResourceLocation(){
        switch (this){
            case BLEED -> {
                return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/blood.png");
            }
            case SPLINT -> {
                return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/splint.png");
            }
            case FRACTURE -> {
                return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/fracture.png");
            }
            case SHRAPNEL -> {
                return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/shrapnel.png");
            }
            case INFECTION -> {
                return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/infection.png");
            }
            case TOURNIQUET -> {
                return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/tourniquet.png");
            }
            case DISLOCATION -> {
                return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/dislocation.png");
            }
            case DISINFECTION -> {
                return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/disinfection.png");
            }
            default -> {
                return null;
            }
        }
    }


    public Component getTextComponents(){
        switch (this){
            case TOURNIQUET -> {
                return Component.translatable("prototype_pain.gui.tourniquet_button");
            }
            case DISLOCATION -> {
                return Component.translatable("prototype_pain.gui.dislocation_button");
            }
            case SHRAPNEL -> {
                return Component.translatable("prototype_pain.gui.shrapnel_button");
            }
            case SPLINT -> {
                return Component.translatable("prototype_pain.gui.splint_button");
            }
        }

        return Component.empty();
    }
}
