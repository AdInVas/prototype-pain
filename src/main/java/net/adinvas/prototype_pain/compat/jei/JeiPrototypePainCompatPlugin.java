package net.adinvas.prototype_pain.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.blocks.medical_mixer.MedicalMixerScreen;
import net.adinvas.prototype_pain.item.ModItems;
import net.adinvas.prototype_pain.recipe.MedicalMixerRecipe;
import net.adinvas.prototype_pain.recipe.ModRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JeiPrototypePainCompatPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(PrototypePain.MOD_ID, "jei_recipe");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MedicalMixerCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.MedicalMixer.get()),MedicalMixerCategory.MEDICAL_MIXER_RECIPE_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

        List<MedicalMixerRecipe> recipes = manager.getAllRecipesFor(ModRecipes.MEDICAL_MIXER_RECIPE.get());
        registration.addRecipes(MedicalMixerCategory.MEDICAL_MIXER_RECIPE_TYPE,recipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(MedicalMixerScreen.class,90,43,36,10,MedicalMixerCategory.MEDICAL_MIXER_RECIPE_TYPE);
    }
}
