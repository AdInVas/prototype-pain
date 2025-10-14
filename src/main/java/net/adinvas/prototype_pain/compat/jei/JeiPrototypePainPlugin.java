package net.adinvas.prototype_pain.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.recipe.ModRecipes;
import net.adinvas.prototype_pain.recipe.ShapelessWithMedicalContainerRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

@JeiPlugin
public class JeiPrototypePainPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(PrototypePain.MOD_ID,"jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
       registration.addRecipeCategories(new ShapelessFluidCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        PrototypePain.LOGGER.info("Found recipes: " + recipeManager.getAllRecipesFor(ModRecipes.SHAPELESS_TYPE.get()).size());
        List<ShapelessWithMedicalContainerRecipe> shapeless =
                recipeManager.getAllRecipesFor(RecipeType.CRAFTING).stream()
                        .filter(r -> r instanceof ShapelessWithMedicalContainerRecipe)
                        .map(r -> (ShapelessWithMedicalContainerRecipe) r)
                        .toList();
        registration.addRecipes(ShapelessFluidCategory.SHAPELESS_WITH_MEDICAL_CONTAINER_RECIPE_RECIPE_TYPE,shapeless);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(
                net.minecraft.client.gui.screens.inventory.CraftingScreen.class,
                88, 32, 28, 23,
                ShapelessFluidCategory.SHAPELESS_WITH_MEDICAL_CONTAINER_RECIPE_RECIPE_TYPE
        );
    }
}
