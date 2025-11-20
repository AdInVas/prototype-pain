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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
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
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class HitboxEvents {
    private static final Map<UUID, DamageContext> contextMap = new ConcurrentHashMap<>();

    private static class DamageContext {
        public DamageSource source;
        public Entity directEntity; // projectile, attacker, etc.
        public float preArmorAmount = -1f;
        public Vec3 projectileHitPos = null;
        // add whatever else you need (e.g. flags for special sources)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.isCanceled()) return;

        DamageSource src = event.getSource();
        UUID id = player.getUUID();

        DamageContext ctx = new DamageContext();
        ctx.source = src;
        ctx.directEntity = src.getDirectEntity();
        contextMap.put(id, ctx);

        // If projectile, compute accurate intersection now and store position
        Entity direct = ctx.directEntity;
        if (direct instanceof Projectile proj) {
            // Use your sweep logic to compute hit position against this player
            Vec3 hit = sweepProjectileStep(proj, player);
            ctx.projectileHitPos = hit;
        }

        // You can do other detection here if needed (e.g. store attacker pos)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.isCanceled()) return;

        UUID id = player.getUUID();
        DamageContext ctx = contextMap.computeIfAbsent(id, k -> new DamageContext());
        ctx.preArmorAmount = event.getAmount();

        // Do small pre-armor reductions like absorption/resistance if you want here.
        // But prefer to do the full authoritative calculation in LivingDamageEvent.
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.isCanceled()) return;

        UUID id = player.getUUID();
        DamageContext ctx = contextMap.remove(id); // consume context

        float finalDamage = event.getAmount(); // final damage after armor/enchantments
        DamageSource src = (ctx != null && ctx.source != null) ? ctx.source : event.getSource();

        // Protect against weird values
        if (Float.isNaN(finalDamage) || Float.isInfinite(finalDamage) || finalDamage <= 0f) {
            // If you want to still process zero damage for e.g. status effects, handle separately
            event.setAmount(0f);
            return;
        }

        // Example: handle freeze and fall specially (mimic your original logic)
        if (src.is(DamageTypes.FREEZE)) {
            float dmg = finalDamage;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.setTemperature(h.getTemperature() - dmg * 1.8f);
            });
            event.setAmount(0f);
            return;
        }

        if (src.is(DamageTypes.FALL)) {
            float dmg = finalDamage;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.handleFallDamage(dmg, player);
            });
            event.setAmount(0f);
            return;
        }

        // Explosion / shrapnel handling
        if (src.is(DamageTypeTags.IS_EXPLOSION)) {
            float dmg = finalDamage;
            boolean isShrapnel = src.is(ModDamageTypeTags.SHRAPNELL);
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.handleExplosionDamage(dmg, isShrapnel, player);
            });
            event.setAmount(0f);
            return;
        }

        // Projectile detection: use stored projectileHitPos if present, otherwise try direct entity
        if (isAnyProjectile(src)) {
            Vec3 hitPos = null;
            if (ctx != null && ctx.projectileHitPos != null) {
                hitPos = ctx.projectileHitPos;
            } else {
                Entity direct = src.getDirectEntity();
                if (direct instanceof Projectile proj) {
                    hitPos = sweepProjectileStep(proj, player);
                }
            }

            if (hitPos != null) {
                HitSector hit = detectHit(player, hitPos);
                float dmg = finalDamage;
                player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                    h.handleProjectileDamage(hit, dmg, player);
                });
                event.setAmount(0f);
                return;
            }
        }

        // Player attacker: try to detect hit pos via raytrace of attacker (keep your old logic)
        Entity attacker = src.getEntity();
        if (attacker instanceof Player shooter) {
            double range = 400.0;
            EntityHitResult hitPos = rayTraceLivingEntity(shooter, range);
            if (hitPos != null) {
                HitSector hit = detectHit(player, hitPos.getLocation());
                float dmg = finalDamage;
                player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                    h.handleProjectileDamage(hit, dmg, player);
                });
                event.setAmount(0f);
                return;
            }
        }

        // Magic, fire, bypass armor and other categories
        if (src.is(ModDamageTypeTags.MAGIC) || src.is(DamageTypes.MAGIC)) {
            float dmg = finalDamage;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.handleMagicDamage(dmg, (player instanceof ServerPlayer sp) ? sp : null);
            });
            event.setAmount(0f);
            return;
        }

        if (src.is(DamageTypeTags.BYPASSES_ARMOR)) {
            float dmg = finalDamage;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                h.handleRandomDamage(dmg, player);
            });
            event.setAmount(0f);
            return;
        }

        // Default fallback: standard damage routed into your system
        float dmg = finalDamage;
        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
            h.handleRandomDamage(dmg, player);
        });
        event.setAmount(0f);
    }



    //@SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void oLivingDamage(LivingHurtEvent event){
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
        damageamount = Math.max(damageamount,0);

        /*
        player.sendSystemMessage(Component.literal(
                "Source: " + event.getSource().toString()
                        + " | amt: " + damageamount
                        + " | bypassArmor: " + event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)
                        + " | bypassShield: " + event.getSource().is(DamageTypeTags.BYPASSES_SHIELD)
                        + " | bypassEnchant: " + event.getSource().is(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                        + " | ignoredTag: " + event.getSource().is(ModDamageTypeTags.IGNORE)
                        + " | absorb: " + absorb
        ));

         */
        if (event.getSource().is(ModDamageTypeTags.IGNORE))return;
        if (damageamount == Float.MAX_VALUE || Float.isNaN(damageamount) || damageamount == Float.POSITIVE_INFINITY)return;
        if (event.getSource().is(DamageTypes.FREEZE)){
            float finalDamageamount1 = damageamount;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setTemperature(h.getTemperature()- finalDamageamount1 *1.8f);
            });
            event.setAmount(0);
            return;
        }
        if (event.getSource().is(DamageTypes.FALL)){
            float finalDamage = damageamount;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.handleFallDamage(finalDamage,player);
            });
            event.setAmount(0);
            return;
        }
       if (event.getSource().is(CBCmg)||event.getSource().is(CBCmgwat)) {
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
                    h.handleExplosionDamage(finalDamage, true,player);
                });
            }
            event.setAmount(0);
            return;
    } else if (event.getSource().is(DamageTypeTags.IS_EXPLOSION)){
            float finalDamageamount = damageamount;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.handleExplosionDamage(finalDamageamount,event.getSource().is(ModDamageTypeTags.SHRAPNELL),player);
            });
            event.setAmount(0);
            return;
        }

        else if (isAnyProjectile(event.getSource())){
            Entity directEntity = event.getSource().getDirectEntity();
            if ((directEntity instanceof Projectile projectile)) {
                ;
                Vec3 hitPos = sweepProjectileStep(projectile, player);

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
                    h.handleProjectileDamage(hit, finalDamage, player);
                });
                event.setAmount(0);
                return;
            }
        }
        else if (event.getSource().is(ModDamageTypeTags.MAGIC)||event.getSource().is(DamageTypes.MAGIC)) {
            float finalDamageamount = damageamount;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.handleMagicDamage(finalDamageamount, (ServerPlayer) player);
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
        else if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)){
            float finalDamageamount = damageamount;

            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.handleRandomDamage(finalDamageamount,player);
            });
            event.setAmount(0);
            return;
        }
        //fuck you warium
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
