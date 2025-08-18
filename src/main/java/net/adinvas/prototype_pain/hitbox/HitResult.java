package net.adinvas.prototype_pain.hitbox;

import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class HitResult {
        public final Limb part;
        public final DamageSource type;
        public final float damageValue;

        public HitResult(Limb part, DamageSource type, float damageValue) {
            this.part = part;
            this.type = type;
            this.damageValue = damageValue;
        }
}
