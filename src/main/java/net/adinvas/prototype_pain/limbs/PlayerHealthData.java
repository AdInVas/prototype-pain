package net.adinvas.prototype_pain.limbs;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.hitbox.HitSector;
import net.adinvas.prototype_pain.item.IMedUsable;
import net.adinvas.prototype_pain.item.INarcotic;
import net.adinvas.prototype_pain.item.ModItems;
import net.adinvas.prototype_pain.network.MedicalAction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class PlayerHealthData {
    private final Map<Limb,LimbStatistics> limbStats = new EnumMap<>(Limb.class);
    private List<DelayedChangeEntry> changeEntries = new ArrayList<>();
    private float blood = 5f;
    private double totalPain = 0f;
    private float contiousness = 100f;
    private float contiousnessCap = 100f;
    private float hemothorax = 0f;
    private float hemothoraxpain = 0f;
    private float internalBleeding = 0f;
    private float Oxygen = 100f;
    private float OxygenCap = 100;
    private float Opioids = 0;
    private int BPM = 70;
    private boolean isBreathing = true;
    private boolean respitoryArrest = false;
    private float deathTimer = 0;

    //passtrough values
    private int hungerLevel = 20;
    private boolean isUnderwater = false;



    // Constants — ALL values now in per-tick units
    public final float INFECTION_RATE = (.25f) / 20f;           // 1% per second infection growth
    public final float DESINFECTION_RATE = (3.0f) / 20f;        // 3% per second healing from disinfection
    public final double WUND_ANTIBLEED_RATE = (0.02f) / 20f;   // L per second → per tick
    public final float INFECTION_CHANCE = (0.001f) / 20f;         // (5%) chance per second → per tick
    public final float INFECTION_MUSCLE_DRAIN = (0.4f) / 20f;   // % per second muscle health loss
    public final float HEMOTHORAX_HEAL_RATE = (0.1f) / 20f;     // points per second

    // Oxygen changes
    public final float OXYGEN_REPLENISH = (8f) / 20f;           // % per second
    public final float OXYGEN_DRAIN = (5f) / 20f;               // % per second

    // Blood regen & bleeding
    public final float BLOOD_REGEN_RATE = (0.001f) / 20f;       // L per second
    public final float MAX_BLEED_RATE = (0.03f) / 20f;          // L per second

    // Damage scaling
    public final float DAMAGE_SCALE = 5f;
    public final float PAIN_PER_DAMAGE = 5f;
    public final float OPIATE_PAIN_REDUCTION = 0.4f;
    public final float FRAC_DISL_FROM_MUSCLE_DAMAGE_CHANCE = 0.33f;
    public final int MAX_FRACT_DISL_TIME_T = 5*60*20;

    // Limb healing rates
    public final float NORMAL_LIMB_HEAL_RATE =  (0.04f) / 20f;    // % per second
    public final float BOOSTED_LIMB_HEAL_RATE = (0.125f) / 20f;   // % per second

    public final float MANUAL_SHRAPNEL_SUCCESS_CHANCE = 0.30f;
    public final float DISLOCATION_FIX_CHANCE = 0.7f;

    // Tourniquet behavior
    public final float TOURNIQUET_PAIN_PER_TICK = 1f / (20f);
    public final int TOURNIQUET_SAFE_TICKS = 20 * 60*5;                 // 60s before muscle damage starts
    public final float TOURNIQUET_MUSCLE_DAMAGE = 1.5f / 20f;         // 1.5 per second



    //1.8L/min = 0.03L/s

    public PlayerHealthData(){
        for (Limb limb : Limb.values()){
            limbStats.put(limb,new LimbStatistics());
        }
    }
    private LimbStatistics ensureLimb(Limb limb) {
        LimbStatistics stats = limbStats.get(limb);
        if (stats == null) {
            stats = new LimbStatistics();
            limbStats.put(limb, stats);
            PrototypePain.LOGGER.warn("PlayerHealthData: missing LimbStatistics for {} — created default", limb);
        }
        return stats;
    }

    public void setHungerLevel(int hunger) {
        this.hungerLevel = hunger;
    }

    public float getNutritionFactor() {
        // Scale from 0.0 to 1.0 based on hunger (20 max)
        return hungerLevel / 20f;
    }


    public float getLimbSkinHealth(Limb limb){
        return ensureLimb(limb).skinHealth;
    }

    public void setLimbSkinHealth(Limb limb,float health){
        ensureLimb(limb).skinHealth = health;
    }


    public float getLimbMuscleHealth(Limb limb) {
        return ensureLimb(limb).muscleHealth;
    }


    public void setLimbMuscleHealth(Limb limb, float health) {
        ensureLimb(limb).muscleHealth = health;
    }


    public float getLimbPain(Limb limb) {
        return ensureLimb(limb).finalPain;
    }


    public void setLimbPain(Limb limb, float pain) {
        ensureLimb(limb).pain = pain;
    }


    public void setLimbMinPain(Limb limb, float paintarget) {
        ensureLimb(limb).MinPain = paintarget;
    }


    public float getLimbInfection(Limb limb) {
        return ensureLimb(limb).infection;
    }


    public void setLimbInfection(Limb limb, float infection) {
        ensureLimb(limb).infection = infection;
    }



    public float getLimbFracture(Limb limb) {
        return ensureLimb(limb).fractureTimer;
    }


    public void setLimbFracture(Limb limb, float fracture) {
        ensureLimb(limb).fractureTimer = fracture;
    }


    public float isLimbDislocated(Limb limb) {
        return ensureLimb(limb).dislocatedTimer;
    }


    public void setLimbDislocation(Limb limb, float dislocation) {
        ensureLimb(limb).dislocatedTimer = dislocation;
    }


    public boolean hasLimbShrapnell(Limb limb) {
        return ensureLimb(limb).shrapnell;
    }


    public void setLimbShrapnell(Limb limb, boolean shrapnell) {
        ensureLimb(limb).shrapnell = shrapnell;
    }


    public float getLimbBleedRate(Limb limb) {
        Float val =ensureLimb(limb).bleedRate;
        if (val.isNaN()|| val.isInfinite()||val==null) {
            return 0;
        }
        return val;
    }


    public void setLimbBleedRate(Limb limb, float bleed) {
        ensureLimb(limb).bleedRate = bleed;
    }


    public float getLimbDesinfected(Limb limb) {
        return ensureLimb(limb).desinfectionTimer;
    }


    public void setLimbDesinfected(Limb limb, float desinfection) {
        ensureLimb(limb).desinfectionTimer = desinfection;
    }


    public boolean hasLimbSplint(Limb limb) {
        return ensureLimb(limb).hasSplint;
    }


    public void setLimbSplint(Limb limb, boolean Splint) {
        ensureLimb(limb).hasSplint = Splint;
    }


    public float getBloodVolume() {
        return blood;
    }


    public void setBloodVolume(float liters) {
        blood = liters;
    }


    public float getCombinedBleed() {
        float bleed_all= 0f;
        for (Limb limb : limbStats.keySet()) {
            LimbStatistics stats = limbStats.get(limb);
            if (!stats.Tourniquet && !isOppositeToChestUnderTourniquet(limb)) {
                bleed_all += stats.bleedRate;
            }
        }
        return bleed_all+internalBleeding;
    }

    public boolean isOppositeToChestUnderTourniquet(Limb limb) {
        switch (limb){
            case RIGHT_FOOT -> {
                return getTourniquet(Limb.RIGHT_LEG);
            }
            case LEFT_FOOT -> {
                return getTourniquet(Limb.LEFT_LEG);
            }
            case LEFT_HAND ->{
                return getTourniquet(Limb.LEFT_ARM);
            }
            case RIGHT_HAND ->{
                return getTourniquet(Limb.RIGHT_ARM);
            }
        }
        return false;
    }

    public float getContiousness() {
        return contiousness;
    }


    public void setContiousness(float value) {
        contiousness = value;
    }


    public void setContiousnessCap(float value) {
        contiousnessCap = value;
    }


    public void recalculateContiousness() {
        double finalcontiousness = Oxygen;
        if (totalPain>=100||limbStats.get(Limb.HEAD).muscleHealth<45){
            limbStats.get(Limb.HEAD).MuscleHeal = true;
            finalcontiousness = 0;
        }else if (totalPain>80){
            finalcontiousness += 80-totalPain;
        }
        finalcontiousness = Math.min(contiousnessCap,finalcontiousness);
        contiousness = (float) Math.max(0,Mth.lerp(0.5,contiousness,finalcontiousness));
    }


    public float getLungBlood() {
        return hemothorax;
    }


    public void setLungBlood(float value) {
        hemothorax = value;
    }


    public float getLungBloodRate() {
        return internalBleeding;
    }


    public void setLungBloodRate(float value) {
        internalBleeding = value;
    }


    public float getOpioids() {
        return Opioids;
    }


    public void setOpioids(float value) {
        Opioids = value;
    }


    public float getOxygen() {
        return Oxygen;
    }


    public void setOxygen(float value) {
        Oxygen = value;
    }


    public void setOxygenCap(float value) {
        OxygenCap = value;
    }


    public float getBPM() {
        return BPM;
    }


    public void setBPM(int value) {
        BPM = value;
    }

    public void setTourniquet(Limb limb,boolean value){
        ensureLimb(limb).Tourniquet = value;
    }
    public boolean getTourniquet(Limb limb){
        return ensureLimb(limb).Tourniquet;
    }

    public boolean isBreathing() {
        return isBreathing;
    }


    public void setBreathing(boolean value) {
        isBreathing = value;
    }


    public double getTotalPain() {
        return totalPain;
    }

    public void setisUnderwater(boolean val){
        isUnderwater=val;
    }

    public void recalcTotalPain() {
        totalPain = limbStats.values().stream().mapToDouble(ls -> ls.finalPain).max().orElse(0f);
    }

    private void UpdateLimb(Limb limb){
        LimbStatistics stats = limbStats.get(limb); // store once, reuse



        //MinpainCalculation

        stats.MinPain = ((stats.infection/100)*10)+(((stats.skinHealth-100)/-100)*15);

        //Healing
        if (stats.SkinHeal){
            stats.skinHealth += BOOSTED_LIMB_HEAL_RATE;
        }else {
            stats.skinHealth += NORMAL_LIMB_HEAL_RATE;
        }

        if (stats.MuscleHeal&&!stats.shrapnell&&stats.infection<=0){
            stats.muscleHealth += BOOSTED_LIMB_HEAL_RATE;
        }else if (!stats.shrapnell&&stats.infection<=0){
            stats.muscleHealth += NORMAL_LIMB_HEAL_RATE;
        }
        stats.skinHealth = Math.min(stats.skinHealth,100);
        stats.muscleHealth = Math.min(stats.muscleHealth,100);

        // Pain Adjustment
        float decay = 0.05f * (float)Math.pow(stats.pain / 30f, 1.2f);
        stats.pain = Math.max(stats.MinPain, stats.pain-decay);

        // Infection Adjustment
        if (stats.infection > 0) {
            if (stats.desinfectionTimer > 0) {
                stats.desinfectionTimer -= 1;
                stats.infection -= DESINFECTION_RATE;
            } else {
                stats.infection = Math.min(100, stats.infection + INFECTION_RATE);
            }
        }

        if (stats.skinHealth < 100 && stats.infection <= 0) {
            float chance = ((100-stats.skinHealth) / 100f) * INFECTION_CHANCE;
            if (Math.random() < chance) {
                stats.infection += 1;
            }
        }

        infectionSpread(limb);

        // Bleed Adjustment
        stats.bleedRate = Math.min(stats.bleedRate,MAX_BLEED_RATE*(Math.abs((stats.skinHealth-100)/100)));

        //Fract/Disl calculation
        stats.dislocatedTimer = Mth.clamp(
                stats.dislocatedTimer-1,0,MAX_FRACT_DISL_TIME_T
        );
        stats.fractureTimer = Mth.clamp(
                stats.fractureTimer-1,0,MAX_FRACT_DISL_TIME_T
        );


        if (stats.infection>=75){
            stats.muscleHealth -= INFECTION_MUSCLE_DRAIN;
        }


        if (stats.fractureTimer>0){
            stats.fractureTimer--;
            if (stats.hasSplint)
                stats.fractureTimer--;
        }

        if (stats.Tourniquet) {
            // Pain ramps up towards 40
            if (stats.pain < 40) {
                stats.pain = Math.min(40, stats.pain + TOURNIQUET_PAIN_PER_TICK);
            }

            // Timer ticks up
            stats.tourniquetTimer++;
            if (stats.tourniquetTimer > TOURNIQUET_SAFE_TICKS) {
                stats.muscleHealth = Math.max(0, stats.muscleHealth - TOURNIQUET_MUSCLE_DAMAGE);
                switch (limb) {
                    case LEFT_ARM -> limbStats.get(Limb.LEFT_HAND).muscleHealth =Math.max(0, limbStats.get(Limb.LEFT_HAND).muscleHealth - TOURNIQUET_MUSCLE_DAMAGE);
                    case RIGHT_ARM -> limbStats.get(Limb.RIGHT_HAND).muscleHealth =Math.max(0, limbStats.get(Limb.RIGHT_HAND).muscleHealth - TOURNIQUET_MUSCLE_DAMAGE);
                    case LEFT_LEG -> limbStats.get(Limb.LEFT_FOOT).muscleHealth =Math.max(0, limbStats.get(Limb.LEFT_FOOT).muscleHealth - TOURNIQUET_MUSCLE_DAMAGE);
                    case RIGHT_LEG ->limbStats.get(Limb.RIGHT_FOOT).muscleHealth =Math.max(0, limbStats.get(Limb.RIGHT_FOOT).muscleHealth - TOURNIQUET_MUSCLE_DAMAGE) ;
                }
            }
        } else {
            // Reset timer when removed
            stats.tourniquetTimer = 0;
        }
        stats.skinHealth = Mth.clamp(stats.skinHealth,0,100);
        stats.muscleHealth = Mth.clamp(stats.muscleHealth,0,100);
        stats.finalPain = stats.pain - Opioids * OPIATE_PAIN_REDUCTION;
    }



    private void infectionSpread(Limb limb){
        if (limbStats.get(limb).infection>75){
            float chance = (limbStats.get(limb).infection-75);
            if (Math.random()>chance){
                Limb conectedLimb = limb.randomFromConectedLimb();
                if (limbStats.get(conectedLimb).infection<=0){
                    limbStats.get(conectedLimb).infection++;
                }
            }
        }
    }

    public float painFromDamage(float damage){
        return damage * PAIN_PER_DAMAGE;
    }
    public void applyPain(Limb limb, float value){
        limbStats.get(limb).pain+= value;
    }
    private void applySkinDamage(Limb limb,float damage){
        limbStats.get(limb).skinHealth = Math.max(limbStats.get(limb).skinHealth-damage*DAMAGE_SCALE,0);
        limbStats.get(limb).SkinHeal = false;
        limbStats.get(limb).MuscleHeal = false;

    }
    private void applyMuscleDamage(Limb limb, float damage){
        if (limbStats.get(limb).muscleHealth<100&&damage>5){
            float bone_damage_chance = (100-limbStats.get(limb).muscleHealth)/100*FRAC_DISL_FROM_MUSCLE_DAMAGE_CHANCE;
            if (Math.random()>0.5){
                if (Math.random()<bone_damage_chance||damage>15){
                    setLimbFracture(limb,Math.max(getLimbFracture(limb),Math.max(1200,damage*500)));
                }
            }else {
                if (Math.random()<bone_damage_chance||damage>15){
                    setLimbDislocation(limb,Math.max(getLimbDislocated(limb),Math.max(1200,damage*300)));
                }
            }
        }
        limbStats.get(limb).muscleHealth =Math.max(limbStats.get(limb).muscleHealth-damage*DAMAGE_SCALE,0);
        limbStats.get(limb).SkinHeal = false;
        limbStats.get(limb).MuscleHeal = false;
    }
    private void applyBleedDamage(Limb limb, float damage){
        float bleed = (damage/15)* MAX_BLEED_RATE;
        limbStats.get(limb).bleedRate += bleed;
    }
    private void applyDirectBleedRate(Limb limb,float value){
        limbStats.get(limb).bleedRate = Mth.clamp(limbStats.get(limb).bleedRate-value,0,100);
    }

    public float getLimbDislocated(Limb limb){
            return ensureLimb(limb).dislocatedTimer;
    }

    public void tickUpdate(ServerPlayer player) {
        isUnderwater = player.isUnderWater();
        hungerLevel = player.getFoodData().getFoodLevel();
        isBreathing = true;

        // Death timer check
        if (deathTimer > 80) {
            player.hurt(player.damageSources().genericKill(), Float.MAX_VALUE);
            player.kill();
            return;
        }

        // Update each limb
        for (Limb limb : limbStats.keySet()) {
            UpdateLimb(limb);
        }

        recalcTotalPain();

        // Bleeding — internal
        if (internalBleeding > 0) {
            internalBleeding = (float) Math.max(0, internalBleeding - WUND_ANTIBLEED_RATE);
        }

        // Hemothorax
        hemothorax += internalBleeding;
        if (hemothorax > 0) {
            hemothoraxpain = (float) ((4.0 / 15.0) * hemothorax);
            hemothorax -= HEMOTHORAX_HEAL_RATE;
        }

        // Blood calculation
        float totalBleedPerTick = getCombinedBleed();
        blood -= totalBleedPerTick;
        contiousnessCap = 100;

        if (blood > 5.25) contiousnessCap = 80;

        if (blood > 5) {
            blood = Math.max(5, blood - (BLOOD_REGEN_RATE * getNutritionFactor()));
        } else if (blood < 5) {
            blood = Math.min(5, blood + (BLOOD_REGEN_RATE * getNutritionFactor()));
        }

        // Respiratory arrest condition
        respitoryArrest = Opioids > 100 || blood >= 5.7 || limbStats.get(Limb.CHEST).muscleHealth < 5||getTourniquet(Limb.HEAD);

        // Oxygen cap — based on blood volume and hemothorax
        OxygenCap = 100;
        if (blood < 4.375) {
            OxygenCap += (-2.0f / 3.0f) * hemothorax;
            OxygenCap += (160f / 3) * blood - (700f / 3);
            OxygenCap = Math.max(0, OxygenCap);
        }

        // Breathing & oxygen change
        if (isUnderwater || respitoryArrest) {
            isBreathing = false;
            Oxygen = Math.max(0, Oxygen - OXYGEN_DRAIN);
        }

        if (isBreathing) {
            Oxygen = Math.min(100, Oxygen + OXYGEN_REPLENISH);
            if (Oxygen > OxygenCap) Oxygen = OxygenCap;
        }

        // Opioid decay
        Opioids = Math.max(0, Opioids - 0.02f);

        // Consciousness
        recalculateContiousness();

        // Death timer adjustments
        if (Oxygen <= 0||blood<2.5) {
            deathTimer++;
        }
        if (Oxygen > 0 && deathTimer > 0) {
            deathTimer -= 0.5f;
        }
        applyPenalties(player);
        changeEntries.removeIf(entry -> {
            entry.reduceTicks(1); // decrement by 1 tick
            applyDirectBleedRate(entry.getLimb(), entry.getAmount_per_tick());
            return entry.getTicks() <= 0; // remove if done
        });
    }

    public void applyPenalties(ServerPlayer player){
        double baseMoveSpeed = 0.1;
        double baseAttackDamage = 1.0;
        double baseAttackSpeed = 4.0;

// Calculate movement multiplier from legs/feet
        double moveReduction =
                ((100 - getLimbMuscleHealth(Limb.RIGHT_LEG))  / 100.0) * 0.14 +
                        ((100 - getLimbMuscleHealth(Limb.LEFT_LEG))   / 100.0) * 0.14 +
                        ((100 - getLimbMuscleHealth(Limb.RIGHT_FOOT)) / 100.0) * 0.14 +
                        ((100 - getLimbMuscleHealth(Limb.LEFT_FOOT))  / 100.0) * 0.14;

// Calculate attack multiplier from arms/hands
        double attackMultiplier =
                (getLimbMuscleHealth(Limb.RIGHT_ARM)  / 100.0) * 0.25 +
                        (getLimbMuscleHealth(Limb.LEFT_ARM)   / 100.0) * 0.25 +
                        (getLimbMuscleHealth(Limb.RIGHT_HAND) / 100.0) * 0.25 +
                        (getLimbMuscleHealth(Limb.LEFT_HAND)  / 100.0) * 0.25;

// Apply attributes (scaling base value)
        double moveMultiplier = 1.0 - moveReduction;
        Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED))
                .setBaseValue(baseMoveSpeed * Math.max(0.0, moveMultiplier));

        Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE))
                .setBaseValue(baseAttackDamage * attackMultiplier);

        Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_SPEED))
                .setBaseValue(baseAttackSpeed * attackMultiplier);


        ItemStack offhand = player.getItemBySlot(EquipmentSlot.OFFHAND);
        ItemStack mainHand = player.getItemBySlot(EquipmentSlot.MAINHAND);


        boolean offHandBroken = getLimbMuscleHealth(Limb.LEFT_HAND)<10||getLimbFracture(Limb.LEFT_HAND)>0;
        boolean mainHandBroken = getLimbMuscleHealth(Limb.RIGHT_HAND)<10||getLimbFracture(Limb.RIGHT_HAND)>0;
        if (!offhand.isEmpty()&&offHandBroken){
                if (player.getInventory().add(offhand)) {
                    player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                } else {
                    player.drop(offhand, false);
                    player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
        }
        if (!mainHand.isEmpty()&&mainHandBroken){
            if (player.getInventory().add(mainHand)){
                player.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY);
            }else {
                player.drop(mainHand,false);
                player.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY);
            }
        }


      
    }

    public CompoundTag serializeNBT(CompoundTag nbt) {

        // Player-wide values
        nbt.putFloat("Blood", blood);
        nbt.putDouble("TotalPain", totalPain);
        nbt.putFloat("Consciousness", contiousness);
        nbt.putFloat("ConsciousnessCap", contiousnessCap);
        nbt.putFloat("Hemothorax", hemothorax);
        nbt.putFloat("HemothoraxPain", hemothoraxpain);
        nbt.putFloat("InternalBleeding", internalBleeding);
        nbt.putFloat("Oxygen", Oxygen);
        nbt.putFloat("OxygenCap", OxygenCap);
        nbt.putFloat("Opioids", Opioids);
        nbt.putInt("BPM", BPM);
        nbt.putBoolean("IsBreathing", isBreathing);

        ListTag changeList = new ListTag();
        for (DelayedChangeEntry entry:changeEntries){
            changeList.add(entry.toNBT());
        }
        nbt.put("ChangeList",changeList);

        // Serialize limb data as a list
        ListTag limbList = new ListTag();
        for (Map.Entry<Limb, LimbStatistics> entry : limbStats.entrySet()) {
            CompoundTag limbTag = new CompoundTag();
            limbTag.putString("LimbName", entry.getKey().name());

            LimbStatistics stats = entry.getValue();
            limbTag.putFloat("SkinHealth", stats.skinHealth);
            limbTag.putFloat("MuscleHealth", stats.muscleHealth);
            limbTag.putFloat("Pain", stats.pain);
            limbTag.putFloat("Infection", stats.infection);
            limbTag.putFloat("FractureTimer", stats.fractureTimer);
            limbTag.putFloat("Dislocated", stats.dislocatedTimer);
            limbTag.putBoolean("Shrapnell", stats.shrapnell);
            limbTag.putBoolean("HasSplint", stats.hasSplint);
            if ((Float)(stats.bleedRate)==null)stats.bleedRate=0;
            limbTag.putFloat("BleedRate", stats.bleedRate);
            limbTag.putFloat("DesinfectionTimer", stats.desinfectionTimer);
            limbTag.putFloat("MinPain", stats.MinPain);
            limbTag.putFloat("FinalPain",stats.finalPain);
            limbTag.putBoolean("SkinHeal",stats.SkinHeal);
            limbTag.putBoolean("MuscleHeal",stats.MuscleHeal);
            limbTag.putBoolean("Tourniquet",stats.Tourniquet);
            limbTag.putInt("TourniquetTime",stats.tourniquetTimer);
            limbList.add(limbTag);
        }
        nbt.put("LimbStats", limbList);

        return nbt;
    }




    public void copyFrom(PlayerHealthData other) {
        this.blood = other.blood;
        this.totalPain = other.totalPain;
        this.contiousness = other.contiousness;
        this.contiousnessCap = other.contiousnessCap;
        this.hemothorax = other.hemothorax;
        this.hemothoraxpain = other.hemothoraxpain;
        this.internalBleeding = other.internalBleeding;
        this.Oxygen = other.Oxygen;
        this.OxygenCap = other.OxygenCap;
        this.Opioids = other.Opioids;
        this.BPM = other.BPM;
        this.isBreathing = other.isBreathing;

        this.changeEntries.clear();
        for (DelayedChangeEntry entry: other.changeEntries){
            this.changeEntries.add(new DelayedChangeEntry(entry.getAmount_per_tick(),entry.getTicks(),entry.getLimb()));
        }

        // Assuming you have a Map<Limb, LimbStatistics> limbStats, copy each limb:
        this.limbStats.clear();
        for (Map.Entry<Limb, LimbStatistics> entry : other.limbStats.entrySet()) {
            Limb limb = entry.getKey();
            LimbStatistics originalStats = entry.getValue();
            LimbStatistics copiedStats = new LimbStatistics();

            // Copy all fields from originalStats to copiedStats
            copiedStats.skinHealth = originalStats.skinHealth;
            copiedStats.muscleHealth = originalStats.muscleHealth;
            copiedStats.pain = originalStats.pain;
            copiedStats.infection = originalStats.infection;
            copiedStats.fractureTimer = originalStats.fractureTimer;
            copiedStats.dislocatedTimer = originalStats.dislocatedTimer;
            copiedStats.shrapnell = originalStats.shrapnell;
            copiedStats.hasSplint = originalStats.hasSplint;
            copiedStats.bleedRate = originalStats.bleedRate;
            copiedStats.desinfectionTimer = originalStats.desinfectionTimer;
            copiedStats.MinPain = originalStats.MinPain;
            copiedStats.finalPain = originalStats.finalPain;
            copiedStats.SkinHeal = originalStats.SkinHeal;
            copiedStats.MuscleHeal = originalStats.MuscleHeal;
            copiedStats.Tourniquet = originalStats.Tourniquet;
            copiedStats.tourniquetTimer = originalStats.tourniquetTimer;

            this.limbStats.put(limb, copiedStats);
        }
    }

    public void deserializeNBT(CompoundTag nbt) {
        blood = nbt.getFloat("Blood");
        totalPain = nbt.getDouble("TotalPain");
        contiousness = nbt.getFloat("Consciousness");
        contiousnessCap = nbt.getFloat("ConsciousnessCap");
        hemothorax = nbt.getFloat("Hemothorax");
        hemothoraxpain = nbt.getFloat("HemothoraxPain");
        internalBleeding = nbt.getFloat("InternalBleeding");
        Oxygen = nbt.getFloat("Oxygen");
        OxygenCap = nbt.getFloat("OxygenCap");
        Opioids = nbt.getFloat("Opioids");
        BPM = nbt.getInt("BPM");
        isBreathing = nbt.getBoolean("IsBreathing");

        changeEntries.clear();
        ListTag changeList = nbt.getList("ChangeList",10);
        for(int i =0; i<changeList.size();i++){
            CompoundTag changeTag = changeList.getCompound(i);
            changeEntries.add(DelayedChangeEntry.fromNBT(changeTag));
        }
        limbStats.clear();
        ListTag limbList = nbt.getList("LimbStats", 10); // 10 = CompoundTag type
        for (int i = 0; i < limbList.size(); i++) {
            CompoundTag limbTag = limbList.getCompound(i);
            Limb limb = Limb.valueOf(limbTag.getString("LimbName"));
            LimbStatistics stats = new LimbStatistics();

            stats.skinHealth = limbTag.getFloat("SkinHealth");
            stats.muscleHealth = limbTag.getFloat("MuscleHealth");
            stats.pain = limbTag.getFloat("Pain");
            stats.infection = limbTag.getFloat("Infection");
            stats.fractureTimer = limbTag.getFloat("FractureTimer");
            stats.dislocatedTimer = limbTag.getFloat("Dislocated");
            stats.shrapnell = limbTag.getBoolean("Shrapnell");
            stats.hasSplint = limbTag.getBoolean("HasSplint");
            stats.bleedRate = limbTag.getFloat("BleedRate");
            stats.desinfectionTimer = limbTag.getFloat("DesinfectionTimer");
            stats.MinPain = limbTag.getFloat("MinPain");
            stats.finalPain = limbTag.getFloat("FinalPain");
            stats.SkinHeal = limbTag.getBoolean("SkinHeal");
            stats.MuscleHeal = limbTag.getBoolean("MuscleHeal");
            stats.Tourniquet = limbTag.getBoolean("Tourniquet");
            stats.tourniquetTimer = limbTag.getInt("TourniquetTime");
            if (Float.isNaN(stats.bleedRate))stats.bleedRate = 0;

            limbStats.put(limb, stats);
        }
    }


    public boolean tryUseItem(Limb limb, ItemStack itemstack, ServerPlayer source, ServerPlayer target,InteractionHand hand){
        if (itemstack.getItem() instanceof IMedUsable medItem){
            boolean used =  medItem.onMedicalUse(limb,source,target,itemstack,hand);
            PrototypePain.LOGGER.info("{} USED BY {} ON {} | {}",itemstack,source,target,used);
            return used;
        }
        return false;
    }

    public boolean tryUseItem(float value, ItemStack itemstack, ServerPlayer source, ServerPlayer target,InteractionHand hand){
        if (itemstack.getItem() instanceof INarcotic medItem){
            boolean used =  medItem.onMedicalUse(value,source,target,itemstack,hand);
            PrototypePain.LOGGER.info("{} USED BY {} ON {} | {}",itemstack,source,target,used);
            return used;
        }
        return false;
    }


    public void medicalAction(MedicalAction action, Limb limb, Player source){
        Random random = new Random();
        switch (action){
            case TRY_SHRAPNEL -> {
                applyPain(limb,10);
                int damage = random.nextInt(3);
                applySkinDamage(limb,damage);
                applyMuscleDamage(limb,damage);
                applyBleedDamage(limb,damage/6f);
                if (random.nextFloat()<=MANUAL_SHRAPNEL_SUCCESS_CHANCE){
                    setLimbShrapnell(limb,false);
                }
            }
            case REMOVE_SPLINT -> {
                setLimbSplint(limb,false);
            }
            case FIX_DISLOCATION -> {
                applyPain(limb,30);
                if (random.nextFloat()<=DISLOCATION_FIX_CHANCE){
                    setLimbDislocation(limb,Math.max(isLimbDislocated(limb)-20,0));
                }
            }
            case REMOVE_TOURNIQUET -> {
                limbStats.get(limb).Tourniquet = false;
                source.getInventory().add(new ItemStack(ModItems.Tourniquet.get()));
            }
        }
    }

    public float getContiousnessCap() {
        return contiousnessCap;
    }

    public float getOxygenCap() {
        return OxygenCap;
    }



    public void handleFallDamage(float damageValue){
        Random random = new Random();
        float remainingDamage = damageValue * 1;


        float[][] stages = {
                {0.6f, 0.0f, 0.0f},   // stage 1: feet only
                {0.4f, 0.6f, 0.0f},   // stage 2: feet + legs
                {0.2f, 0.4f, 0.2f},   // stage 3
                {0.1f, 0.2f, 0.4f},   // stage 4
                {0.1f, 0.1f, 0.4f},   // stage 5
                {0.1f, 0.1f, 0.6f},   // stage 6
                {0.1f, 0.1f, 0.8f},   // stage 7
                {0.1f, 0.1f, 1.0f}    // stage 8: always random damage
        };

        for (float[] stage : stages) {
            float footMult = stage[0];
            float legMult  = stage[1];
            float randChance = stage[2];

            // feet
            if (footMult > 0f) {
                applyMuscleDamage(Limb.LEFT_FOOT,  remainingDamage * footMult);
                applyPain(Limb.LEFT_FOOT,  painFromDamage(remainingDamage * footMult));
                applyMuscleDamage(Limb.RIGHT_FOOT,  remainingDamage * footMult);
                applyPain(Limb.RIGHT_FOOT,  painFromDamage(remainingDamage * footMult));
            }

            // legs
            if (legMult > 0f) {
                applyMuscleDamage(Limb.LEFT_LEG,  remainingDamage * legMult);
                applyPain(Limb.LEFT_LEG,  painFromDamage(remainingDamage * legMult));
                applyMuscleDamage(Limb.RIGHT_LEG,  remainingDamage * legMult);
                applyPain(Limb.RIGHT_LEG,  painFromDamage(remainingDamage * legMult));
            }

            // random damage
            if (randChance > 0f && random.nextFloat() < randChance) {
                handleRandomDamage(remainingDamage * 0.5f);
            }

            // decay for next pass
            remainingDamage *= 0.7f;
            if (remainingDamage < 1f) return;
        }

    }

    public void handleExplosionDamage(){

    }

    public void handleProjectileDamage(HitSector hitSector) {

    }

    public void handleRandomDamage(float damageValue) {
        applyRecursiveRandomDamage(damageValue, null, 0);
    }

    private void applyRecursiveRandomDamage(float damage, Limb previousLimb, int depth) {
        Random random = new Random();
        if (damage < 1f) return;                 // too small → stop
        if (depth > 6) return;                   // hard cap so it never goes infinite

        // Pick a limb (first = random, then connected)
        Limb target = (previousLimb == null) ? Limb.weigtedRandomLimb() : previousLimb.randomFromConectedLimb();

        // Decide: Muscle OR Skin
        if (random.nextBoolean()) {
            // Muscle damage only
            applyMuscleDamage(target, damage * 0.4f);
        } else {
            // Skin damage (with bleed chance)
            applySkinDamage(target, damage * 0.5f);

            if (random.nextFloat() < 0.6f) { // 60% chance for bleed
                applyBleedDamage(target, damage * 0.3f);
            }
        }

        // Pain always applied
        applyPain(target, painFromDamage(damage * 0.25f));

        // Recursive spread: chance grows with damage
        float spreadChance = Math.min(0.9f, damage / 15f);
        if (random.nextFloat() < spreadChance) {
            // Spread to a connected limb with reduced strength
            applyRecursiveRandomDamage(damage * 0.6f, target, depth + 1);
        }
    }


    public void addDelayedChange(float totalBleedAmount,int timeInTicks,Limb limb){
        float bleed = totalBleedAmount/timeInTicks;
        changeEntries.add(new DelayedChangeEntry(bleed,timeInTicks,limb));
    }

}
