package net.adinvas.prototype_pain.limbs;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

public enum AdvancedDamageTypes {
    FALL,
    BLUNT,
    PIERCING,
    SLASHING,
    PROJECTILE,
    EXPLOSION,
    FIRE,
    GENERIC;

    public static AdvancedDamageTypes damageTypeFromDamageSource(DamageSource src){
        boolean Blunt =
                        src.is(DamageTypes.FLY_INTO_WALL) ||
                        src.is(DamageTypes.FALLING_BLOCK) ||
                        src.is(DamageTypes.FALLING_ANVIL) ||
                        src.is(DamageTypes.FALLING_STALACTITE) ||
                        src.is(DamageTypes.CRAMMING) ||
                        src.is(DamageTypes.SONIC_BOOM);

        boolean Piercing =
                src.is(DamageTypes.STING) ||
                        src.is(DamageTypes.CACTUS) ||
                        src.is(DamageTypes.SWEET_BERRY_BUSH) ||
                        src.is(DamageTypes.THORNS) ||
                        src.is(DamageTypes.STALAGMITE);

        boolean Slashing =
                src.is(DamageTypes.MOB_ATTACK) ||
                        src.is(DamageTypes.MOB_ATTACK_NO_AGGRO) ||
                        src.is(DamageTypes.PLAYER_ATTACK);

        boolean Projectile =
                src.is(DamageTypes.ARROW) ||
                        src.is(DamageTypes.TRIDENT) ||
                        src.is(DamageTypes.MOB_PROJECTILE) ||
                        src.is(DamageTypes.FIREWORKS) ||
                        src.is(DamageTypes.FIREBALL) ||
                        src.is(DamageTypes.UNATTRIBUTED_FIREBALL) ||
                        src.is(DamageTypes.WITHER_SKULL) ||
                        src.is(DamageTypes.THROWN) ||
                        src.is(DamageTypes.INDIRECT_MAGIC);

        boolean Explosion =
                src.is(DamageTypes.EXPLOSION) ||
                        src.is(DamageTypes.PLAYER_EXPLOSION);

        boolean Fire =
                src.is(DamageTypes.IN_FIRE) ||
                        src.is(DamageTypes.LIGHTNING_BOLT) ||
                        src.is(DamageTypes.ON_FIRE) ||
                        src.is(DamageTypes.LAVA) ||
                        src.is(DamageTypes.HOT_FLOOR) ||
                        src.is(DamageTypes.BAD_RESPAWN_POINT);

        if (Blunt)return BLUNT;
        if (Piercing)return PIERCING;
        if (Slashing)return SLASHING;
        if (Projectile)return PROJECTILE;
        if (Explosion)return EXPLOSION;
        if (Fire)return FIRE;
        if (src.is(DamageTypes.FALL)) return FALL;
        return GENERIC;
    }
}