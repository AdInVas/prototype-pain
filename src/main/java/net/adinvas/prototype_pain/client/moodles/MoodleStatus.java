package net.adinvas.prototype_pain.client.moodles;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.resources.ResourceLocation;

public enum MoodleStatus {
    NONE,
    LIGHT,
    NORMAL,
    HEAVY,
    CRITICAL;


    public ResourceLocation getTex(){
        switch (this){
            case LIGHT -> {
                return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/moodle_light.png");
            }
            case NORMAL -> {
                return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/moodle_normal.png");
            }
            case HEAVY -> {
                return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/moodle_heavy.png");
            }
            case CRITICAL -> {
                return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/moodle_critical.png");
            }
            default -> {
                return new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/moodles/moodle_light.png");
            }
        }
    }
}
