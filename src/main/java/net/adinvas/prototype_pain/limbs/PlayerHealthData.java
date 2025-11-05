package net.adinvas.prototype_pain.limbs;

import net.adinvas.prototype_pain.ModDamageTypes;
import net.adinvas.prototype_pain.ModGamerules;
import net.adinvas.prototype_pain.ModSounds;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.config.ServerConfig;
import net.adinvas.prototype_pain.hitbox.HitSector;
import net.adinvas.prototype_pain.item.ISimpleMedicalUsable;
import net.adinvas.prototype_pain.item.ModItems;
import net.adinvas.prototype_pain.network.MedicalAction;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.adinvas.prototype_pain.network.TriggerLastStandPacket;
import net.adinvas.prototype_pain.tags.ModItemTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.Tags;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.BooleanUtils;

import java.util.*;
import java.util.List;

public class PlayerHealthData {
    private Map<Limb,LimbStatistics> limbStats = new EnumMap<>(Limb.class);
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
    private float PendingOpioids=0;
    private float BPM = 70;
    private boolean isBreathing = true;
    private boolean respitoryArrest = false;
    private float bloodViscosity = 0;
    private float adrenaline = 0;
    private int lifeSupportTimer= 0;


    private float immunity=100;
    private float antibiotic_timer=0;
    private float drug_addition = 0;
    private float brainHealth =100;
    private float Shock =0;
    private float dirtyness = 0;
    private float temperature = 36.6f;
    private float hearingLoss = 0;
    private float flashHearingLoss = 0;

    private boolean leftEyeBlind = false;
    private boolean RightEyeBlind= false;
    private boolean isMouthRemoved = false;

    private boolean LastStand = false;


    private float painscale = 1;
    /*
    TODO: - Make:
        -MAKE Amputations
     */


    public String baseToString() {
        return "PlayerHealthData{" +
                "blood=" + blood +
                ", totalPain=" + totalPain +
                ", contiousness=" + contiousness +
                ", contiousnessCap=" + contiousnessCap +
                ", hemothorax=" + hemothorax +
                ", hemothoraxpain=" + hemothoraxpain +
                ", internalBleeding=" + internalBleeding +
                ", oxygen=" + Oxygen +
                ", oxygenCap=" + OxygenCap +
                ", opioids=" + Opioids +
                ", bpm=" + BPM +
                ", isBreathing=" + isBreathing +
                ", respiratoryArrest=" + respitoryArrest +
                ", bloodViscosity=" + bloodViscosity +
                ", immunity=" + immunity +
                ", drug_addiction=" + drug_addition +
                ", brainHealth=" + brainHealth +
                ", Shock=" + Shock +
                ", dirtyness=" + dirtyness +
                ", temperature=" + temperature +
                ", hearingLoss=" + hearingLoss +
                ", adrenalone=" + adrenaline +
                '}';
    }

    //passtrough values
    private int hungerLevel = 20;
    private boolean isUnderwater = false;


    public void setAntibiotic_timer(float antibiotic_timer) {
        this.antibiotic_timer = antibiotic_timer;
    }

    public float getDrug_addition() {
        return drug_addition;
    }

    public void setDrug_addition(float drug_addition) {
        this.drug_addition = drug_addition;
    }

    public float getAntibiotic_timer() {
        return antibiotic_timer;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void setLeftEyeBlind(boolean leftEyeBlind) {
        this.leftEyeBlind = leftEyeBlind;
    }

    public void setRightEyeBlind(boolean rightEyeBlind) {
        RightEyeBlind = rightEyeBlind;
    }

    public void setMouthRemoved(boolean mouthRemoved) {
        isMouthRemoved = mouthRemoved;
    }

    public void setFlashHearingLoss(float flashHearingLoss) {
        this.flashHearingLoss = flashHearingLoss;
    }

    public float getFlashHearingLoss() {
        return flashHearingLoss;
    }


    public float getTemperature() {
        return temperature;
    }

    public void setDirtyness(float dirtyness) {
        this.dirtyness = dirtyness;
    }

    public float getDirtyness() {
        return dirtyness;
    }

    public float getAdrenaline() {
        return adrenaline;
    }

    public void setAdrenaline(float adrenaline) {
        this.adrenaline = adrenaline;
    }

    public float getBrainHealth() {
        return brainHealth;
    }

    public float getImmunity() {
        return immunity;
    }

    public int getLifeSupportTimer() {
        return lifeSupportTimer;
    }

    public void setLifeSupportTimer(int lifeSupportTimer) {
        this.lifeSupportTimer = lifeSupportTimer;
    }

    public void setBrainHealth(float brainHealth) {
        this.brainHealth = brainHealth;
    }

    public void setImmunity(float immunity) {
        this.immunity = immunity;
    }

    public void setShock(float shock) {
        Shock = shock;
    }

    public boolean isLeftEyeBlind() {
        return leftEyeBlind;
    }

    public boolean isRightEyeBlind() {
        return RightEyeBlind;
    }

    public boolean isMouthRemoved() {
        return isMouthRemoved;
    }

    public float getHearingLoss() {
        return hearingLoss;
    }

    public void setHearingLoss(float hearingLoss) {
        this.hearingLoss = hearingLoss;
    }

    public float getShock() {
        return Shock;
    }

   public boolean getPERNAMENT_DAMAGE(){
        return ServerConfig.PERNAMENT_DAMAGE.get();
   }

    public float getBRAIN_DRAIN(){
        return (float) (ServerConfig.BRAIN_DRAIN.get()/20f);
    }

    public float getBRAIN_REGEN_RATE(){
        return (float) (ServerConfig.BRAIN_HEALTH_REGEN.get()/20f/60f);
    }
    public float getDISINFECTION_STRENGTH(){
        return (float) (ServerConfig.DISINFECTION_SCALE.get()/20f);
    }
    public double getWUND_ANTIBLEED_RATE(){
        return ServerConfig.WUND_ANTIBLEED_RATE.get()/20/60;
    }
    public float getINFECTION_CHANCE(){
        return (float) (ServerConfig.INFECTION_CHANCE.get()/20f);
    }
    public float getINFECTION_MUSCLE_DRAIN(){
        return (float) (ServerConfig.INFECTION_MUSCLE_DRAIN.get()/20f);
    }
    public float getHEMOTHORAX_HEAL_RATE(){
        return (float) (ServerConfig.HEMOTHORAX_HEAL_RATE.get()/20f);
    }
    public float getOXYGEN_REPLENISH(){
        return (float) (ServerConfig.OXYGEN_REPLENISH.get()/20f);
    }
    public float getOXYGEN_DRAIN(){
        return (float) (ServerConfig.OXYGEN_DRAIN.get()/20f);
    }
    public float getBLOOD_REGEN_RATE(){
        return (float) (ServerConfig.BLOOD_REGEN_RATE.get()/20f);
    }
   public float getMAX_BLEED_RATE(){
        return (float) (ServerConfig.MAX_BLEED_RATE.get()/20f);
   }
   public float getBLOOD_VISCOSITY_REGEN(){
        return (float) (ServerConfig.BLOOD_VISCOSITY_REGEN.get()/20f);
   }
   public double getDAMAGE_SCALE(){
        return ServerConfig.DAMAGE_SCALE.get();
   }
   public double getPAIN_PER_DAMAGE(){
        return ServerConfig.PAIN_PER_DAMAGE.get();
   }
   public double getOPIATE_PAIN_REDUCTION(){
        return ServerConfig.OPIATE_PAIN_REDUCTION.get();
   }
   public double getFRAC_DISL_FROM_MUSCLE_DAMAGE_CHANCE(){
        return ServerConfig.FRAC_DISL_FROM_MUSCLE_DAMAGE_CHANCE.get();
   }
   public double getFRACTURE_HEAL(){
        return ServerConfig.FRACTURE_HEAL_RATE.get()/20d;
   }
   public double getDISLOCATION_HEAL(){
        return ServerConfig.DISLOCATION_HEAL_RATE.get()/20d;
   }
   public double getContiousnessPerOpioid(){
        return ServerConfig.CONS_PENALTY_PER_OPIOID.get();
   }
   public float getNORMAL_LIMB_HEAL_RATE(){
        return (float) (ServerConfig.NORMAL_LIMB_HEAL_RATE.get()/20f);
   }
   public float getBOOSTED_LIMB_HEAL_RATE(){
        return (float) (ServerConfig.BOOSTED_LIMB_HEAL_RATE.get()/20f);
   }
   public double getMANUAL_SHRAPNEL_SUCCESS_CHANCE(){
        return ServerConfig.MANUAL_SHRAPNEL_SUCCESS_CHANCE.get();
   }
   public double getDISLOCATION_FIX_CHANCE(){
        return ServerConfig.DISLOCATION_FIX_CHANCE.get();
   }
   public float getTOURNIQUET_PAIN_PER_TICK(){
        return (float) (ServerConfig.TOURNIQUET_PAIN_PER_TICK.get()/1f);
   }
   public int getTOURNIQUET_SAFE_TICKS(){
        return ServerConfig.TOURNIQUET_SAFE_TICKS.get();
   }
   public float getTOURNIQUET_MUSCLE_DAMAGE(){
        return (float) (ServerConfig.TOURNIQUET_MUSCLE_DAMAGE.get()/20f);
   }
   public float getMAGICAL_HEAL(){
        return (float) (ServerConfig.MAGICAL_HEAL_RATE.get()/1f);
   }
   public double getContiousnessregen(){
        return ServerConfig.CONSIOUSNESS_REGEN.get();
   }
   public double getIMMUNITY_STRENGTH(){
        return ServerConfig.IMMUNITY_SCALE.get();
   }
   public boolean getDO_TEMPERATURE_CHANGE(){
        return ServerConfig.DO_TEMP_SCALE.get();
   }
   public double[] getArmorScaling(){
        double[] list = new double[4];
        list[0] = ServerConfig.HELMET_ARMOR_SCALE.get();
       list[1] = ServerConfig.CHESTPLATE_ARMOR_SCALE.get();
       list[2] = ServerConfig.LEGS_ARMOR_SCALE.get();
       list[3] = ServerConfig.BOOTS_ARMOR_SCALE.get();
       return list;
   }



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
        ensureLimb(limb).skinHealth = Mth.clamp(health,0,100);
    }


    public float getLimbMuscleHealth(Limb limb) {
        return ensureLimb(limb).muscleHealth;
    }


    public void setLimbMuscleHealth(Limb limb, float health) {
        ensureLimb(limb).muscleHealth = Mth.clamp(health,0,100);
    }


    public float getLimbPain(Limb limb) {
        return ensureLimb(limb).finalPain;
    }


    public void setLimbPain(Limb limb, float pain) {
        ensureLimb(limb).pain = Mth.clamp(pain,0,999);
    }


    public void setLimbMinPain(Limb limb, float paintarget) {
        ensureLimb(limb).MinPain = paintarget;
    }


    public float getLimbInfection(Limb limb) {
        return ensureLimb(limb).infection;
    }


    public void setLimbInfection(Limb limb, float infection) {
        ensureLimb(limb).infection = Mth.clamp(infection,0,100);
    }



    public float getLimbFracture(Limb limb) {
        return ensureLimb(limb).fracture;
    }


    public void setLimbFracture(Limb limb, float fracture) {
        ensureLimb(limb).fracture = Mth.clamp(fracture,0,100);
    }


    public float isLimbDislocated(Limb limb) {
        return ensureLimb(limb).dislocation;
    }


    public void setLimbDislocation(Limb limb, float dislocation) {
        ensureLimb(limb).dislocation = Mth.clamp(dislocation,0,100);
    }


    public int hasLimbShrapnell(Limb limb) {
        return ensureLimb(limb).shrapnell;
    }

    public void setLimbShrapnell(Limb limb, int shrapnell) {
        ensureLimb(limb).shrapnell = Math.min(shrapnell,5);
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

    public void setInternalBleeding(float internalBleeding) {
        this.internalBleeding = internalBleeding;
    }

    public float getInternalBleeding() {
        return internalBleeding;
    }

    public void setHemothorax(float hemothorax) {
        this.hemothorax = hemothorax;
    }

    public float getHemothorax() {
        return hemothorax;
    }

    public float getLimbDesinfected(Limb limb) {
        return ensureLimb(limb).desinfectionTimer;
    }


    public void setLimbDesinfected(Limb limb, float desinfection) {
        ensureLimb(limb).desinfectionTimer = desinfection;
    }

    public Component getLimbDataText(Limb limb){
        return Component.literal(limbStats.get(limb).toString());
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
        if (limbStats==null){
            return 0;
        }
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


    public void recalculateConsciousness() {
        // Base consciousness target from oxygen
        double target = Oxygen;
        boolean hard_cut= false;
        // Hard knockout conditions
        if (Shock > 0.66) {
            target = 0;
            hard_cut=true;
        }
        // Pain reduces target consciousness
        else if (totalPain > 50) {
            target += (50 - totalPain);
        }

        // Clamp to max cap
        target = Math.min(contiousnessCap, target);

        // Calculate difference
        double diff = target - contiousness;

        // Apply regeneration rate (getContiousnessRegen = %/s)
        float regenPerSecond = (float) getContiousnessregen(); // e.g. 0.5 means 0.5% per second
        float regenPerTick = regenPerSecond / 20f;     // assuming 20 ticks per second

        // Smoothly move toward target
        if (Math.abs(diff) > regenPerTick&&!hard_cut&&diff>0) {
            contiousness += Math.signum(diff) * regenPerTick;
        } else {
            contiousness = (float) target; // close enough, snap to target
        }

        // Clamp final value
        contiousness = Mth.clamp(contiousness, 0f, contiousnessCap);
    }


    public float getLungBlood() {
        return hemothorax;
    }


    public void setLungBlood(float value) {
        hemothorax = value;
    }


    public float getPendingOpioids() {
        return PendingOpioids;
    }

    public float getOpioids() {
        return Opioids;
    }

    public void setPendingOpioids(float value) {
        PendingOpioids = value;
    }

    public void setOpioids(float va){
        Opioids = va;
    }

    public float getNetOpiodids(){
        return Opioids-drug_addition;
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

    public void setlimbAmputated(Limb limb,boolean value){
        isReducedDirty=true;
        ensureLimb(limb).amputated = value;
    }
    public boolean isAmputated(Limb limb){
        return ensureLimb(limb).amputated;
    }

    public void dismember(Limb limb){
        switch (limb){
            case LEFT_ARM -> {
                setlimbAmputated(Limb.LEFT_HAND,true);
            }
            case RIGHT_LEG -> {
                setlimbAmputated(Limb.RIGHT_FOOT,true);
            }
            case LEFT_LEG ->{
                setlimbAmputated(Limb.LEFT_FOOT,true);
            }
            case RIGHT_ARM -> {
                setlimbAmputated(Limb.RIGHT_HAND,true);
            }
        }
        setlimbAmputated(limb,true);
    }

    public boolean handleAmputation(Limb limb,float damage,float base_damage_treshhold){
        if (!getPERNAMENT_DAMAGE())return false;
        boolean skipOthers= false;
        float damage_treshold = base_damage_treshhold;
        if ((limb==Limb.HEAD&&isMouthRemoved&&leftEyeBlind&&RightEyeBlind)|| limb==Limb.CHEST){
            damage_treshold*=2;
            skipOthers = true;
        } else if (limb==Limb.HEAD && (!leftEyeBlind||!RightEyeBlind)) {
            damage_treshold -=10;
        }
        float musclepenalty= (100-getLimbMuscleHealth(limb))/100 *-10;
        float skinpenalty = (100-getLimbSkinHealth(limb))/100 *-5;
        damage_treshold += musclepenalty+skinpenalty;
        if (damage>=damage_treshold/2){
            if (Math.random()<damage/(damage_treshold))return false;
            if (limb==Limb.HEAD && !skipOthers){
                Random random = new Random();
                int chance = random.nextInt(3);
                if (chance==1&&!RightEyeBlind){
                    RightEyeBlind= true;
                } else if ((chance==2||chance==1) &&!leftEyeBlind) {
                    leftEyeBlind=true;
                }else if (!isMouthRemoved){
                    isMouthRemoved=true;
                }
                Minecraft.getInstance().player.playSound(ModSounds.AMPUTATION.get());
                return true;
            }
            List<Limb> limbList = limb.getConnectedLimbs();
            for (Limb limb1 :limbList){
                setLimbSkinHealth(limb1,0);
                setLimbBleedRate(limb1,1);
                setLimbPain(limb1,200);
                setAdrenaline(Math.max(getAdrenaline(),125));
            }
            dismember(limb);
            Minecraft.getInstance().player.playSound(ModSounds.AMPUTATION.get());
            return true;
        } else if (damage>=6&&!(limb==Limb.CHEST || limb==Limb.HEAD)) {
            if (Math.random()<0.01){
                handleAmputation(limb,1,0);
            }
        }
        return false;
    }

    public void setClean(){
        isReducedDirty = false;
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
        totalPain = Math.max(totalPain,hemothoraxpain);
        totalPain = Math.max(0,totalPain-adrenaline);
    }

    public double getMaxInfection(){
        double infection = limbStats.values().stream().mapToDouble(ls -> ls.infection).max().orElse(0d);
        return Math.max(infection,0);
    }

    private void UpdateLimb(Limb limb){
        LimbStatistics stats = limbStats.get(limb); // store once, reuse
        if (stats.amputated){
            stats.muscleHealth = 0;
            stats.infection = 0;
            stats.Tourniquet = false;
            stats.skinHealth = 0;
            stats.hasSplint = false;
            stats.bleedRate = 0;
            stats.finalPain = 0;
            stats.fracture= 0;
            stats.dislocation = 0;
            stats.tourniquetTimer = 0;
            stats.pain= 0;
            stats.shrapnell= 0;
            stats.desinfectionTimer = 0;
            return;
        }


        //MinpainCalculation

        stats.MinPain = ((stats.infection/100)*10)+(((stats.skinHealth-100)/-100)*15);

        //Healing
        if (stats.SkinHeal&&stats.shrapnell<=0){
            stats.skinHealth += getBOOSTED_LIMB_HEAL_RATE();
        }else {
            stats.skinHealth += getNORMAL_LIMB_HEAL_RATE();
        }

        if (stats.MuscleHeal&&stats.shrapnell<=0&&stats.infection<=0){
            stats.muscleHealth += getBOOSTED_LIMB_HEAL_RATE();
        }else if (stats.shrapnell<=0&&stats.infection<=0){
            stats.muscleHealth += getNORMAL_LIMB_HEAL_RATE();
        }
        stats.skinHealth = Math.min(stats.skinHealth,100);
        stats.muscleHealth = Math.min(stats.muscleHealth,100);

        // Pain Adjustment
        float x = stats.pain / 100f;
        float decay = 0.05f + 0.1f * (float)Math.pow(x, 1.2f);
        if(stats.Tourniquet){
            if (stats.pain>60){
                stats.pain = Math.max(stats.MinPain, stats.pain-decay*(1+(Math.max(0,getNetOpiodids()/40))));
            }
        }else{
            stats.pain = Math.max(stats.MinPain, stats.pain-decay*(1+(Math.max(0,getNetOpiodids())/40)));
        }

        // Infection Adjustment
        calculateInfectionForLimb(limb);
        if (stats.skinHealth < 100 && stats.infection <= 0) {
            float chance = ((100-stats.skinHealth) / 100f) * getINFECTION_CHANCE();
            if (Math.random() < chance) {
                stats.infection += 1;
            }
        }

        infectionSpread(limb);

        // Bleed Adjustment
        stats.bleedRate = Math.min(stats.bleedRate,getMAX_BLEED_RATE()*(Math.abs((stats.skinHealth-100)/100)));
        stats.bleedRate = Math.max(stats.bleedRate,0);

        //Fract/Disl calculation

        if (stats.fracture>0 ||stats.dislocation>0){
            stats.muscleHealth = Math.min(stats.muscleHealth,50);
        }


        if (stats.fracture>0){
            double reduction = getFRACTURE_HEAL()*(1+BooleanUtils.toInteger(stats.hasSplint));
            stats.fracture = (float) Mth.clamp(stats.fracture-reduction,0,100);
        }
        if (stats.dislocation>0){
            double reduction = getFRACTURE_HEAL()*(1+BooleanUtils.toInteger(stats.hasSplint));
            stats.dislocation = (float) Mth.clamp(stats.dislocation-reduction,0,100);
        }


        if (stats.infection>=75){
            stats.muscleHealth -= getINFECTION_MUSCLE_DRAIN();
        }


        if (stats.infection<=0&& limb==Limb.HEAD&&stats.muscleHealth<15){   
            stats.muscleHealth += getBOOSTED_LIMB_HEAL_RATE()*3;
        }

        if (stats.Tourniquet) {
            // Pain ramps up towards 40
            if (stats.pain < 60) {
                stats.pain = Math.min(60, stats.pain + getTOURNIQUET_PAIN_PER_TICK());
            }

            // Timer ticks up
            stats.tourniquetTimer++;
            if (stats.tourniquetTimer > getTOURNIQUET_SAFE_TICKS()) {
                stats.muscleHealth = Math.max(0, stats.muscleHealth -  getTOURNIQUET_MUSCLE_DAMAGE());
                switch (limb) {
                    case LEFT_ARM -> limbStats.get(Limb.LEFT_HAND).muscleHealth =Math.max(0, limbStats.get(Limb.LEFT_HAND).muscleHealth -  getTOURNIQUET_MUSCLE_DAMAGE());
                    case RIGHT_ARM -> limbStats.get(Limb.RIGHT_HAND).muscleHealth =Math.max(0, limbStats.get(Limb.RIGHT_HAND).muscleHealth -  getTOURNIQUET_MUSCLE_DAMAGE());
                    case LEFT_LEG -> limbStats.get(Limb.LEFT_FOOT).muscleHealth =Math.max(0, limbStats.get(Limb.LEFT_FOOT).muscleHealth -  getTOURNIQUET_MUSCLE_DAMAGE());
                    case RIGHT_LEG ->limbStats.get(Limb.RIGHT_FOOT).muscleHealth =Math.max(0, limbStats.get(Limb.RIGHT_FOOT).muscleHealth -  getTOURNIQUET_MUSCLE_DAMAGE()) ;
                }
            }
        } else {
            // Reset timer when removed
            stats.tourniquetTimer = 0;
        }
        stats.skinHealth = Mth.clamp(stats.skinHealth,0,100);
        stats.muscleHealth = Mth.clamp(stats.muscleHealth,0,100);
        stats.finalPain = stats.pain;
        if (Float.isNaN(stats.pain))stats.pain=0;
    }

    public void setLimbMuscleHeal(Limb limb,boolean value){
        ensureLimb(limb).MuscleHeal = value;
    }
    public void setLimbSkinHeal(Limb limb,boolean value){
        ensureLimb(limb).SkinHeal = value;
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
        return (float) (damage * getPAIN_PER_DAMAGE());
    }
    public void applyPain(Limb limb, float value){
        limbStats.get(limb).pain+= value*painscale;
    }
    public void applySkinDamage(Limb limb,float damage){
        limbStats.get(limb).skinHealth = (float) Math.max(limbStats.get(limb).skinHealth-damage*getDAMAGE_SCALE(),0);
        limbStats.get(limb).SkinHeal = false;
        limbStats.get(limb).MuscleHeal = false;

    }
    public void applyMuscleDamage(Limb limb, float damage){
        if (limbStats.get(limb).muscleHealth<100&&damage>2){
            float bone_damage_chance = (float) ((100-limbStats.get(limb).muscleHealth)/100*getFRAC_DISL_FROM_MUSCLE_DAMAGE_CHANCE());
            if (Math.random()>0.5){
                if (Math.random()<bone_damage_chance||damage>15){
                    setLimbFracture(limb,Math.max(getLimbFracture(limb),30+(damage/10)*70));
                    Minecraft.getInstance().player.playSound(ModSounds.BROKEN_BONE.get());
                }
            }else {
                if (Math.random()<bone_damage_chance||damage>15){
                    setLimbDislocation(limb,Math.max(getLimbDislocated(limb),30+(damage/10)*70));
                    Minecraft.getInstance().player.playSound(ModSounds.BROKEN_BONE.get());
                }
            }

        }
        if (limb == Limb.CHEST&&Math.random()>0.5){
            internalBleeding += (damage/15)* (getMAX_BLEED_RATE()/3);
        }
        limbStats.get(limb).muscleHealth = (float) Math.max(limbStats.get(limb).muscleHealth-damage*getDAMAGE_SCALE(),0);
        limbStats.get(limb).SkinHeal = false;
        limbStats.get(limb).MuscleHeal = false;
        if (Math.random()>0.9&& limb==Limb.HEAD){
            brainHealth -= (float) (Math.random()*5);
        }
    }
    public void applyBleedDamage(Limb limb, float damage,Player player){
        float bleed = (damage/15)* getMAX_BLEED_RATE();
        limbStats.get(limb).bleedRate += bleed;
        //spawnParticleFromDamage((ServerPlayer) player,damage);
    }
    private void applyDirectBleedRate(Limb limb,float value){
        limbStats.get(limb).bleedRate = Mth.clamp(limbStats.get(limb).bleedRate-value,0,100);
    }
    private void applyConcussion(Limb limb,float damage){
        if (limb==Limb.HEAD){
            setContiousness(contiousness-(Math.max(damage*2,10)));
        }
    }

    public float getLimbDislocated(Limb limb){
            return ensureLimb(limb).dislocation;
    }

    int tick=0;
    int breathTick = 0;

    int lastStandAnim =-1;
    boolean JustTriggeredLastStand= false;
    float tempspeedup=0f;
    public void tickUpdate(ServerPlayer player) {
         if (JustTriggeredLastStand||(lastStandAnim>0&&!LastStand)) {
             if (JustTriggeredLastStand){
                 lastStandAnim =130;
                 JustTriggeredLastStand=false;
                 return;
             }
             if (lastStandAnim>1){
                 lastStandAnim--;
                 return;
             }
            LastStand = true;
            lastStandAnim =0;
            float newBrain = (float) (75f+ (Math.random()*15));
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20);
            //thirst if i add it
            //sickness if i add it
            blood = Math.max(blood,3.5f);
            if (player.isUnderWater()) Oxygen =100;
            else Oxygen = 20;
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,500,1));
            internalBleeding *=0.2f;
            hemothorax *=0.2f;
            temperature = 36.6f;
            antibiotic_timer += 1.5f*60*20;
            for (Limb limb:Limb.values()){
                setLimbMuscleHealth(limb,getLimbMuscleHealth(limb)+30);
                setLimbInfection(limb,getLimbInfection(limb)*0.2f);
                setLimbBleedRate(limb,getLimbBleedRate(limb)*0.2f);
            }
            drug_addition = 0;
            brainHealth = newBrain;
            contiousness = 20;
            return;
        }
        if (!LastStand){
            if (brainHealth<15&&contiousness<=10&&(temperature>=42||Oxygen<=4)){
                float chance = player.level().getGameRules().getInt(ModGamerules.LAST_STAND_CHANCE)/100f;
                if (Math.random()<chance){
                    JustTriggeredLastStand = true;
                    ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),new TriggerLastStandPacket());
                }
            }
        }
        if (getDO_TEMPERATURE_CHANGE()) {
            updateTemperature(player);
        }else {
            temperature = 36.6f;
        }
        calculateImmunity();
        updateDirtyness(player);
        if (flashHearingLoss>0){
            flashHearingLoss-= 0.1f/20f;
        }
        flashHearingLoss = Mth.clamp(flashHearingLoss,0,1);
        if (hearingLoss>0){
            hearingLoss-= 0.001f/20f;
        }
        hearingLoss = Mth.clamp(hearingLoss,0,1);
        if (adrenaline>0){
            adrenaline -= 4f/20f;
        }
        adrenaline = Math.max(0,adrenaline);
        //Opioid Pending
        if (PendingOpioids>0){
            float change = Math.min(2f+tempspeedup,PendingOpioids)/20f;
            PendingOpioids -=change;
            Opioids +=change;
            tempspeedup = (float) Math.min(tempspeedup+0.05,3f);
        }else {
            tempspeedup=0;
        }
        PendingOpioids = Math.max(PendingOpioids,0);
        isUnderwater = player.isUnderWater();
        hungerLevel = player.getFoodData().getFoodLevel();
        isBreathing = true;

        if (breathTick++>20){
            breathTick=0;
            player.setAirSupply(player.getMaxAirSupply());
        }
        if (player.hasEffect(MobEffects.REGENERATION)&&tick++>20){

            tick=0;
            MobEffectInstance inst = player.getEffect(MobEffects.REGENERATION);
            if (inst!=null) {
                handleMagicHeal(inst.getAmplifier()+1);
            }
        }


        // Death timer check
        if (brainHealth<0.1||limbStats.get(Limb.CHEST).amputated||limbStats.get(Limb.HEAD).amputated) {
            if (player.isAlive()) {
                killPlayer(player, false);
                return;
            }
        }

        // Update each limb
        for (Limb limb : limbStats.keySet()) {
            UpdateLimb(limb);
        }

        recalcTotalPain();
        if (totalPain>75){
            Shock+=0.0015f;
        }else {
            Shock-=0.003f;
        }
        Shock = Mth.clamp(Shock,0,1);
        // Bleeding — internal
        if (internalBleeding > 0) {
            internalBleeding = (float) Math.max(0, internalBleeding - getWUND_ANTIBLEED_RATE());
            internalBleeding = Mth.clamp(internalBleeding,0,getMAX_BLEED_RATE()/4);
        }

        // Hemothorax
        hemothorax += internalBleeding*40;
        if (hemothorax > 0) {
            hemothoraxpain = (float) ((4.0 / 15.0) * hemothorax);
            hemothorax -= getHEMOTHORAX_HEAL_RATE();
        }

        // Blood calculation
        float totalBleedPerTick = getCombinedBleed();
        blood -= totalBleedPerTick;
        contiousnessCap = 100;

        if (blood > 5.25) contiousnessCap = 80;

        if (contiousnessCap>brainHealth)contiousnessCap=brainHealth;
        double headpenalty = Math.min((limbStats.get(Limb.HEAD).muscleHealth-50)*2,0);

        if (limbStats.get(Limb.HEAD).muscleHealth<15){
            limbStats.get(Limb.HEAD).MuscleHeal = true;
        }
        if (contiousnessCap>100+headpenalty)contiousnessCap= (float) (100+headpenalty);


        if (blood > 5) {
            blood = Math.max(5, blood - (getBLOOD_REGEN_RATE() * getNutritionFactor()));
        } else if (blood < 5) {
            blood = Math.min(5, blood + (getBLOOD_REGEN_RATE() * getNutritionFactor()));
        }
        if (getNetOpiodids()>0){
            float negativecons = (float) (getNetOpiodids()*getContiousnessPerOpioid());
            contiousnessCap = Math.min(contiousnessCap,100-negativecons);
        } else if (getNetOpiodids()<-40) {
            brainHealth -= 0.01f/20f;
        }

        if (brainHealth<30)contiousnessCap=0;

        if (Opioids>0){
            drug_addition+= 0.05f/20f;
        }else {
            drug_addition+= -0.05f/20f;
        }
        drug_addition = Math.max(0,drug_addition);
        if (drug_addition>42){
            brainHealth -= 0.05f/20f;
        }

        // Respiratory arrest condition
        respitoryArrest = isFreezing||getNetOpiodids() > 100 || blood >= 5.7 || limbStats.get(Limb.CHEST).muscleHealth < 5||getTourniquet(Limb.HEAD);

        // Oxygen cap — based on blood volume and hemothorax
        OxygenCap = 100;
        bloodViscosity = Math.max(0,bloodViscosity-getBLOOD_VISCOSITY_REGEN());
        if (blood < 4.375) {
            OxygenCap += (160f / 3) * blood - (700f / 3);
        }
        OxygenCap += (-2.0f / 3.0f) * hemothorax;
        OxygenCap -= bloodViscosity;
        OxygenCap = Math.max(0, OxygenCap);

        // Breathing & oxygen change
        if ((isUnderwater&&player.getEffect(MobEffects.WATER_BREATHING)==null) || respitoryArrest) {
            isBreathing = false;
        }
        if (Oxygen>OxygenCap||(!isBreathing&&!isUnderwater)||(getAirLossRate(player)>0&&isUnderwater)||respitoryArrest){
            Oxygen = Math.max(0, Oxygen - getOXYGEN_DRAIN());
        }
        if (isBreathing&&(Oxygen <= OxygenCap)) {
            Oxygen = Math.min(100, Oxygen + getOXYGEN_REPLENISH());
        }

        // Opioid decay
        Opioids = Math.max(0, Opioids - 0.02f);

        // Consciousness
        float oldCont = this.contiousness;
        recalculateConsciousness();

        // Death timer adjustments
        if (Oxygen <= 4) {
            brainHealth -=getBRAIN_DRAIN();
        }
        calculateBrain();
        calculateBPM();
        applyPenalties(player);
        changeEntries.removeIf(entry -> {
            entry.reduceTicks(1); // decrement by 1 tick
            applyDirectBleedRate(entry.getLimb(), entry.getAmount_per_tick());
            return entry.getTicks() <= 0; // remove if done
        });


        if (oldCont <= 4 && this.contiousness > 4){
            if (player.level().isClientSide) return; // only server-side

            for (ServerPlayer other : player.getServer().getPlayerList().getPlayers()) {
                if (other.containerMenu == player.inventoryMenu) {
                    other.closeContainer(); // kicks them out of target's inventory
                }
            }
        }
        if (lifeSupportTimer>0){
            lifeSupportTimer--;
            Oxygen = Math.max(Oxygen,50);
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,10,0));
            adrenaline = Math.max(70,adrenaline);
        }
        if (leftEyeBlind&&RightEyeBlind){
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,100,1,true,false));
        }

        boolean isUnc = getContiousness()<=10;
            if (isUnc){
                player.zza = 0;
                player.xxa = 0;
                player.yRotO = 0; // Stop looking around
                player.xRotO = 0;
            }

    }

    public void calculateBrain(){
        if (Oxygen>5){
            if (getPERNAMENT_DAMAGE()){
                brainHealth = Mth.clamp(brainHealth+getBRAIN_REGEN_RATE(),0,100);
            }else {
                brainHealth = Mth.clamp(brainHealth+(5/20f),0,100);
            }
        }
    }

    public boolean isRespitoryArrest() {
        return respitoryArrest;
    }

    public void setBloodViscosity(float bloodViscosity) {
        this.bloodViscosity = bloodViscosity;
    }

    public float getBloodViscosity() {
        return bloodViscosity;
    }

    public boolean isFreezing= false;
    public void applyPenalties(ServerPlayer player){
        double baseMoveSpeed = 0.1;
        double baseAttackDamage = 1.0;
        double baseAttackSpeed = 4.0;
// --- Calculate limb-based multipliers ---
        double moveReduction =
                ((100 - getLimbMuscleHealth(Limb.RIGHT_LEG))  / 100.0) * 0.14 +
                        ((100 - getLimbMuscleHealth(Limb.LEFT_LEG))   / 100.0) * 0.14 +
                        ((100 - getLimbMuscleHealth(Limb.RIGHT_FOOT)) / 100.0) * 0.14 +
                        ((100 - getLimbMuscleHealth(Limb.LEFT_FOOT))  / 100.0) * 0.14;

        if (isAmputated(Limb.RIGHT_LEG) && isAmputated(Limb.LEFT_LEG)) {
            moveReduction = 0.2;
        }

        HumanoidArm handpart = Limb.getFromHand(InteractionHand.MAIN_HAND, player);

        double attackMultiplier = ((100-getLimbMuscleHealth(Limb.RIGHT_ARM))  / 100.0) * 0.1 +
                ((100-getLimbMuscleHealth(Limb.LEFT_ARM))   / 100.0) * 0.25 +
                ((100-getLimbMuscleHealth(Limb.RIGHT_HAND)) / 100.0) * 0.10 +
                ((100-getLimbMuscleHealth(Limb.LEFT_HAND))  / 100.0) * 0.25;
        PrototypePain.LOGGER.info("at {}",attackMultiplier);
        if (handpart == HumanoidArm.RIGHT) {
            attackMultiplier = ((100-getLimbMuscleHealth(Limb.RIGHT_ARM))  / 100.0) * 0.10 +
                    ((100-getLimbMuscleHealth(Limb.LEFT_ARM))   / 100.0) * 0.10 +
                    ((100-getLimbMuscleHealth(Limb.RIGHT_HAND)) / 100.0) * 0.25 +
                    ((100-getLimbMuscleHealth(Limb.LEFT_HAND))  / 100.0) * 0.25;
        }

        double moveMultiplier = 1.0 - moveReduction;
        isFreezing = false;
        painscale = 1f;

// --- Temperature effects ---
        if (temperature > 42) {
            for (Limb limb : Limb.values()) {
                applyPain(limb, 0.1f / 20f);
            }
            brainHealth -= 0.5f / 20f;
            moveMultiplier -= 0.1f;
        } else if (temperature > 41) {
            moveMultiplier -= 0.05f;
        } else if (temperature < 27) {
            moveMultiplier -= 0.2f;
            contiousness -= (float) ((getContiousnessregen() + 0.25f) / 20f);
            if (contiousness > 1) {
                isFreezing = true;
            }
            painscale = 1.5f;
        } else if (temperature < 32) {
            painscale = 1.3f;
            moveMultiplier -= 0.1f;
        } else if (temperature < 33) {
            painscale = 1.1f;
            moveMultiplier -= 0.07f;
        } else if (temperature < 35) {
            moveMultiplier -= 0.05f;
        }
// --- Apply modifiers safely ---
        applyAttributeModifier(player, Attributes.MOVEMENT_SPEED, "custom_move_speed",
                (baseMoveSpeed * Math.max(0.0, moveMultiplier)) - baseMoveSpeed,
                AttributeModifier.Operation.ADDITION);

        applyAttributeModifier(player, Attributes.ATTACK_DAMAGE, "custom_attack_damage",
                (baseAttackDamage * (1-attackMultiplier)) - baseAttackDamage,
                AttributeModifier.Operation.ADDITION);

        applyAttributeModifier(player, Attributes.ATTACK_SPEED, "custom_attack_speed",
                (baseAttackSpeed * (1-attackMultiplier)) - baseAttackSpeed,
                AttributeModifier.Operation.ADDITION);

        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);



            if (!stack.isEmpty()) {
                HumanoidArm arm = Limb.getFromHand(hand, player);
                Limb limb = (arm == HumanoidArm.LEFT) ? Limb.LEFT_HAND : Limb.RIGHT_HAND;

                boolean broken = getLimbMuscleHealth(limb) < 10
                        || getLimbFracture(limb) > 0
                        || getLimbDislocated(limb) > 0;

                if (broken) {
                    player.setItemInHand(hand, ItemStack.EMPTY);

                    int handSlot = player.getInventory().selected; // hotbar index of hand
                    if (player.getInventory().getItem(handSlot).isEmpty()) {
                        // don't add to inventory, just drop
                        player.drop(stack, false);
                    } else {
                        // safe to add to other inventory slots
                        ItemStack leftover = player.getInventory().add(stack) ? ItemStack.EMPTY : stack;
                        if (!leftover.isEmpty()) player.drop(leftover, false);
                    }
                }
            }
        }
    }

    private static void applyAttributeModifier(LivingEntity player, Attribute attribute, String name, double amount, AttributeModifier.Operation operation) {
        var instance = player.getAttribute(attribute);
        if (instance == null) return;

        UUID id = UUID.nameUUIDFromBytes(name.getBytes());
        // Remove old modifier if it exists
        instance.removeModifier(id);

        // Skip adding modifier if multiplier = 0 (no change)
        if (amount == 0.0) return;

        AttributeModifier modifier = new AttributeModifier(id, name, amount, operation);
        instance.addPermanentModifier(modifier);
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
        nbt.putFloat("BPM", BPM);
        nbt.putBoolean("IsBreathing", isBreathing);
        nbt.putFloat("BloodViscosity",bloodViscosity);
        nbt.putFloat("BrainHealth",brainHealth);
        nbt.putFloat("Immunity",immunity);
        nbt.putFloat("Drug_addition",drug_addition);
        nbt.putFloat("Shock",Shock);
        nbt.putFloat("Dirty",dirtyness);
        nbt.putFloat("Temp",temperature);
        nbt.putFloat("Adrenaline",adrenaline);
        nbt.putInt("LifeSupport",lifeSupportTimer);
        nbt.putBoolean("LeftEyeBlind",leftEyeBlind);
        nbt.putBoolean("RightEyeBlind",RightEyeBlind);
        nbt.putBoolean("MouthMissing",isMouthRemoved);
        nbt.putFloat("HearingLoss",hearingLoss);
        nbt.putFloat("FlashHearing",flashHearingLoss);
        nbt.putBoolean("LastStand",LastStand);

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
            limbTag.putFloat("FractureTimer", stats.fracture);
            limbTag.putFloat("Dislocated", stats.dislocation);
            limbTag.putInt("Shrapnell", stats.shrapnell);
            limbTag.putBoolean("HasSplint", stats.hasSplint);
            limbTag.putFloat("BleedRate", stats.bleedRate);
            limbTag.putFloat("DesinfectionTimer", stats.desinfectionTimer);
            limbTag.putFloat("MinPain", stats.MinPain);
            limbTag.putFloat("FinalPain",stats.finalPain);
            limbTag.putBoolean("SkinHeal",stats.SkinHeal);
            limbTag.putBoolean("MuscleHeal",stats.MuscleHeal);
            limbTag.putBoolean("Tourniquet",stats.Tourniquet);
            limbTag.putInt("TourniquetTime",stats.tourniquetTimer);
            limbTag.putBoolean("Amputated", stats.amputated);
            limbList.add(limbTag);
        }
        nbt.put("LimbStats", limbList);

        return nbt;
    }

    public boolean isReducedDirty = false;
    public CompoundTag serilizeReducedNbt(CompoundTag tag){
        ListTag limbList = new ListTag();
        for (Map.Entry<Limb, LimbStatistics> entry : limbStats.entrySet()) {
            CompoundTag limbTag = new CompoundTag();
            limbTag.putString("LimbName", entry.getKey().name());
            LimbStatistics stats = entry.getValue();
            limbTag.putBoolean("Amputated", stats.amputated);
            limbList.add(limbTag);
        }
        tag.put("LimbStats", limbList);

        return tag;
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
        this.bloodViscosity = other.bloodViscosity;
        this.brainHealth = other.brainHealth;
        this.immunity = other.immunity;
        this.antibiotic_timer = other.antibiotic_timer;
        this.drug_addition = other.drug_addition;
        this.Shock = other.Shock;
        this.dirtyness = other.dirtyness;
        this.temperature = other.temperature;
        this.adrenaline = other.adrenaline;
        this.lifeSupportTimer = other.lifeSupportTimer;
        this.isMouthRemoved = other.isMouthRemoved;
        this.leftEyeBlind = other.leftEyeBlind;
        this.RightEyeBlind = other.RightEyeBlind;
        this.hearingLoss = other.hearingLoss;
        this.flashHearingLoss = other.flashHearingLoss;
        this.LastStand = other.LastStand;

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
            copiedStats.fracture = originalStats.fracture;
            copiedStats.dislocation = originalStats.dislocation;
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
            copiedStats.amputated = originalStats.amputated;

            this.limbStats.put(limb, copiedStats);
        }
    }

    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("Blood"))
            blood = nbt.getFloat("Blood");
        if(nbt.contains("TotalPain"))
            totalPain = nbt.getDouble("TotalPain");
        if(nbt.contains("Consciousness"))
            contiousness = nbt.getFloat("Consciousness");
        if(nbt.contains("ConsciousnessCap"))
            contiousnessCap = nbt.getFloat("ConsciousnessCap");
        if(nbt.contains("Hemothorax"))
            hemothorax = nbt.getFloat("Hemothorax");
        if(nbt.contains("HemothoraxPain"))
            hemothoraxpain = nbt.getFloat("HemothoraxPain");
        if(nbt.contains("InternalBleeding"))
            internalBleeding = nbt.getFloat("InternalBleeding");
        if(nbt.contains("Oxygen"))
            Oxygen = nbt.getFloat("Oxygen");
        if(nbt.contains("OxygenCap"))
            OxygenCap = nbt.getFloat("OxygenCap");
        if(nbt.contains("Opioids"))
            Opioids = nbt.getFloat("Opioids");
        if(nbt.contains("BPM"))
            BPM = nbt.getFloat("BPM");
        if(nbt.contains("IsBreathing"))
            isBreathing = nbt.getBoolean("IsBreathing");
        if(nbt.contains("BloodViscosity"))
            bloodViscosity = nbt.getFloat("BloodViscosity");
        if (nbt.contains("BrainHealth"))
            brainHealth = nbt.getFloat("BrainHealth");

        if (nbt.contains("Immunity"))
            immunity = nbt.getFloat("Immunity");
        if (nbt.contains("Drug_addition"))
            drug_addition = nbt.getFloat("Drug_addition");
        if (nbt.contains("Shock"))
            Shock = nbt.getFloat("Shock");
        if (nbt.contains("Dirty"))
            dirtyness = nbt.getFloat("Dirty");
        if (nbt.contains("Temp"))
            temperature = nbt.getFloat("Temp");
        if (nbt.contains("Adrenaline"))
            adrenaline = nbt.getFloat("Adrenaline");
        if (nbt.contains("LifeSupport"))
            lifeSupportTimer = nbt.getInt("LifeSupport");
        if (nbt.contains("LeftEyeBlind"))
            leftEyeBlind = nbt.getBoolean("LeftEyeBlind");
        if (nbt.contains("RightEyeBlind"))
            RightEyeBlind = nbt.getBoolean("RightEyeBlind");
        if (nbt.contains("MouthMissing"))
            isMouthRemoved = nbt.getBoolean("MouthMissing");
        if (nbt.contains("HearingLoss"))
            hearingLoss = nbt.getFloat("HearingLoss");
        if (nbt.contains("FlashHearing"))
            flashHearingLoss = nbt.getFloat("FlashHearing");
        if (nbt.contains("LastStand"))
            LastStand = nbt.getBoolean("LastStand");

        changeEntries.clear();
        ListTag changeList = nbt.getList("ChangeList", 10);
        for (int i = 0; i < changeList.size(); i++) {
            CompoundTag changeTag = changeList.getCompound(i);
            changeEntries.add(DelayedChangeEntry.fromNBT(changeTag));
        }
        ListTag limbList = nbt.getList("LimbStats", Tag.TAG_COMPOUND);
        for (int i = 0; i < limbList.size(); i++) {
            CompoundTag limbTag = limbList.getCompound(i);
            Limb limb = Limb.valueOf(limbTag.getString("LimbName"));

            // ✅ Get existing stats or create if missing
            LimbStatistics stats = limbStats.computeIfAbsent(limb, k -> new LimbStatistics());

            // ✅ Now update fields in place
            if (limbTag.contains("SkinHealth")) stats.skinHealth = limbTag.getFloat("SkinHealth");
            if (limbTag.contains("MuscleHealth")) stats.muscleHealth = limbTag.getFloat("MuscleHealth");
            if (limbTag.contains("Pain")) stats.pain = limbTag.getFloat("Pain");
            if (limbTag.contains("Infection")) stats.infection = limbTag.getFloat("Infection");
            if (limbTag.contains("FractureTimer")) stats.fracture = limbTag.getFloat("FractureTimer");
            if (limbTag.contains("Dislocated")) stats.dislocation = limbTag.getFloat("Dislocated");
            if (limbTag.contains("Shrapnell")) stats.shrapnell = limbTag.getInt("Shrapnell");
            if (limbTag.contains("HasSplint")) stats.hasSplint = limbTag.getBoolean("HasSplint");
            if (limbTag.contains("BleedRate")) stats.bleedRate = limbTag.getFloat("BleedRate");
            if (limbTag.contains("DesinfectionTimer")) stats.desinfectionTimer = limbTag.getFloat("DesinfectionTimer");
            if (limbTag.contains("MinPain")) stats.MinPain = limbTag.getFloat("MinPain");
            if (limbTag.contains("FinalPain")) stats.finalPain = limbTag.getFloat("FinalPain");
            if (limbTag.contains("SkinHeal")) stats.SkinHeal = limbTag.getBoolean("SkinHeal");
            if (limbTag.contains("MuscleHeal")) stats.MuscleHeal = limbTag.getBoolean("MuscleHeal");
            if (limbTag.contains("Tourniquet")) stats.Tourniquet = limbTag.getBoolean("Tourniquet");
            if (limbTag.contains("TourniquetTime")) stats.tourniquetTimer = limbTag.getInt("TourniquetTime");
            if (limbTag.contains("Amputated"))stats.amputated = limbTag.getBoolean("Amputated");
            if (Float.isNaN(stats.bleedRate)) stats.bleedRate = 0;
        }
    }

    public void resetToDefaults() {
        // clear & repopulate limb stats with fresh defaults
        limbStats.clear();
        for (Limb limb : Limb.values()) {
            limbStats.put(limb, new LimbStatistics());
        }

        // clear delayed changes
        changeEntries.clear();

        // reset player-wide primitives to initial defaults (match the field initializers)
        blood = 5f;
        totalPain = 0f;
        contiousness = 100f;
        contiousnessCap = 100f;
        hemothorax = 0f;
        hemothoraxpain = 0f;
        internalBleeding = 0f;
        Oxygen = 100f;
        OxygenCap = 100f;
        Opioids = 0f;
        BPM = 70;
        isBreathing = true;
        respitoryArrest = false;
        bloodViscosity = 0f;
        brainHealth = 100;
        drug_addition = 0;
        temperature = 36.6f;
        Shock =0 ;
        dirtyness = 0;
        antibiotic_timer = 0;
        immunity = 100;
        adrenaline = 0;
        lifeSupportTimer =0;
        isMouthRemoved = false;
        leftEyeBlind = false;
        RightEyeBlind= false;
        hearingLoss = 0;
        flashHearingLoss = 0;
        LastStand = false;

        // passthrough / player-state
        hungerLevel = 20;
        isUnderwater = false;

        // derived values
        recalcTotalPain();
        recalculateConsciousness();
    }


    public ItemStack tryUseItem(Limb limb, ItemStack itemstack, ServerPlayer source, ServerPlayer target){
        if (itemstack.getItem() instanceof ISimpleMedicalUsable medItem){
            ItemStack used =  medItem.onMedicalUse(limb,source,target,itemstack);
            if (used!=itemstack){
                source.serverLevel().getLevel().playSound(null,source.getOnPos(),medItem.getUseSound(), SoundSource.PLAYERS);
            }
            return used;
        }
        return itemstack;
    }


    public void medicalAction(MedicalAction action, Limb limb, Player source){
        Random random = new Random();
        switch (action){
            case TRY_SHRAPNEL -> {
                applyPain(limb,10);
                int damage = random.nextInt(3);
                applySkinDamage(limb,damage);
                applyMuscleDamage(limb,damage);
                applyBleedDamage(limb,damage/6f,source);
                if (random.nextFloat()<=getMANUAL_SHRAPNEL_SUCCESS_CHANCE()){
                    setLimbShrapnell(limb,0);
                }
            }
            case REMOVE_SPLINT -> {
                setLimbSplint(limb,false);
                source.getInventory().add(new ItemStack(ModItems.Splint.get()));
            }
            case FIX_DISLOCATION -> {
                applyPain(limb,30);
                if (random.nextFloat()<=getDISLOCATION_FIX_CHANCE()){
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



    public void handleFallDamage(float damageValue,Player player){
        Random random = new Random();
        setAdrenaline(Math.max(getAdrenaline(),damageValue*2));
        float remainingDamage = damageValue * 1;

        remainingDamage = applyLocationalArmor(Limb.LEFT_FOOT,remainingDamage,player,false,false,false,true);


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
                handleRandomDamage(remainingDamage * 0.5f,player);
            }

            // decay for next pass
            remainingDamage *= 0.7f;
            if (remainingDamage < 1f) return;
        }

    }

    public void handleMagicDamage(float damage){
        for (Limb limb : limbStats.keySet()){
            setAdrenaline(Math.max(getAdrenaline(),damage*1));
            applyMuscleDamage(limb, (float) (damage*(Math.random()/4f)));
            applyPain(limb, (float) (damage*(Math.random())));
        }
    }

    public float applyLocationalArmor(Limb limb, float damage, Player player,boolean is_fire,boolean is_projectile,boolean is_explosion,boolean is_fall){
        float armPoints=0,tough=0,prot=0,fprot =0,pprot=0,expprot=0,ff=0;
        double[] armorScale = getArmorScaling();
        switch (limb){
            case HEAD -> {
                ItemStack item = player.getItemBySlot(EquipmentSlot.HEAD);
                if (item.getItem() instanceof ArmorItem armor){
                    armPoints = (float) (armor.getDefense() * armorScale[0]);
                    tough = armor.getToughness();
                    prot = item.getEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION);
                    fprot = item.getEnchantmentLevel(Enchantments.FIRE_PROTECTION);
                    pprot = item.getEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION);
                    expprot = item.getEnchantmentLevel(Enchantments.BLAST_PROTECTION);
                }
            }
            case LEFT_ARM,RIGHT_ARM,LEFT_HAND,RIGHT_HAND ->{
                ItemStack item = player.getItemBySlot(EquipmentSlot.CHEST);
                float scalar = 0.5f;
                if (item.is(ModItemTags.ARMOR_CHEST_ONLY))
                    scalar =0;
                else if (item.is(ModItemTags.ARMOR_FULL_ARM))
                    scalar = 1;
                if (item.getItem() instanceof ArmorItem armor){
                    armPoints = (float) (armor.getDefense()*scalar* armorScale[1]);
                    tough = armor.getToughness();
                    prot = item.getEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION);
                    fprot = item.getEnchantmentLevel(Enchantments.FIRE_PROTECTION);
                    pprot = item.getEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION);
                    expprot = item.getEnchantmentLevel(Enchantments.BLAST_PROTECTION);
                }
            }
            case CHEST -> {
                ItemStack item = player.getItemBySlot(EquipmentSlot.CHEST);
                if (item.getItem() instanceof ArmorItem armor){
                    armPoints = (float) (armor.getDefense()* armorScale[1]);
                    tough = armor.getToughness();
                    prot = item.getEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION);
                    fprot = item.getEnchantmentLevel(Enchantments.FIRE_PROTECTION);
                    pprot = item.getEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION);
                    expprot = item.getEnchantmentLevel(Enchantments.BLAST_PROTECTION);
                }
            }
            case RIGHT_LEG,LEFT_LEG ->{
                ItemStack item = player.getItemBySlot(EquipmentSlot.LEGS);
                if (item.getItem() instanceof ArmorItem armor){
                    armPoints = (float) (armor.getDefense()* armorScale[2]);
                    tough = armor.getToughness();
                    prot = item.getEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION);
                    fprot = item.getEnchantmentLevel(Enchantments.FIRE_PROTECTION);
                    pprot = item.getEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION);
                    expprot = item.getEnchantmentLevel(Enchantments.BLAST_PROTECTION);
                }
            }
            case RIGHT_FOOT,LEFT_FOOT -> {
                ItemStack item = player.getItemBySlot(EquipmentSlot.FEET);
                if (item.getItem() instanceof ArmorItem armor){
                    armPoints = (float) (armor.getDefense()* armorScale[3]);
                    tough = armor.getToughness();
                    prot = item.getEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION);
                    fprot = item.getEnchantmentLevel(Enchantments.FIRE_PROTECTION);
                    pprot = item.getEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION);
                    expprot = item.getEnchantmentLevel(Enchantments.BLAST_PROTECTION);
                    ff = item.getEnchantmentLevel(Enchantments.FALL_PROTECTION);
                }
            }
            default -> {
                return damage;
            }
        }
        float d,r=0;
        d= (float) Math.min(0.75f,0.14f+0.02f*Math.pow(armPoints-1,2));
        r = 1.0f - Math.min(damage / (damage + (2 * tough + 8)), 0.5f);
        float finalReduction = Mth.clamp(d*r,0,1);
        float magic=0;
        if (prot>0){
            magic = 1f-prot*0.05f;
        }else {
            if (is_fire){
                magic =1-fprot*0.10f;
            }else if (is_projectile){
                magic =1-pprot*0.10f;
            } else if (is_fall) {
                magic =1-ff*0.14f;
            } else if (is_explosion) {
                magic =1-expprot*0.10f;
            }
        }

        return (damage*(1-finalReduction))*magic;
    }

    public void handleFireDamage(float damage,Player player){
        setAdrenaline(Math.max(getAdrenaline(),damage*1));
        int i=0;
        while (damage>0){
            i++;
            Limb randLimb = Limb.weigtedRandomLimb();

            float damage_pass = (float) (Math.random() * 4);
            if (damage_pass>damage) damage_pass = damage;
            float passDamage = applyLocationalArmor(randLimb,Math.min(2,damage_pass),player,true,false,false,false);
            applyPain(randLimb,passDamage*5);
            applyConcussion(randLimb,damage_pass);
            limbStats.get(randLimb).muscleHealth -= passDamage*3;
            applySkinDamage(randLimb,passDamage*3);
            if (Math.random()<i*0.2){
                applyPain(randLimb,passDamage*5);
                limbStats.get(randLimb).muscleHealth -= passDamage*5;
                applySkinDamage(randLimb,passDamage*1.5f);
                applyBleedDamage(randLimb,passDamage*2.5f,player);
                damage-=1;
            }
            damage-=damage_pass;
            hurtArmor(randLimb,player,damage_pass);
        }
    }

    public void handleExplosionDamage(float damage,boolean shrapnell, Player player){
        setAdrenaline(Math.max(getAdrenaline(),damage*1));
        while (damage>2){
            float passDamage = Math.min(damage,6);
            Limb rLimb = Limb.weigtedRandomLimb();
            passDamage = applyLocationalArmor(rLimb,passDamage,player,false,false,true,false);
            applyConcussion(rLimb,passDamage);
            applyMuscleDamage(rLimb,passDamage*0.2f);
            applySkinDamage(rLimb,passDamage*0.8f);
            applyPain(rLimb,painFromDamage(passDamage));
            applyBleedDamage(rLimb,passDamage*0.7f,player);
            float chance = passDamage/6+0.2f;
            if (Math.random()<chance&&shrapnell){
                setLimbShrapnell(rLimb, (int) (hasLimbShrapnell(rLimb)+(Math.random()*5)));
            }
            hurtArmor(rLimb,player,damage);
            boolean amputated = handleAmputation(rLimb,passDamage,15+5);
            if (amputated){
                damage/=4;
            }
            damage-=2;
        };
    }

    public void calculateBPM(){
        int newBPM=70;
        int painAdd = (int) (Math.pow((totalPain/100),1.4)*100);

        newBPM =newBPM+painAdd;

        if (blood<3){
            newBPM = (int) (newBPM *(blood/3));
        }
        if (getNetOpiodids()>50){
            newBPM = (int) (newBPM*0.80);
        }
        this.BPM = Mth.clamp(newBPM,0,170);
    }

    public void handleProjectileDamage(HitSector hitSector,float damage, Player player) {
        setAdrenaline(Math.max(getAdrenaline(),damage*2));
        Random random = new Random();
        List<Limb> limbList = hitSector.getLimbsPerSector();
        Limb randomLimb = limbList.get(random.nextInt(limbList.size()));
        damage = applyLocationalArmor(randomLimb,damage,player,false,true,false,false);

        applyConcussion(randomLimb,damage);
        applyMuscleDamage(randomLimb,(float) (damage*(Math.random()/2f+0.5f)));
        applyPain(randomLimb,painFromDamage(damage));
        applySkinDamage(randomLimb, (float) (damage*(Math.random()/2f+0.5f)));
        applyBleedDamage(randomLimb,damage*0.9f,player);
        float chance;

        if (damage <= 2f || damage >= 10f) {
            chance = 0f;
        } else {
            float t = (damage - 2f) / 8f; // normalize to [0..1]
            float curve = (float)(-4 * Math.pow(t - 0.5f, 2) + 1);
            chance = Math.max(0f, curve * 0.75f); // scale to max 0.75
        }
        if (random.nextFloat()<chance){
            setLimbShrapnell(randomLimb,hasLimbShrapnell(randomLimb)+1);
        }
        hurtArmor(randomLimb,player,damage);
        boolean amputated = handleAmputation(randomLimb,damage,15+5+6);
        if (amputated){
            damage/=4;
        }
    }

    public void handleRandomDamage(float damage, Player player) {
        setAdrenaline(Math.max(getAdrenaline(),damage*1));
        int i=0;
        while (damage>0){
            i++;
            Limb randLimb = Limb.weigtedRandomLimb();
            float damage_pass = (float) (Math.random() * 4);
            if (damage_pass>damage) damage_pass = damage;
            float passDamage = applyLocationalArmor(randLimb,Math.min(2,damage_pass),player,true,false,false,false);
            applyConcussion(randLimb,passDamage);
            applyPain(randLimb,painFromDamage((float) (passDamage*(Math.random()/2+1f))));
            applyMuscleDamage(randLimb, (float) (passDamage*(Math.random()/2+0.5f)));
            applySkinDamage(randLimb,(float) (passDamage*(Math.random()/2+0.5f)));
            if (Math.random()<i*0.2){
                applyPain(randLimb,painFromDamage((float) (passDamage*(Math.random()/2+1.5f))));
                applyMuscleDamage(randLimb, (float) (passDamage*(Math.random()/2+0.7f)));
                applySkinDamage(randLimb,(float) (passDamage*(Math.random()/2+0.7f)));
                applyBleedDamage(randLimb,(float) (passDamage*(Math.random()/2+0.7f)),player);
                damage-=damage_pass;
            }
            damage-=damage_pass;
            hurtArmor(randLimb,player,damage_pass);
            boolean amputated = handleAmputation(randLimb,passDamage,15+5+2);
            if (amputated){
                damage/=4;
            }
        }
    }



    public void addDelayedChange(float totalBleedAmount,int timeInTicks,Limb limb){
        float bleed = totalBleedAmount/timeInTicks;
        changeEntries.add(new DelayedChangeEntry(bleed,timeInTicks,limb));
    }

    public void onArmUse(InteractionHand hand,Player player){
        HumanoidArm arm = Limb.getFromHand(hand,player);

        if (arm ==HumanoidArm.LEFT){
            if ((getLimbFracture(Limb.LEFT_HAND)>0||getLimbDislocated(Limb.LEFT_HAND)>0))applyPain(Limb.LEFT_HAND,0.5f);
            if ((getLimbFracture(Limb.LEFT_ARM)>0||getLimbDislocated(Limb.LEFT_ARM)>0))applyPain(Limb.LEFT_ARM,0.5f);
        }else{
            if ((getLimbFracture(Limb.RIGHT_HAND)>0||getLimbDislocated(Limb.RIGHT_HAND)>0))applyPain(Limb.RIGHT_HAND,0.5f);
            if ((getLimbFracture(Limb.RIGHT_ARM)>0||getLimbDislocated(Limb.RIGHT_ARM)>0))applyPain(Limb.RIGHT_ARM,0.5f);
        }
    }

    public void onLegUse(){
        float pain_per_tick = 0.5f;
            if (getLimbFracture(Limb.RIGHT_LEG)>0||getLimbDislocated(Limb.RIGHT_LEG)>0)applyPain(Limb.RIGHT_LEG,pain_per_tick);
            if (getLimbFracture(Limb.LEFT_LEG)>0||getLimbDislocated(Limb.LEFT_LEG)>0)applyPain(Limb.LEFT_LEG,pain_per_tick);
            if (getLimbFracture(Limb.RIGHT_FOOT)>0||getLimbDislocated(Limb.RIGHT_FOOT)>0)applyPain(Limb.RIGHT_FOOT,pain_per_tick);
            if (getLimbFracture(Limb.LEFT_FOOT)>0||getLimbDislocated(Limb.LEFT_FOOT)>0)applyPain(Limb.LEFT_FOOT,pain_per_tick);
    }

    public void killPlayer(ServerPlayer player,boolean gaveUp){
        boolean bleedout = blood<3.5f;
        boolean internalBleed = hemothorax>50;
        boolean overdose = getNetOpiodids()>100;
        boolean bleedoutHeavy = getCombinedBleed()>2f/20f/60f;

        DamageSource src = ModDamageTypes.oxygen(player.serverLevel());
        if (gaveUp){
            src = ModDamageTypes.giveUp(player.serverLevel());

        }
        if (bleedoutHeavy) {
            src = ModDamageTypes.heavy_bleed(player.serverLevel());

        }
        if (internalBleed) {
            src = ModDamageTypes.internal(player.serverLevel());

        }
        if (bleedout) {
            src = ModDamageTypes.bleed(player.serverLevel());

        }
        if (overdose) {
            src = ModDamageTypes.opioids(player.serverLevel());

        }
        for (Limb limb: limbStats.keySet()){
            LimbStatistics lstat = limbStats.get(limb);
            if (lstat.Tourniquet){
                player.getInventory().add(new ItemStack(ModItems.Tourniquet.get()));
            }
            if (lstat.hasSplint){
                player.getInventory().add(new ItemStack(ModItems.Splint.get()));
            }
        }
        player.setHealth(0.1f);
        player.hurt(src, 5.0F);
        if (player.isAlive()) {
            player.setHealth(0.1f);
            player.hurt(src, 5.0F);
        }
        player.kill();
    }



    public void handleMagicHeal(float amount){
        for (Limb limb : limbStats.keySet()){
            if (limbStats.get(limb).bleedRate>0)
                limbStats.get(limb).bleedRate = Math.max(0,limbStats.get(limb).bleedRate-amount*0.00005f*getMAGICAL_HEAL());
            limbStats.get(limb).skinHealth = Math.min(100,limbStats.get(limb).skinHealth+2*amount*getMAGICAL_HEAL());
            limbStats.get(limb).muscleHealth = Math.min(100,limbStats.get(limb).muscleHealth+2*amount*getMAGICAL_HEAL());
        }
        if (blood<5){
            blood = Math.min(5,blood+0.02f*amount*getMAGICAL_HEAL());
        }else if (blood>5){
            blood = Math.max(5,blood-(0.02f*amount*getMAGICAL_HEAL()));
        }
        if (bloodViscosity >10){
            bloodViscosity -=1*amount;
        }
    }

    public void hurtArmor(Limb limb,Player player,float damage){
        ItemStack item = null;
        EquipmentSlot eq = null;
        switch (limb){
            case CHEST,LEFT_ARM,RIGHT_ARM,LEFT_HAND,RIGHT_HAND -> {
                item = player.getItemBySlot(EquipmentSlot.CHEST);
                eq = EquipmentSlot.CHEST;
            }
            case HEAD -> {
                item = player.getItemBySlot(EquipmentSlot.HEAD);
                eq = EquipmentSlot.HEAD;
            }
            case RIGHT_LEG,LEFT_LEG ->{
                item = player.getItemBySlot(EquipmentSlot.LEGS);
                eq = EquipmentSlot.LEGS;
            }
            case RIGHT_FOOT,LEFT_FOOT ->{
                item = player.getItemBySlot(EquipmentSlot.FEET);
                eq = EquipmentSlot.FEET;
            }
        }
        if (item!=null){
            int amount = (int) Math.max(0,damage/2f);
            if (amount<-1)return;
            EquipmentSlot finalEq = eq;
            item.hurtAndBreak(amount,player, player1 -> player1.broadcastBreakEvent(finalEq));
        }
    }


    public void updateTemperature(Player player){
        float biomeTemp = player.level().getBiome(player.blockPosition())
                .value().getBaseTemperature();

        float envTemp = 32.5f + (biomeTemp * 7f);

        int seaLevel = player.level().getSeaLevel();
        double heightDiff = player.getY() - seaLevel;

        float heightModifier = (float) Mth.clamp(-heightDiff * 0.025f, -4f, 4f);
        envTemp += heightModifier;

        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        float blockheatBonus = 0f;

        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    checkPos.set(player.blockPosition().offset(dx, dy, dz));
                    BlockState state = player.level().getBlockState(checkPos);

                    float base = 0f;

                    if (state.is(Blocks.LAVA)) base += 3f;
                    else if (state.is(Blocks.FIRE)) base += 2f;
                    else if (state.is(Blocks.TORCH)) base += 0.5f;
                    else if (state.is(Blocks.MAGMA_BLOCK)) base += 3f;
                    else if (state.is(Blocks.CAMPFIRE)) base += 2.5f;
                    else if (state.is(Blocks.ICE)) base -= 3f;
                    else if (state.is(Blocks.POWDER_SNOW)) base -= 3f;
                    if (state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT)) {
                        base += 1f;
                    }

                    if (base != 0f) {
                        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        float falloff = (float) Math.max(0.0, 1.0 - dist / 4.0); // full at 0m, none beyond 4m
                        blockheatBonus += base * falloff;
                    }
                }
            }
        }
        blockheatBonus = Mth.clamp(blockheatBonus,-12,15);
        if (player.isSprinting()) envTemp += 1.5f;
        else if (player.isSwimming()) envTemp -= 1f;
        if (player.getFoodData().getFoodLevel() < 6) envTemp -= 0.5f;

        float generalheatbonus = 0f;

        if (player.isInWater()) generalheatbonus -= 3f; // cold water
        if (player.isOnFire()) generalheatbonus += 10f;

        float armorInsulation = 0f;
        armorInsulation = ThermalArmorHandler.getArmorInsulation(player);

        boolean skyVisible = player.level().canSeeSkyFromBelowWater(player.blockPosition());
        if (!skyVisible) {
            blockheatBonus *= 0.5f; // less effect indoors
        }

        if (player.level().isRainingAt(player.blockPosition())) generalheatbonus -= 2f;
        if (player.level().isThundering()) generalheatbonus -= 3f;
        if (player.level().isDay() && player.level().canSeeSky(player.blockPosition())) generalheatbonus += 1f;

        envTemp += (blockheatBonus + generalheatbonus);

// --- Armor insulation slows down temperature change ---
// Each point of insulation reduces how fast body temperature changes toward environment
        float INSULATION_PER_POINT = 0.05f;   // 5% slower per insulation point
        float MAX_INSULATION_SCALE = 0.85f;   // max 85% slower (still allows change)
        float MIN_RATE = 1e-5f;               // avoid stalling

// Compute insulation effect and clamp it
        float rawInsulationEffect = armorInsulation * INSULATION_PER_POINT;
        float clampedInsulationEffect = Mth.clamp(rawInsulationEffect, 0f, MAX_INSULATION_SCALE);

// Base adjustment rate — faster when in water
        float baseRate = (player.isInWater() ? 0.01f : 0.005f) / 20f;

// Reduce change rate by insulation
        float adjustmentRate = baseRate * (1f - clampedInsulationEffect);
        adjustmentRate = Math.max(adjustmentRate, MIN_RATE);

// Smoothly adjust toward target
        float targetTemp = Mth.clamp(envTemp,18,50);

        temperature += (targetTemp - temperature) * adjustmentRate;
    }

    public void calculateImmunity(){
        float temp_bonus = -36.6f+temperature;
        float blood_bonus = blood>5?(-5+blood)*5:0;
        float dirtiness_bonus = -Math.max(0,dirtyness-50);
        float antibiotics_bonus = antibiotic_timer>0?60:0;
        float hunder_bonus = hungerLevel-10;
        antibiotic_timer = Math.max(antibiotic_timer-1,0);
        immunity = 100+temp_bonus+blood_bonus+dirtiness_bonus+antibiotics_bonus+hunder_bonus;
    }

    public void calculateInfectionForLimb(Limb limb){
        float infection_progress = (float) ((immunity*getIMMUNITY_STRENGTH()) * -0.001188f +0.18f);
        if (getLimbDesinfected(limb)>0){
            infection_progress -= (0.125f*20)* getDISINFECTION_STRENGTH();
            limbStats.get(limb).desinfectionTimer -= 1;
        }
        float infection = limbStats.get(limb).infection;
        if (infection>0){
            infection +=infection_progress/20;
            infection = Mth.clamp(infection,0,100);
            limbStats.get(limb).infection = infection;
            infectionSpread(limb);
        }
        limbStats.get(limb).infection = Mth.clamp(limbStats.get(limb).infection,0,100);
    }

    public void updateDirtyness(Player player) {
        float passiveIncrease = 0.000666f;
        if (player.isSprinting())passiveIncrease *=3;
        boolean swamp =
                player.level().getBiome(player.blockPosition()).is(Biomes.SWAMP)||
                        player.level().getBiome(player.blockPosition()).is(Biomes.MANGROVE_SWAMP)||
                        player.level().getBiome(player.blockPosition()).is(Biomes.JUNGLE);


        // Dirt accumulates faster if player is hurt or bleeding
        if (getCombinedBleed()>0||swamp) passiveIncrease += 0.02f;
        BlockPos blockPosBelow = player.blockPosition().below();
        BlockState blockStateBelow = player.level().getBlockState(blockPosBelow);
        if (blockStateBelow.is(Blocks.MUD)||blockStateBelow.is(BlockTags.SAND)) passiveIncrease += 0.05f;
        if (player.isOnFire()) passiveIncrease += 0.01f;  // smoke/ash

        // Rain and clean water slowly reduce dirt
        float passiveDecrease = 0f;
        if (player.isUnderWater()) {
            if (!swamp) {
                passiveDecrease += 1f;
            }else{
                passiveIncrease +=0.5f;
            }
        }
        else if (player.level().isRainingAt(player.blockPosition())) passiveDecrease += 0.05f;

        dirtyness += (passiveIncrease - passiveDecrease) /20;

        // Clamp between 0 and 100
        dirtyness = Mth.clamp(dirtyness, 0f, 100f);
    }

    //Visuals

    private int lastAir = -1;
    private long lastTick = -1;
    private float lastRate = 0f;

    // Call every tick for the player
    public float getAirLossRate(Player player) {
        int currentAir = player.getAirSupply();
        long tick = player.level().getGameTime();

        // On first tick, just initialize
        if (lastAir == -1) {
            lastAir = currentAir;
            lastTick = tick;
            return 0f;
        }

        // Compute difference
        long deltaTicks = tick - lastTick;
        if (deltaTicks <= 0) return lastRate; // Avoid division by 0

        int diff = lastAir - currentAir;
        float rate = diff / (float) deltaTicks;

        // Update tracking
        lastAir = currentAir;
        lastTick = tick;

        // Filter small changes (air regen, mod sync issues, etc.)
        if (rate < 0) rate = 0f;

        lastRate = rate;
        return rate;
    }



}
