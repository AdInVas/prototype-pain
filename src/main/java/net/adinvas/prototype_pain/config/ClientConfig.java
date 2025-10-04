package net.adinvas.prototype_pain.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ADDITIONAL_SOUNDS;

    static {
        BUILDER.push("Prototype Pain Client Config");

        ADDITIONAL_SOUNDS = BUILDER
                .define("additionalSoundEffects",false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
