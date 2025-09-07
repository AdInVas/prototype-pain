package net.adinvas.prototype_pain.hitbox;


import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.tags.ModDamageTypeTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Mod.EventBusSubscriber
public class HitboxEvents {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event){
        if (!(event.getEntity() instanceof Player player))return;
        float damageamount = event.getAmount();

        if (event.getSource().is(ModDamageTypeTags.IGNORE))return;
        if (damageamount == Float.MAX_VALUE || Float.isNaN(damageamount) || damageamount == Float.POSITIVE_INFINITY)return;
        if (isAnyProjectile(event.getSource())){
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
            event.setAmount(0);
            return;
        }
       else if (event.getSource().is(DamageTypeTags.IS_EXPLOSION)){
           player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
               h.handleExplosionDamage(damageamount,event.getSource().is(ModDamageTypeTags.SHRAPNELL));
           });
            event.setAmount(0);
            return;
       } else if (event.getSource().is(ModDamageTypeTags.MAGIC)) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.handleMagicDamage(damageamount);
            });
            event.setAmount(0);
            return;
       } else if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.handleFireDamage(damageamount);
            });
            event.setAmount(0);
            return;
        }
        if (event.getSource().getEntity() instanceof LivingEntity shooter) {

            double range = 400.0;
            EntityHitResult hitPos = rayTraceLivingEntity(shooter, range);
            // Your custom hit sector logic
            HitSector hit = detectHit(player, hitPos.getLocation());
            // This damage value is AFTER vanilla reductions (armor, resistance, etc.)
            float finalDamage = event.getAmount();

            // Call into your capability with final damage
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.handleProjectileDamage(hit, finalDamage);
            });
            event.setAmount(0);
            return;
        }
        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            h.handleRandomDamage(damageamount);
        });
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
        double reach = 400.0D; // melee reach distance
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
    private static EntityHitResult raytraceEntity(LivingEntity shooter, double range) {
        Vec3 eyePos = shooter.getEyePosition(1.0F);
        Vec3 lookVec = shooter.getLookAngle();
        Vec3 endVec = eyePos.add(lookVec.scale(range));
        AABB searchBox = shooter.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.0D);

        return net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(
                shooter.level(),
                shooter,
                eyePos,
                endVec,
                searchBox,
                e -> e.isPickable() && !e.isSpectator() && e != shooter
        );
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

    public static boolean isAnyProjectile(DamageSource source) {
        // 1. Vanilla tag check
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return true;

        // 2. Direct entity instanceof check
        Entity direct = source.getDirectEntity();
        if (direct instanceof Projectile) return true;

        // 3. Ritchie's Projectile Lib precise-motion check
        TagKey<DamageType> RPL_PRECISE = TagKey.create(
                Registries.DAMAGE_TYPE,
                new ResourceLocation("ritchiesprojectilelib", "precise_motion")
        );

        if (source.is(RPL_PRECISE)) return true;

        return false;
    }


    public static EntityHitResult rayTraceLivingEntity(LivingEntity shooter, double range) {
        Level world = shooter.level();
        Vec3 start = shooter.getEyePosition(1.0F);
        Vec3 direction = shooter.getViewVector(1.0F).normalize();
        Vec3 end = start.add(direction.scale(range));

        // Raytrace for blocks first
        ClipContext blockContext = new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                shooter
        );
        BlockHitResult blockHit = world.clip(blockContext);

        Vec3 finalEnd = end;
        if (blockHit != null && blockHit.getType() == HitResult.Type.BLOCK) {
            finalEnd = blockHit.getLocation();
        }

        // Build an AABB along the ray
        AABB scanBox = shooter.getBoundingBox().expandTowards(direction.scale(range)).inflate(1.0);
        List<LivingEntity> candidates = world.getEntitiesOfClass(
                LivingEntity.class,
                scanBox,
                e -> e != shooter && e.isAlive()
        );

        LivingEntity nearest = null;
        double nearestDistSq = Double.MAX_VALUE;
        Vec3 hitPos = null;

        for (LivingEntity target : candidates) {
            AABB box = target.getBoundingBox().inflate(target.getPickRadius());
            Optional<Vec3> optHit = box.clip(start, finalEnd);
            if (optHit.isPresent()) {
                double distSq = start.distanceToSqr(optHit.get());
                if (distSq < nearestDistSq) {
                    nearestDistSq = distSq;
                    nearest = target;
                    hitPos = optHit.get();
                }
            }
        }

        if (nearest != null && hitPos != null) {
            return new EntityHitResult(nearest, hitPos);
        }

        return null; // No entity hit
    }

}
