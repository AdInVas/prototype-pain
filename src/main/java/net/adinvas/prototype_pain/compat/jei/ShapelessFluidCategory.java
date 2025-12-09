package net.adinvas.prototype_pain.compat.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.adinvas.prototype_pain.item.IMedicalFluidContainer;
import net.adinvas.prototype_pain.item.ModItems;
import net.adinvas.prototype_pain.recipe.MedicalIngridient;
import net.adinvas.prototype_pain.recipe.ShapelessWithMedicalContainerRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShapelessFluidCategory implements IRecipeCategory<ShapelessWithMedicalContainerRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(PrototypePain.MOD_ID,"shapeless_with_medical_container");
    public static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");


    public static final RecipeType<ShapelessWithMedicalContainerRecipe> SHAPELESS_WITH_MEDICAL_CONTAINER_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, ShapelessWithMedicalContainerRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ShapelessFluidCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 29, 16, 116, 54);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.MakeshiftLRD.get()));
    }


    @Override
    public RecipeType<ShapelessWithMedicalContainerRecipe> getRecipeType() {
        return SHAPELESS_WITH_MEDICAL_CONTAINER_RECIPE_RECIPE_TYPE;
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public Component getTitle() {
        return Component.translatable("prototype_pain.gui.fluid_crafting");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    @Nullable
    public IDrawable getBackground() {
        return  this.background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, ShapelessWithMedicalContainerRecipe recipe, IFocusGroup iFocusGroup) {
        int index=0;
        int startX = 0;
        int startY = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (index < recipe.getMedIngredients().size()) {
                    MedicalIngridient mi = recipe.getMedIngredients().get(index);
                    int x = startX + col * 18;
                    int y = startY + row * 18;

                    // Convert Ingredient to ItemStack
                    ItemStack displayStack = mi.ingredient.getItems().length > 0
                            ? mi.ingredient.getItems()[0].copy()
                            : ItemStack.EMPTY;


                    // Add fluid NBT if it's a container
                    if (mi.isFluidContainer && displayStack.getItem() instanceof IMedicalFluidContainer container) {
                        if (mi.fluidId != null && mi.fluidAmount != 0) {
                                List<ItemStack> itemStacks = new ArrayList<>();
                                MedicalFluid fluid = MedicalFluids.get(mi.fluidId);
                                if (fluid != null) {
                                    for (ItemStack stack : mi.ingredient.getItems()){
                                        ItemStack copyStack = stack.copy(); // <-- copy first!
                                        container.addFluid(copyStack, Math.abs(mi.fluidAmount), fluid);
                                        itemStacks.add(copyStack);
                                    }
                                }
                                iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1+x, 1+y)
                                        .addItemStacks(itemStacks);
                                index++;
                                continue;
                        } else if (mi.fluidTag !=null && mi.fluidAmount != 0) {
                            List<ItemStack> itemStacks = new ArrayList<>();
                            for (MedicalFluid f : MedicalFluids.getTag(mi.fluidTag)){
                                ItemStack tempstack = displayStack.copy();
                                container.addFluid(tempstack,Math.abs(mi.fluidAmount),f);
                                itemStacks.add(tempstack);
                            }
                            iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1+x, 1+y)
                                    .addItemStacks(itemStacks);
                            index++;
                            continue;
                        }
                    }
                    // Add slot to JEI
                    iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1+x, 1+y)
                            .addIngredients(mi.ingredient);
                }
                index++;
            }
        }

        // Output slot
        ItemStack output = recipe.getResultItem(null).copy(); // base output
        List<MedicalIngridient> ingredients = recipe.getMedIngredients();

// 1. Check for copy ingredients
        for (MedicalIngridient mi : ingredients) {
            if (mi.copy) {
                ItemStack copyStack = mi.ingredient.getItems().length > 0
                        ? mi.ingredient.getItems()[0].copy()
                        : ItemStack.EMPTY;
                if (!copyStack.isEmpty()) {
                    output.setTag(copyStack.getTag() != null ? copyStack.getTag().copy() : null);
                }
                break; // only first copy ingredient
            }
        }

// 2. Apply fluid if needed
        if (output.getItem() instanceof IMedicalFluidContainer container && recipe.resultFluidId != null) {
            MedicalFluid fluid = MedicalFluids.get(recipe.resultFluidId);
            if (fluid != null) {
                container.addFluid(output, recipe.resultFluidAmount, fluid);
            }
        }

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 94+1, 18+1)
                .addItemStack(output);
    }


}
