package net.adinvas.prototype_pain.hitbox;


import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class HitboxEvents {

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event){
        if (!(event.getEntity() instanceof Player player))return;
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        float damageamount = event.getAmount();
        if (damageamount == Float.MAX_VALUE || Float.isNaN(damageamount) || damageamount == Float.POSITIVE_INFINITY)return;

        HitResult result = raytraceMeleeHit(attacker, player);
        if (result instanceof EntityHitResult entityResult && entityResult.getEntity() == player) {
            Vec3 hitPos = entityResult.getLocation();
            HitSector hit = detectHit(player, hitPos);



        }
        event.setCanceled(true);
    }



    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event){
        if (!(event.getRayTraceResult()instanceof EntityHitResult entityHitResult))return;
        if (entityHitResult.getEntity() instanceof Player player){
           HitSector hit =  detectHit(player,entityHitResult.getLocation());



        }
    }

    public static HitSector detectHit(Player player, Vec3 hitpos){
        AABB box = player.getBoundingBox();

        double relY = (hitpos.y - box.minY) / box.getYsize();
        Vec3 center = player.position().add(0, player.getBbHeight() / 2, 0);
        Vec3 localHit = hitpos.subtract(center);

        float yaw = player.getYRot(); // player rotation in degrees
        double rad = Math.toRadians(-yaw);

        double localX = localHit.x * Math.cos(rad) - localHit.z * Math.sin(rad);


        HitSector hitPart;
        if (relY > 0.8) {
            hitPart = HitSector.HEAD;
        } else if (relY > 0.3) {
            // Torso region
            if (localX < 0.3) {
                hitPart = HitSector.LEFT_ARM;
            } else if (localX > 0.7) {
                hitPart = HitSector.RIGHT_ARM;
            } else {
                hitPart = HitSector.TORSO;
            }
        } else {
            hitPart = HitSector.LEGS;
        }

        return hitPart;
    }

    private static HitResult raytraceMeleeHit(LivingEntity attacker, Entity target) {
        double reach = 3.0D; // melee reach distance
        Vec3 eyePos = attacker.getEyePosition(1.0F);
        Vec3 lookVec = attacker.getLookAngle();
        Vec3 endVec = eyePos.add(lookVec.scale(reach));

        return target.level().clip(new ClipContext(
                eyePos, endVec,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                attacker
        ));
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event){
        if (event.getEntity() instanceof Player player) {
            float distance = event.getDistance();
            float damageMultiplier = event.getDamageMultiplier();

            // Calculate vanilla fall damage
            float damage = (distance - 3.0F) * damageMultiplier;

            if (damage > 0) {
                // Cancel vanilla damage
                event.setCanceled(true);

                // Call your custom fall damage handler
               player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                   h.handleFallDamage(damage);
               });
            }
        }
    }

}
