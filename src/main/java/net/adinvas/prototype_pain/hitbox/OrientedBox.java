package net.adinvas.prototype_pain.hitbox;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class OrientedBox {
    public final Vec3 center;
    public final Vec3 halfSize;
    public final float yaw;
    public final float pitch;

    public OrientedBox(Vec3 center, Vec3 halfSize, float yaw, float pitch) {
        this.center = center;
        this.halfSize = halfSize;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Optional<Vec3> rayIntersect(Vec3 rayStart, Vec3 rayEnd) {
        // Transform ray into box-local space
        Vec3 localStart = worldToLocal(rayStart);
        Vec3 localEnd = worldToLocal(rayEnd);

        AABB aabb = new AABB(halfSize.scale(-1), halfSize);
        Optional<Vec3> hit = aabb.clip(localStart, localEnd);
        return hit.map(this::localToWorld);
    }

    private Vec3 worldToLocal(Vec3 point) {
        Vec3 translated = point.subtract(center);

        double yawRad = Math.toRadians(-yaw);
        double cosY = Math.cos(yawRad), sinY = Math.sin(yawRad);
        double xzX = translated.x * cosY - translated.z * sinY;
        double xzZ = translated.x * sinY + translated.z * cosY;

        double pitchRad = Math.toRadians(-pitch);
        double cosP = Math.cos(pitchRad), sinP = Math.sin(pitchRad);
        double y = translated.y * cosP - xzZ * sinP;
        double z = translated.y * sinP + xzZ * cosP;

        return new Vec3(xzX, y, z);
    }

    private Vec3 localToWorld(Vec3 point) {
        double pitchRad = Math.toRadians(pitch);
        double cosP = Math.cos(pitchRad), sinP = Math.sin(pitchRad);
        double y = point.y * cosP - point.z * sinP;
        double z1 = point.y * sinP + point.z * cosP;

        double yawRad = Math.toRadians(yaw);
        double cosY = Math.cos(yawRad), sinY = Math.sin(yawRad);
        double x = point.x * cosY - z1 * sinY;
        double z = point.x * sinY + z1 * cosY;

        return new Vec3(x, y, z).add(center);
    }
}
