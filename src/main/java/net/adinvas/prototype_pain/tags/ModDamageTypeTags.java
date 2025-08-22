package net.adinvas.prototype_pain.tags;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypeTags {
    public static final TagKey<DamageType> SHRAPNELL = tag("shrapnel");

    private static TagKey<DamageType> tag(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(PrototypePain.MOD_ID, name));
    }
}
