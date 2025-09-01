package net.adinvas.prototype_pain.tags;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypeTags {
    public static final TagKey<DamageType> SHRAPNELL = tag("shrapnel");
    public static final TagKey<DamageType> MAGIC = tag("magic");
    public static final TagKey<DamageType> IGNORE = tag("ignore");
    public static final TagKey<DamageType> ABSTRACT_PROJECTILE = tag("abstract_projectile");

    private static TagKey<DamageType> tag(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(PrototypePain.MOD_ID, name));
    }
}
