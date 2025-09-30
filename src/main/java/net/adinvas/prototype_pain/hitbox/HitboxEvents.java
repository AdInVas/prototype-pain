package net.adinvas.prototype_pain.hitbox;


import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.tags.ModDamageTypeTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;
@Mod.EventBusSubscriber
public class HitboxEvents {

    @SubscribeEvent
    public static void onLivingDamage(LivingHurtEvent event){
        if (!(event.getEntity() instanceof Player player))return;
        float absorb = player.getAbsorptionAmount();
        float damageamount = event.getAmount();

        if (player.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
            int amp = player.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier();
            float reduction = 0.2f * (amp + 1); // 20% per level
            damageamount *= (1.0f - reduction);
        }
        if (absorb>0){
            float reduction = Math.min(absorb, damageamount*0.75f);
            player.setAbsorptionAmount(absorb-reduction);
            damageamount -= reduction;
        }
        if (event.getSource().is(ModDamageTypeTags.IGNORE))return;
        if (damageamount == Float.MAX_VALUE || Float.isNaN(damageamount) || damageamount == Float.POSITIVE_INFINITY)return;
        if (event.getSource().is(DamageTypes.FALL)){
            float finalDamage = damageamount;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.handleFallDamage(finalDamage,player);
            });
            event.setAmount(0);
            return;
        }
        if (event.getSource().is(CBCgrape)||event.getSource().is(CBCshrap)){
            float finalDamage = damageamount;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.handleProjectileDamage(HitSector.getCBCChances(), finalDamage,player);
            });
            event.setAmount(0);
            return;
        } else if (event.getSource().is(CBCmg)||event.getSource().is(CBCmgwat)) {
            float finalDamage = damageamount;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.handleProjectileDamage(HitSector.getCBCChances(), finalDamage,player);
            });
            event.setAmount(0);
            return;
        } else if (event.getSource().is(CBCproj)||event.getSource().is(CBCprojbig)||event.getSource().is(CBCtraff)) {
            if (damageamount<5){
                float finalDamage = damageamount*15;

                player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                    h.handleProjectileDamage(HitSector.getCBCChances(), finalDamage,player);
                    h.handleProjectileDamage(HitSector.getCBCChances(), finalDamage,player);
                    h.handleProjectileDamage(HitSector.getCBCChances(), finalDamage,player);
                    h.handleProjectileDamage(HitSector.getCBCChances(), finalDamage,player);
                    h.handleProjectileDamage(HitSector.getCBCChances(), finalDamage,player);
                    h.handleProjectileDamage(HitSector.getCBCChances(), finalDamage,player);
                    h.handleProjectileDamage(HitSector.getCBCChances(), finalDamage,player);
                    h.handleProjectileDamage(HitSector.getCBCChances(), finalDamage,player);
                });
            }else {
                float finalDamage = damageamount;
                player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                    h.handleProjectileDamage(HitSector.getCBCChances(), finalDamage,player);
                });
            }
            event.setAmount(0);
            return;
    } else if (isAnyProjectile(event.getSource())){
            Entity directEntity = event.getSource().getDirectEntity();
            if (!(directEntity instanceof Projectile projectile)) return;
            Vec3 hitPos = sweepProjectileStep(projectile,player);

            /*
            if (event.getSource().getEntity() instanceof Player pp){
                pp.sendSystemMessage(Component.literal("hitpos "+hitPos));
                pp.sendSystemMessage(Component.literal("projspeed "+projectile.getDeltaMovement().length()));
            }

             */


            // Your custom hit sector logic
            HitSector hit = detectHit(player, hitPos);
            // This damage value is AFTER vanilla reductions (armor, resistance, etc.)
            float finalDamage = damageamount;

            // Call into your capability with final damage
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.handleProjectileDamage(hit, finalDamage,player);
            });
            event.setAmount(0);
            return;
        }
       else if (event.getSource().is(DamageTypeTags.IS_EXPLOSION)){
            float finalDamageamount = damageamount;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
               h.handleExplosionDamage(finalDamageamount,event.getSource().is(ModDamageTypeTags.SHRAPNELL),player);
           });
            event.setAmount(0);
            return;
       } else if (event.getSource().is(ModDamageTypeTags.MAGIC)||event.getSource().is(DamageTypes.MAGIC)) {
            float finalDamageamount = damageamount;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.handleMagicDamage(finalDamageamount);
            });
            event.setAmount(0);
            return;
       } else if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
            float finalDamageamount = damageamount;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                    h.handleFireDamage(finalDamageamount,player);
            });
            event.setAmount(0);
            return;
        }
        else if (event.getSource().getEntity() instanceof Player shooter) {
            double range = 400.0;
            EntityHitResult hitPos = rayTraceLivingEntity(shooter, range);
            // Your custom hit sector logic
            HitSector hit = detectHit(player, hitPos.getLocation());
            // This damage value is AFTER vanilla reductions (armor, resistance, etc.)
            float finalDamage = event.getAmount();
            // Call into your capability with final damage
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.handleProjectileDamage(hit, finalDamage,player);
            });
            event.setAmount(0);
            return;
        }
        float finalDamageamount = damageamount;

        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            h.handleRandomDamage(finalDamageamount,player);
        });

        event.setAmount(0);
    }


    @SubscribeEvent
    public static void onHeal(LivingHealEvent event){
        if (event.getEntity() instanceof Player player) {
            float amount = event.getAmount();
            event.setAmount(0);
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.handleMagicHeal(amount);
            });
        }
    }

    public static Vec3 sweepProjectileStep(Projectile proj, Player target) {
        Vec3 prev = new Vec3(proj.xo, proj.yo, proj.zo); // last tick position
        Vec3 motion = proj.getDeltaMovement();
        Vec3 step = motion.scale(0.25); // break into 4 sub-steps per tick

        AABB box = target.getBoundingBox().inflate(0.05);

        Vec3 pos = prev;
        int steps = (int) Math.ceil(1.0 / 0.25); // 4 steps → adjust if needed

        for (int i = 0; i < steps; i++) {
            Vec3 next = pos.add(step);

            Optional<Vec3> hit = box.clip(pos, next);
            if (hit.isPresent()) {
                return hit.get(); // return exact intersection
            }

            pos = next;
        }

        // No hit → fallback to *final sub-step* (closer to true position)
        return pos;
    }



    public static HitSector detectHit(Player player, Vec3 hitpos) {
        AABB box = player.getBoundingBox();

        // relative vertical position: 0 = feet, 1 = top of head
        double relY = (hitpos.y - box.minY) / box.getYsize();

        // shift hit into player-local space (centered on body)
        Vec3 center = new Vec3(
                (box.minX + box.maxX) / 2.0,
                box.minY + player.getBbHeight() / 2.0,
                (box.minZ + box.maxZ) / 2.0
        );
        Vec3 localHit = hitpos.subtract(center);

        // rotate into player-facing space so +Z = forward, +X = right
        float yaw = player.getYRot(); // degrees
        double rad = Math.toRadians(-yaw);
        double localX = localHit.x * Math.cos(rad) - localHit.z * Math.sin(rad);
        double localZ = localHit.x * Math.sin(rad) + localHit.z * Math.cos(rad);

        // width for left/right detection
        double halfWidth = player.getBbWidth() / 2.0;

        HitSector hitPart;
        if (relY > 0.8) {
            hitPart = HitSector.HEAD;
        } else if (relY > 0.3) {
            // torso height band
            if (localX < -0.2 * halfWidth) {
                hitPart = HitSector.LEFT_ARM;
            } else if (localX > 0.2 * halfWidth) {
                hitPart = HitSector.RIGHT_ARM;
            } else {
                hitPart = HitSector.TORSO;
            }
        } else {
            hitPart = HitSector.LEGS;
        }

        return hitPart;
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteract event){
        if (!(event.getTarget() instanceof Player target)) return;
        Player actor = event.getEntity();

        if (actor.level().isClientSide) return;

        // Only when sneaking
        if (!actor.isShiftKeyDown()) return;

        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
            if (h.getContiousness() <= 4) {
                // Vector from target → actor
                double dx = actor.getX() - target.getX();
                double dz = actor.getZ() - target.getZ();
                double dist = Math.sqrt(dx * dx + dz * dz);

                if (dist > 0.001) {
                    double strength = 0.25; // tweak to taste

                    double dy = 0.0;

                    // If dragger is at least 0.75 blocks higher than target → add bump
                    if (actor.getY() - target.getY() >= 0.75) {
                        dy = 0.25; // tweak to taste
                    }

                    target.setDeltaMovement(
                            target.getDeltaMovement().add(dx / dist * strength, dy, dz / dist * strength)
                    );
                    target.hurtMarked = true; // force motion sync
                }

                event.setCanceled(true);
            }
        });
    }


    public static boolean isAnyProjectile(DamageSource source) {
        // 1. Vanilla tag check
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return true;

        // 2. Direct entity instanceof check
        Entity direct = source.getDirectEntity();
        if (direct instanceof Projectile) return true;

        return false;
    }

    static ResourceKey<DamageType> CBCproj = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("createbigcannons","cannon_projectile"));
    static ResourceKey<DamageType> CBCprojbig = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("createbigcannons","big_cannon_projectile"));
    static ResourceKey<DamageType> CBCmg = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("createbigcannons","machine_gun_fire"));
    static ResourceKey<DamageType> CBCmgwat = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("createbigcannons","machine_gun_fire_in_water"));
    static ResourceKey<DamageType> CBCtraff = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("createbigcannons","traffic_cone"));
    static ResourceKey<DamageType> CBCshrap = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("createbigcannons","shrapnel"));
    static ResourceKey<DamageType> CBCgrape = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("createbigcannons","grapeshot"));






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
