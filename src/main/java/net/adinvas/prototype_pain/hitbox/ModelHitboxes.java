package net.adinvas.prototype_pain.hitbox;

import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.world.phys.Vec3;

public class ModelHitboxes {
    public static OrientedBox getBoxForPart(Limb part, Vec3 playerPos, float yawDeg, float pitchDeg) {
        // Define sizes and offsets in player-local space (standing pose)
        Vec3 halfSize;
        Vec3 localOffset;
        switch (part) {
            case HEAD -> {
                halfSize = new Vec3(0.3, 0.225, 0.3);
                localOffset = new Vec3(0, 1.575, 0);
            }
            case CHEST -> {
                halfSize = new Vec3(0.3, 0.45, 0.3);
                localOffset = new Vec3(0, 1.05, 0);
            }
            case RIGHT_ARM -> {
                halfSize = new Vec3(0.15, 0.3, 0.3);
                localOffset = new Vec3(0.45, 1.35, 0);
            }
            case LEFT_ARM -> {
                halfSize = new Vec3(0.15, 0.3, 0.3);
                localOffset = new Vec3(-0.45, 1.35, 0);
            }
            case RIGHT_HAND -> {
                halfSize = new Vec3(0.15, 0.225, 0.3);
                localOffset = new Vec3(0.45, 0.825, 0);
            }
            case LEFT_HAND -> {
                halfSize = new Vec3(0.15, 0.225, 0.3);
                localOffset = new Vec3(-0.45, 0.825, 0);
            }
            case RIGHT_LEG -> {
                halfSize = new Vec3(0.15, 0.45, 0.3);
                localOffset = new Vec3(0.18, 0.45, 0);
            }
            case LEFT_LEG -> {
                halfSize = new Vec3(0.15, 0.45, 0.3);
                localOffset = new Vec3(-0.18, 0.45, 0);
            }
            case RIGHT_FOOT -> {
                halfSize = new Vec3(0.15, 0.225, 0.3);
                localOffset = new Vec3(0.18, 0.0, 0);
            }
            case LEFT_FOOT -> {
                halfSize = new Vec3(0.15, 0.225, 0.3);
                localOffset = new Vec3(-0.18, 0.0, 0);
            }
            default -> throw new IllegalArgumentException("Unknown part: " + part);
        }

        // Rotate offset around player yaw (ignore pitch for now except head)
        Vec3 rotatedOffset = rotateYaw(localOffset, yawDeg);
        Vec3 worldCenter = playerPos.add(rotatedOffset);

        return new OrientedBox(worldCenter, halfSize, yawDeg, pitchDeg);
    }

    private static Vec3 rotateYaw(Vec3 vec, float yawDeg) {
        double yaw = Math.toRadians(-yawDeg);
        double cosY = Math.cos(yaw);
        double sinY = Math.sin(yaw);
        double x = vec.x * cosY - vec.z * sinY;
        double z = vec.x * sinY + vec.z * cosY;
        return new Vec3(x, vec.y, z);
    }
}
