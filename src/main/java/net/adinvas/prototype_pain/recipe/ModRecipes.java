package net.adinvas.prototype_pain.recipe;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, PrototypePain.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, PrototypePain.MOD_ID);

    public static final RegistryObject<RecipeType<MedicalMixerRecipe>> MEDICAL_MIXER_RECIPE=
            TYPES.register("medical_mixer_recipe",()->RecipeType.simple(new ResourceLocation(PrototypePain.MOD_ID,"medical_mixer_recipe")));
    public static final RegistryObject<RecipeSerializer<MedicalMixerRecipe>> MEDICAL_MIXER_RECIPE_SERIALIZER =
            SERIALIZERS.register("medical_mixer_recipe", MedicalMixerRecipeSerializer::new);


    public static void register(IEventBus bus){
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }
}
