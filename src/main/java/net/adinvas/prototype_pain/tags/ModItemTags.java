package net.adinvas.prototype_pain.tags;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;

public class ModItemTags {

    public static TagKey<Item> ARMOR_FULL_ARM = tag("armorfullarm");
    public static TagKey<Item> ARMOR_CHEST_ONLY = tag("armorchestonly");



    private static TagKey<Item> tag(String name) {
        return ItemTags.create(new ResourceLocation(PrototypePain.MOD_ID,name));
    }
}
