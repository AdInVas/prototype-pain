package net.adinvas.prototype_pain.limbs;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.EnumMap;
import java.util.Map;

public class PlayerHealthData {
    private final Map<Limb,LimbStatistics> limbStats = new EnumMap<>(Limb.class);
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



    //Constants
    private final float INFECTION_RATE = 0.05f;// % per t
    private final float DESINFECTION_RATE = 0.15f;// % per t
    private final float WUND_ANTIBLEED_RATE = 0.002f; //L per s
    private final float INFECTION_CHANCE = 0.025f;//%
    private final float INFECTION_MUSCLE_DRAIN = 0.02f;// % per t
    private final float LIMB_HEAL_RATE = 0.02f;// % per t
    private final float HEMOTHORAX_HEAL_RATE = 0.005f;//pts per tick
    private final float OXYGEN_REPLENISH = 2f;//% per s
    private final float OXYGEN_DRAIN = 1f;//% per s
    private final float BLOOD_REGEN_RATE= 0.002f;//L per s

    //1.8L/min = 0.03L/s

    public PlayerHealthData(){
        for (Limb limb : Limb.values()){
            limbStats.put(limb,new LimbStatistics());
        }
    }
    public void setHungerLevel(int hunger) {
        this.hungerLevel = hunger;
    }

    public float getNutritionFactor() {
        // Scale from 0.0 to 1.0 based on hunger (20 max)
        return hungerLevel / 20f;
    }


    public float getLimbSkinHealth(Limb limb){
        return limbStats.get(limb).skinHealth;
    }

    public void setLimbSkinHealth(Limb limb,float health){
        limbStats.get(limb).skinHealth = health;
    }


    public float getLimbMuscleHealth(Limb limb) {
        return limbStats.get(limb).muscleHealth;
    }


    public void setLimbMuscleHealth(Limb limb, float health) {
        limbStats.get(limb).muscleHealth = health;
    }


    public float getLimbPain(Limb limb) {
        return limbStats.get(limb).pain;
    }


    public void setLimbPain(Limb limb, float pain) {
        limbStats.get(limb).pain = pain;
    }


    public void setLimbTargetPain(Limb limb, float paintarget) {
        limbStats.get(limb).targetPain = paintarget;
    }


    public float getLimbInfection(Limb limb) {
        return limbStats.get(limb).infection;
    }


    public void setLimbInfection(Limb limb, float infection) {
        limbStats.get(limb).infection = infection;
    }



    public float getLimbFracture(Limb limb) {
        return limbStats.get(limb).fractureTimer;
    }


    public void setLimbFracture(Limb limb, float fracture) {
        limbStats.get(limb).fractureTimer = fracture;
    }


    public boolean isLimbDislocated(Limb limb) {
        return limbStats.get(limb).dislocated;
    }


    public void setLimbDislocation(Limb limb, boolean dislocation) {
        limbStats.get(limb).dislocated = dislocation;
    }


    public boolean hasLimbShrapnell(Limb limb) {
        return limbStats.get(limb).shrapnell;
    }


    public void setLimbShrapnell(Limb limb, boolean shrapnell) {
        limbStats.get(limb).shrapnell = shrapnell;
    }


    public float getLimbBleedRate(Limb limb) {
        return limbStats.get(limb).bleedRate;
    }


    public void setLimbBleedRate(Limb limb, float bleed) {
        limbStats.get(limb).bleedRate = bleed;
    }


    public float getLimbDesinfected(Limb limb) {
        return limbStats.get(limb).desinfectionTimer;
    }


    public void setLimbDesinfected(Limb limb, float desinfection) {
        limbStats.get(limb).desinfectionTimer = desinfection;
    }


    public boolean hasLimbSplint(Limb limb) {
        return limbStats.get(limb).hasSplint;
    }


    public void setLimbSplint(Limb limb, boolean Splint) {
        limbStats.get(limb).hasSplint = Splint;
    }


    public float getBloodVolume() {
        return blood;
    }


    public void setBloodVolume(float liters) {
        blood = liters;
    }


    public float getCombinedBleed() {
        float bleed_all= 0f;
        for (Limb limb: limbStats.keySet()){
            bleed_all += limbStats.get(limb).bleedRate;
        }
        return bleed_all+internalBleeding;
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
        totalPain = limbStats.values().stream().mapToDouble(ls -> ls.pain).max().orElse(0f);
    }

    private void UpdateLimb(Limb limb){
        LimbStatistics stats = limbStats.get(limb); // store once, reuse

        // Pain Adjustment
        stats.pain = (float) Mth.lerp(0.02, stats.pain, stats.targetPain);

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
            float chance = (stats.skinHealth / 100f) * INFECTION_CHANCE;
            if (Math.random() < chance) {
                stats.infection += 1;
            }
        }

        infectionSpread(limb);

        // Bleed Adjustment
        if (!stats.shrapnell && stats.bleedRate > 0) {
            stats.bleedRate = Math.max(stats.bleedRate - WUND_ANTIBLEED_RATE / 20, 0);
            if (stats.skinHealth >= 100)
                stats.bleedRate = 0;
        }

        //Health Adjustement
        if (stats.muscleHealth<100&&stats.infection<75){
            stats.muscleHealth = Math.min(100,stats.muscleHealth+LIMB_HEAL_RATE);
        }
        if (stats.infection>=75){
            stats.muscleHealth -= INFECTION_MUSCLE_DRAIN;
        }
        if (stats.skinHealth<100){
            stats.skinHealth = Math.min(100,stats.skinHealth+LIMB_HEAL_RATE);
        }

        if (stats.fractureTimer>0){
            stats.fractureTimer--;
            if (stats.hasSplint)
                stats.fractureTimer--;
        }
    }

    private void infectionSpread(Limb limb){
        if (limbStats.get(limb).infection>75){
            float chance = (limbStats.get(limb).infection-75);
            switch (limb){
                case HEAD -> {
                    if (limbStats.get(Limb.CHEST).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.CHEST).infection +=1;
                        }
                    }
                }
                case CHEST -> {
                    if (limbStats.get(Limb.LEFT_ARM).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.LEFT_ARM).infection +=1;
                        }
                    }
                    if (limbStats.get(Limb.RIGHT_ARM).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.RIGHT_ARM).infection +=1;
                        }
                    }
                    if (limbStats.get(Limb.LEFT_LEG).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.LEFT_LEG).infection +=1;
                        }
                    }
                    if (limbStats.get(Limb.RIGHT_LEG).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.RIGHT_LEG).infection +=1;
                        }
                    }
                }
                case LEFT_ARM -> {
                    if (limbStats.get(Limb.LEFT_HAND).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.LEFT_HAND).infection +=1;
                        }
                    }
                    if (limbStats.get(Limb.CHEST).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.CHEST).infection +=1;
                        }
                    }
                }
                case RIGHT_ARM -> {
                    if (limbStats.get(Limb.RIGHT_HAND).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.RIGHT_HAND).infection +=1;
                        }
                    }
                    if (limbStats.get(Limb.CHEST).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.CHEST).infection +=1;
                        }
                    }
                }
                case LEFT_LEG -> {
                    if (limbStats.get(Limb.LEFT_FOOT).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.LEFT_FOOT).infection +=1;
                        }
                    }
                    if (limbStats.get(Limb.CHEST).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.CHEST).infection +=1;
                        }
                    }
                }
                case RIGHT_LEG -> {
                    if (limbStats.get(Limb.RIGHT_FOOT).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.RIGHT_FOOT).infection +=1;
                        }
                    }
                    if (limbStats.get(Limb.CHEST).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.CHEST).infection +=1;
                        }
                    }
                }
                case RIGHT_HAND -> {
                    if (limbStats.get(Limb.RIGHT_ARM).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.RIGHT_ARM).infection +=1;
                        }
                    }
                }
                case LEFT_HAND -> {
                    if (limbStats.get(Limb.LEFT_ARM).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.LEFT_ARM).infection +=1;
                        }
                    }
                }
                case RIGHT_FOOT -> {
                    if (limbStats.get(Limb.RIGHT_FOOT).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.RIGHT_FOOT).infection +=1;
                        }
                    }
                }
                case LEFT_FOOT -> {
                    if (limbStats.get(Limb.LEFT_FOOT).infection<=0){
                        if (Math.random()<chance){
                            limbStats.get(Limb.LEFT_FOOT).infection +=1;
                        }
                    }
                }
            }
        }
    }

    public void applyPainModifiers() {
        if (Opioids > 0) {
            // Reduce all limb pain proportionally to opioid strength
            for (LimbStatistics stats : limbStats.values()) {
                float painReduction = (Opioids * 0.1f); // tune multiplier
                stats.pain = Math.max(0, stats.pain - painReduction);
            }
            // Decay opioid effect over time
            Opioids = Math.max(0, Opioids - 0.05f); // tune decay rate
        }
    }

    public void tickUpdate(ServerPlayer player) {
        isUnderwater = player.isUnderWater();
        hungerLevel = player.getFoodData().getFoodLevel();
        isBreathing= true;
        if (deathTimer>200){
            player.hurt(player.damageSources().genericKill(),Float.MAX_VALUE);
            return;
        }
        for (Limb limb:limbStats.keySet()){
            UpdateLimb(limb);
        }
        applyPainModifiers();
        recalcTotalPain();
        //bleeding
        if (internalBleeding>0){
            internalBleeding = Math.max(0,internalBleeding-WUND_ANTIBLEED_RATE/20);
        }


        //Hemothorax
        hemothorax += internalBleeding;
        if(hemothorax>0){
            hemothoraxpain = (float) ((4.0/15.0)* hemothorax);
            hemothorax -= HEMOTHORAX_HEAL_RATE;
        }


        //Blood calc
        float total_bleed_t = getCombinedBleed()/20;
        blood -= total_bleed_t;
            contiousnessCap = 100;
        if (blood>5.25)
            contiousnessCap = 80;

        if (blood>5){
            blood = Math.max(5,blood-((BLOOD_REGEN_RATE/20)*getNutritionFactor()));
        } else if (blood<5) {
            blood = Math.min(5,blood+((BLOOD_REGEN_RATE/20)*getNutritionFactor()));
        }

        respitoryArrest = Opioids > 100 || blood >= 5.7 || limbStats.get(Limb.CHEST).muscleHealth < 5;


        //OxygenCap
        OxygenCap = 100;
        if (blood<4.375){
            OxygenCap += (-2.0f/3.0f) * hemothorax;
            OxygenCap += (160f/3) * blood - (700f/3);
            OxygenCap = Math.max(0,OxygenCap);
        }


        if (isUnderwater||respitoryArrest){
            isBreathing=false;
            Oxygen = Math.max(0,Oxygen- OXYGEN_DRAIN/20);
        }
        //Oxygen
        if (isBreathing){
            Oxygen = Math.min(100,Oxygen+OXYGEN_REPLENISH/20);
            if (Oxygen>OxygenCap)Oxygen=OxygenCap;
        }



        recalculateContiousness();
        if (Oxygen<=0){
            deathTimer++;
        }
        if (Oxygen>0&&deathTimer>0){
            deathTimer -= 0.5f;
        }
        PrototypePain.LOGGER.info("Blood {}, Oxygen {},Contoiusness {}",blood,Oxygen,contiousness);
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
            limbTag.putBoolean("Dislocated", stats.dislocated);
            limbTag.putBoolean("Shrapnell", stats.shrapnell);
            limbTag.putBoolean("HasSplint", stats.hasSplint);
            limbTag.putFloat("BleedRate", stats.bleedRate);
            limbTag.putFloat("DesinfectionTimer", stats.desinfectionTimer);
            limbTag.putFloat("TargetPain", stats.targetPain);

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
            copiedStats.dislocated = originalStats.dislocated;
            copiedStats.shrapnell = originalStats.shrapnell;
            copiedStats.hasSplint = originalStats.hasSplint;
            copiedStats.bleedRate = originalStats.bleedRate;
            copiedStats.desinfectionTimer = originalStats.desinfectionTimer;
            copiedStats.targetPain = originalStats.targetPain;

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
            stats.dislocated = limbTag.getBoolean("Dislocated");
            stats.shrapnell = limbTag.getBoolean("Shrapnell");
            stats.hasSplint = limbTag.getBoolean("HasSplint");
            stats.bleedRate = limbTag.getFloat("BleedRate");
            stats.desinfectionTimer = limbTag.getFloat("DesinfectionTimer");
            stats.targetPain = limbTag.getFloat("TargetPain");

            limbStats.put(limb, stats);
        }
    }
}
