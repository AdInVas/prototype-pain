package net.adinvas.prototype_pain.limbs;

public class LimbStatistics {
    public enum Shrapnells {ARROW, NEEDLE,NONE,SMALL_MANY,SMALL_ONE}
    float skinHealth = 100f;//
    float muscleHealth = 100f;//
    float pain = 0f;//
    float infection = 0f;//
    float fracture = 0f;//
    float dislocation = 0f;
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
    boolean amputated = false;


    LimbStatistics(){}

    @Override
    public String toString() {
        return "LimbStatistics{" +
                "skinHealth=" + skinHealth +
                ", muscleHealth=" + muscleHealth +
                ", pain=" + pain +
                ", infection=" + infection +
                ", fractureTimer=" + fracture +
                ", dislocatedTimer=" + dislocation +
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
