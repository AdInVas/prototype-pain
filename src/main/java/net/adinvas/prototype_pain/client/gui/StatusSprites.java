package net.adinvas.prototype_pain.client.gui;

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

    public float getBaseScale(){
        switch (this){
            case SPLINT -> {
                return 1.6f;
            }
            case FRACTURE -> {
                return 1.5f;
            }
            case SHRAPNEL -> {
                return 1.2f;
            }
            case INFECTION -> {
                return 1.3f;
            }
            case TOURNIQUET -> {
                return 2f;
            }
            case DISLOCATION -> {
                return 1.5f;
            }
            case DISINFECTION -> {
                return 1.5f;
            }
            default -> {
                return 1f;
            }
        }
    }



}
