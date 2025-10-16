package net.adinvas.prototype_pain.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ADDITIONAL_VISUALS;

    static {
        BUILDER.push("Prototype Pain Client Config");

        ADDITIONAL_VISUALS = BUILDER
                .comment("Warning, Flashing Lights, and wierd visual effects")
                .define("additionalVisuals",false);


        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
