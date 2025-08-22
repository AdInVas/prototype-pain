package net.adinvas.prototype_pain.hitbox;


import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.tags.ModDamageTypeTags;
import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class HitboxEvents {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event){
        if (!(event.getEntity() instanceof Player player))return;
        float damageamount = event.getAmount();
        if (damageamount == Float.MAX_VALUE || Float.isNaN(damageamount) || damageamount == Float.POSITIVE_INFINITY)return;
        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)){
            Entity directEntity = event.getSource().getDirectEntity();
            if (!(directEntity instanceof Projectile projectile)) return;
            Vec3 hitPos = projectile.position();

            // Your custom hit sector logic
            HitSector hit = detectHit(player, hitPos);
            // This damage value is AFTER vanilla reductions (armor, resistance, etc.)
            float finalDamage = event.getAmount();

            // Call into your capability with final damage
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.handleProjectileDamage(hit, finalDamage);
            });
        }
        else if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            HitResult result = raytraceMeleeHit(attacker, player);
            if (result instanceof EntityHitResult entityResult && entityResult.getEntity() == player) {
                Vec3 hitPos = entityResult.getLocation();
                HitSector hit = detectHit(player, hitPos);
                List<Limb> limbList = hit.getLimbsPerSector();
                Random random = new Random();
                Limb limb = limbList.get(random.nextInt(limbList.size()));
                player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                    h.applyPain(limb,h.painFromDamage(damageamount));
                    h.applySkinDamage(limb,damageamount*0.8f);
                    h.applyMuscleDamage(limb,damageamount*0.3f);
                    if (random.nextBoolean()){
                        h.applyBleedDamage(limb,damageamount);
                    }
                });

            }
        }
       else if (event.getSource().is(DamageTypeTags.IS_EXPLOSION)){
           player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
               h.handleExplosionDamage(damageamount,event.getSource().is(ModDamageTypeTags.SHRAPNELL));
           });
       }
       else{
           player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
               h.handleRandomDamage(damageamount);
           });
        }

        event.setAmount(0);
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
