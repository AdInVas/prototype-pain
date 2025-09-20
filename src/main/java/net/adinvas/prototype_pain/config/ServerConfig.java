package net.adinvas.prototype_pain.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;


    public static final ForgeConfigSpec.DoubleValue INFECTION_RATE;
    public static final ForgeConfigSpec.DoubleValue DISINFECTION_RATE;
    public static final ForgeConfigSpec.DoubleValue WUND_ANTIBLEED_RATE;
    public static final ForgeConfigSpec.DoubleValue INFECTION_CHANCE;
    public static final ForgeConfigSpec.DoubleValue INFECTION_MUSCLE_DRAIN;
    public static final ForgeConfigSpec.DoubleValue HEMOTHORAX_HEAL_RATE;

    // Oxygen changes
    public static final ForgeConfigSpec.DoubleValue OXYGEN_REPLENISH;
    public static final ForgeConfigSpec.DoubleValue OXYGEN_DRAIN;

    // Blood regen & bleeding
    public static final ForgeConfigSpec.DoubleValue BLOOD_REGEN_RATE;   // L per second
    public static final ForgeConfigSpec.DoubleValue MAX_BLEED_RATE;// L per second
    public static final ForgeConfigSpec.DoubleValue BLOOD_VISCOSITY_REGEN;

    // Damage scaling
    public static final ForgeConfigSpec.DoubleValue DAMAGE_SCALE;
    public static final ForgeConfigSpec.DoubleValue PAIN_PER_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue OPIATE_PAIN_REDUCTION;
    public static final ForgeConfigSpec.DoubleValue FRAC_DISL_FROM_MUSCLE_DAMAGE_CHANCE;
    public static final ForgeConfigSpec.IntValue MAX_FRACT_DISL_TIME_T;

    // Limb healing rates
    public static final ForgeConfigSpec.DoubleValue NORMAL_LIMB_HEAL_RATE;  // % per second
    public static final ForgeConfigSpec.DoubleValue BOOSTED_LIMB_HEAL_RATE;   // % per second
    public static final ForgeConfigSpec.DoubleValue MAGICAL_HEAL_RATE;

    public static final ForgeConfigSpec.DoubleValue MANUAL_SHRAPNEL_SUCCESS_CHANCE;
    public static final ForgeConfigSpec.DoubleValue DISLOCATION_FIX_CHANCE;

    // Tourniquet behavior
    public static final ForgeConfigSpec.DoubleValue TOURNIQUET_PAIN_PER_TICK;
    public static final ForgeConfigSpec.IntValue TOURNIQUET_SAFE_TICKS;             // 60s before muscle damage starts
    public static final ForgeConfigSpec.DoubleValue TOURNIQUET_MUSCLE_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue CONS_PENALTY_PER_OPIOID;
    public static final ForgeConfigSpec.DoubleValue CONSIOUSNESS_DELTA;

    //Heavy Shit
    public static final ForgeConfigSpec.BooleanValue TOGGLE_UNCONTIOUS_INVENTORY;







    static {
        BUILDER.push("Prototype Pain Server Config");

        INFECTION_RATE = BUILDER
                .comment("The rate of infection build up (%/s)")
                        .defineInRange("infectionRate",0.25d,0,100);

        DISINFECTION_RATE = BUILDER
                .comment("The rate of infection removal when disinfection is applied (%/s)")
                        .defineInRange("disinfectionRate",3d,0,100);

        WUND_ANTIBLEED_RATE = BUILDER
                .comment("The rate at which internal Bleeding heals (L/s)")
                        .defineInRange("wundAntiBleed",0.02,0,10);

        INFECTION_CHANCE=BUILDER
                .comment("Chance of an infection at 0% skin health (%/s)")
                        .defineInRange("infectionChance",0.001,0,1);
        INFECTION_MUSCLE_DRAIN = BUILDER
                .comment("The rate at which Infection Drains Muscle health(only above 80% infection) (%/s)")
                        .defineInRange("infectionMuscleDrain",0.4,0,100);
        HEMOTHORAX_HEAL_RATE = BUILDER
                .comment("The rate at which Hemothorax is healed (pts/s)")
                        .defineInRange("hemothoraxHealRate",0.1,0,100);
        OXYGEN_REPLENISH = BUILDER
                .comment("The rate at which Oxygen Replenishies (%/s)")
                        .defineInRange("oxygenReplenish",8d,0,100);
        OXYGEN_DRAIN = BUILDER
                .comment("The rate at which Oxygen Drains when not breathing (%/s)")
                        .defineInRange("oxygenDrain",5d,0,100);
        BLOOD_REGEN_RATE = BUILDER
                .comment("The rate at which Blood balances itself around 5L at full hunger (L/s)")
                        .defineInRange("bloodRegen",0.001,0,10);
        MAX_BLEED_RATE = BUILDER
                .comment("The Maximum Rate of Bleeding from one Limb (L/s)")
                        .defineInRange("maxBleedRate",0.03,0,10);
        BLOOD_VISCOSITY_REGEN = BUILDER
                .comment("The rate at which Blood viscosity returns to 0 (pts/s)")
                        .defineInRange("bloodViscosityRegen",0.2,0,100);
        DAMAGE_SCALE = BUILDER
                .comment("% Damage Per point of normal damage to the Limb muscle and skin health")
                        .defineInRange("damageScale",5d,0.1,100);
        PAIN_PER_DAMAGE = BUILDER
                .comment("How much pain gives one point of normal damage")
                        .defineInRange("painPerDamage",5,0.1,100);
        OPIATE_PAIN_REDUCTION = BUILDER
                .comment("How much pain relief gives each point of Opiates")
                        .defineInRange("OpiatePainReduction",0.4,0.1,100);
        FRAC_DISL_FROM_MUSCLE_DAMAGE_CHANCE = BUILDER
                .comment("Chance for Discocation/Fracture at 0% Muscle Health")
                        .defineInRange("fractDislcFromMuslceDamageChance",0.33,0,1);
        MAX_FRACT_DISL_TIME_T= BUILDER
                .comment("Max Fracture/Dislocation duration (ticks)")
                        .defineInRange("maxFractDislcTime",5*60*20,1, Integer.MAX_VALUE);
        NORMAL_LIMB_HEAL_RATE = BUILDER
                .comment("The normal rate at which Limbs Heal their Health passively (%/s)")
                        .defineInRange("normalLimbHealRate",0.04,0,100);
        BOOSTED_LIMB_HEAL_RATE = BUILDER
                .comment("The Boosted rate at which Limbs Heal ther Health passively (%/s)")
                        .defineInRange("boostedLimbHealRate",0.125,0,100);
        TOURNIQUET_PAIN_PER_TICK = BUILDER
                .comment("Pain per tick for limb with applied tourniquet")
                        .defineInRange("tourniquetPainPerTick",0.05,0,100);
        TOURNIQUET_SAFE_TICKS=BUILDER
                .comment("Ticks for how long a tourniquet can be on a limb befor causing muscle damage (ticks)")
                        .defineInRange("tourniquetSafeTicks",20*60*5,0, Integer.MAX_VALUE);
        TOURNIQUET_MUSCLE_DAMAGE=BUILDER
                .comment("Damage to the muscle health of the limb with a tourniquet after safe ticks have passed (s)")
                        .defineInRange("tourniquetMuscleDamage",1.5,0,100);
        MANUAL_SHRAPNEL_SUCCESS_CHANCE=BUILDER
                .comment("Chance for removing Shrapnell from Limb without use of Tweezers")
                        .defineInRange("manualShrapnelRemoveChance",0.3,0,1);
        DISLOCATION_FIX_CHANCE= BUILDER
                .comment("Chance for Manualy fixing a dislocation")
                        .defineInRange("manualDislocationFixChance",0.7,0,1);
        MAGICAL_HEAL_RATE = BUILDER
                .comment("the Heal Scalar of magical healing(potions,regenration)")
                .defineInRange("magicalHealRate",0.5,0,Double.MAX_VALUE);


        TOGGLE_UNCONTIOUS_INVENTORY = BUILDER
                .comment("On/Off for other players opening uncontious players inventory")
                        .define("toggleInventorySteal",false);

        CONS_PENALTY_PER_OPIOID = BUILDER
                .comment("How much contiousness is penalized by one point of Opiodis (optiod max is 100)")
                        .defineInRange("consPenaltyPerOpiod",0.2,0,Double.MAX_VALUE);
        CONSIOUSNESS_DELTA = BUILDER
                .comment("How fast contiousness resores itself(keep in mind that contiousness is also capped by things such as:")
                        .comment("Oxygen,Opioids,High Pain,Head Health ...")
                                .comment("(1 = instantly when it can ; 0.01 almost doesnt increase)")
                                        .defineInRange("consiousnessDelta",0.75,0.01,1);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
