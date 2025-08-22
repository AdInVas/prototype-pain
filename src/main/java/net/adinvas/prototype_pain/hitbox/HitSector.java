package net.adinvas.prototype_pain.hitbox;

import net.adinvas.prototype_pain.limbs.Limb;

import java.util.ArrayList;
import java.util.List;

public enum HitSector {
    HEAD,
    TORSO,
    LEFT_ARM,
    RIGHT_ARM,
    LEGS;

    public List<Limb> getLimbsPerSector(){
        List<Limb> limbList = new ArrayList<>();
        switch (this){
            case RIGHT_ARM -> {
                limbList.add(Limb.RIGHT_ARM);
                limbList.add(Limb.RIGHT_HAND);
            }
            case HEAD -> {
                limbList.add(Limb.HEAD);
            }
            case TORSO -> {
                limbList.add(Limb.CHEST);
            }
            case LEFT_ARM -> {
                limbList.add(Limb.LEFT_ARM);
                limbList.add(Limb.LEFT_HAND);
            }
            case LEGS -> {
                limbList.add(Limb.LEFT_LEG);
                limbList.add(Limb.LEFT_FOOT);
                limbList.add(Limb.RIGHT_FOOT);
                limbList.add(Limb.RIGHT_LEG);
            }
        }
        return limbList;
    }
}
