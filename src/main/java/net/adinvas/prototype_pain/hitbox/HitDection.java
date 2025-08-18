package net.adinvas.prototype_pain.hitbox;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class HitDection {


    public static HitResult detecthit(Entity attacker, Player target, DamageSource source,float baseDamage){
        Vec3 start, end;
        if (source.isIndirect() && attacker != null){
            start = attacker.position();
            end = start.add(attacker.getDeltaMovement().scale(5.0));
        } else if (attacker != null) {
            start = attacker.getEyePosition(1.0f);
            end = start.add(attacker.getLookAngle().scale(6.0));
        }else {
            return null;
        }

        float yaw = target.getYRot();
        float pitch = target.getXRot();
        Vec3 targetPos = target.position();

        Limb hitLimb = null;
        double closestDist = Double.MAX_VALUE;
        for (Limb limb: Limb.values()){
            OrientedBox obb= ModelHitboxes.getBoxForPart(limb,targetPos,yaw,pitch);
            Optional<Vec3> intersection = obb.rayIntersect(start,end);

            if (intersection.isPresent()){
                double dist = start.distanceToSqr(intersection.get());
                if (dist<closestDist){
                    closestDist = dist;
                    hitLimb = limb;
                }
            }
        }

        if (hitLimb==null){
            return null;
        }
        return new HitResult(hitLimb,source,baseDamage);
    }
}
