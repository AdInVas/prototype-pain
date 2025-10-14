package net.adinvas.prototype_pain.recipe;

import net.adinvas.prototype_pain.PrototypePain;
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


    public static final RegistryObject<RecipeSerializer<ShapelessWithMedicalContainerRecipe>> SHAPELESS_FUN_SER =
            SERIALIZERS.register("shapeless_with_medical_container",ShapelessWithMedicalContainerRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<ShapelessWithMedicalContainerRecipe>> SHAPELESS_TYPE =
            TYPES.register("shapeless_with_medical_container",()->ShapelessWithMedicalContainerRecipe.Type.INSTANCE);

    public static void register(IEventBus bus){
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }
}
