package net.adinvas.prototype_pain.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue EXPERIMENTAL_VISUALS;
    public static final ForgeConfigSpec.BooleanValue EXPERIMENTAL_SOUNDS;

    static {
        BUILDER.push("Prototype Pain Client Config");

        EXPERIMENTAL_VISUALS = BUILDER
                .comment("Experimental { ;) } Options")
                .define("additionalVisuals",false);

        EXPERIMENTAL_SOUNDS =BUILDER
                .define("additionalSounds",false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
