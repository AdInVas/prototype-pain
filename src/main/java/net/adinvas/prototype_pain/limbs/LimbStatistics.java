package net.adinvas.prototype_pain.limbs;

public class LimbStatistics {
    float skinHealth = 100f;//
    float muscleHealth = 100f;//
    float pain = 0f;//
    float infection = 0f;//
    float fractureTimer = 0f;//
    float dislocatedTimer = 0f;
    boolean shrapnell = false;//
    boolean hasSplint = false;//
    float bleedRate = 0f;//
    float desinfectionTimer = 0f;//
    float MinPain = 0f;//
    float finalPain = 0f;
    boolean SkinHeal = false;
    boolean MuscleHeal = false;
    boolean Tourniquet = false;
    int tourniquetTimer = 0;


    LimbStatistics(){}

    @Override
    public String toString() {
        return "LimbStatistics{" +
                "skinHealth=" + skinHealth +
                ", muscleHealth=" + muscleHealth +
                ", pain=" + pain +
                ", infection=" + infection +
                ", fractureTimer=" + fractureTimer +
                ", dislocatedTimer=" + dislocatedTimer +
                ", shrapnell=" + shrapnell +
                ", hasSplint=" + hasSplint +
                ", bleedRate=" + bleedRate +
                ", desinfectionTimer=" + desinfectionTimer +
                ", minPain=" + MinPain +
                ", finalPain=" + finalPain +
                ", skinHeal=" + SkinHeal +
                ", muscleHeal=" + MuscleHeal +
                ", tourniquet=" + Tourniquet +
                ", tourniquetTimer=" + tourniquetTimer +
                '}';
    }
}
