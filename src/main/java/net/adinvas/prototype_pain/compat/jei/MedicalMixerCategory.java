package net.adinvas.prototype_pain.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.adinvas.prototype_pain.ModMedicalRegistry;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.ModFluids;
import net.adinvas.prototype_pain.item.ModItems;
import net.adinvas.prototype_pain.recipe.MedicalMixerRecipe;
import net.adinvas.prototype_pain.recipe.ingridients.FluidIngredient;
import net.adinvas.prototype_pain.recipe.ingridients.ItemIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MedicalMixerCategory implements IRecipeCategory<MedicalMixerRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(PrototypePain.MOD_ID,"medical_mixer_recipe");
    private static final ResourceLocation TEX =
            new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/medical_mixer_gui.png");


    public static final RecipeType<MedicalMixerRecipe> MEDICAL_MIXER_RECIPE_TYPE =
            new RecipeType<>(UID,MedicalMixerRecipe.class);


    private final IDrawable background;
    private final IDrawable icon;

    public MedicalMixerCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEX,5,5,206,86);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,new ItemStack(ModItems.MedicalMixer.get()));
        PrototypePain.LOGGER.info("MADE RECIPE TYPE");
    }




    @Override
    public RecipeType<MedicalMixerRecipe> getRecipeType() {
        return MEDICAL_MIXER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.prototype_pain.medical_mixer");
    }

    @Override
    @Nullable
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MedicalMixerRecipe medicalMixerRecipe, IFocusGroup iFocusGroup) {
        int xStartInput = 3;
        int yInput = 24;
        int spacing = 20; // horizontal space between tanks
        int width = 16;
        int height = 38;
        int capacity = 1000;



        for (int i = 0; i < medicalMixerRecipe.getFluidInputs().size(); i++) {
            FluidIngredient ingredient = medicalMixerRecipe.getFluidInputs().get(i);
            if (ingredient.isTagged()){
                if (ingredient.isMedical()){
                    TagKey<MedicalFluid> tagKey = ingredient.getMedicalTag();
                    int amount = ingredient.getAmount();
                    Set<MedicalFluid> fluidSet = ModMedicalRegistry.REGISTRY.get().tags().getTag(tagKey).stream().collect(Collectors.toSet());
                    IRecipeSlotBuilder slotbuilder = builder.addSlot(RecipeIngredientRole.INPUT, xStartInput + i * spacing, yInput);
                    for (MedicalFluid fluid: fluidSet){
                        FluidStack stack = new FluidStack(ModFluids.SRC_MEDICAL.get(),amount);
                        stack.getOrCreateTag().putString("MedicalId",fluid.getRegistryId().toString());
                        slotbuilder.addFluidStack(stack.getFluid(), amount, stack.getTag());
                    }
                    slotbuilder.setFluidRenderer(capacity, false, width, height);
                }else {
                    TagKey<Fluid> tagKey = ingredient.getFluidTag();
                    int amount = ingredient.getAmount();
                    Set<Fluid> fluidSet = ForgeRegistries.FLUIDS.tags().getTag(tagKey).stream().collect(Collectors.toSet());
                    IRecipeSlotBuilder slotbuilder = builder.addSlot(RecipeIngredientRole.INPUT, xStartInput + i * spacing, yInput);
                    for (Fluid fluid: fluidSet){
                        slotbuilder.addFluidStack(fluid, amount, new CompoundTag());
                    }
                    slotbuilder.setFluidRenderer(capacity, false, width, height);
                }
            }else {
                FluidStack stack = ingredient.getAsFluidStack();
                builder.addSlot(RecipeIngredientRole.INPUT, xStartInput + i * spacing, yInput)
                        .addFluidStack(stack.getFluid(), stack.getAmount(), stack.getTag())
                        .setFluidRenderer(capacity, false, width, height);
            }
        }

// --- Fluid Output Tanks ---
        int xStartOutput = 147;
        int yOutput = 24;

        for (int i = 0; i < medicalMixerRecipe.getFluidOutputs().size(); i++) {
            FluidStack stack = medicalMixerRecipe.getFluidOutputs().get(i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, xStartOutput + i * spacing, yOutput)
                    .addFluidStack(stack.getFluid(), stack.getAmount(), stack.getTag())
                    .setFluidRenderer(capacity, false, width, height);
        }

        int[] inputX = {77, 95, 113, 86, 104};
        int[] inputY = {3, 3, 3, 21, 21};
        for (int i = 0; i < medicalMixerRecipe.getItemInputs().size() && i < 5; i++) {
            ItemIngredient ingredient = medicalMixerRecipe.getItemInputs().get(i);
            if (ingredient.isTagged()){
                IRecipeSlotBuilder slotbuilder = builder.addSlot(RecipeIngredientRole.INPUT, inputX[i], inputY[i]);
                Set<Item> items = ForgeRegistries.ITEMS.tags().getTag(ingredient.getTag()).stream().collect(Collectors.toSet());
                for (Item item : items){
                    ItemStack stack = new ItemStack(item,ingredient.getCount());
                    slotbuilder.addItemStack(stack);
                }

            }else{
                builder.addSlot(RecipeIngredientRole.INPUT, inputX[i], inputY[i])
                        .addItemStack(new ItemStack(medicalMixerRecipe.getItemInputs().get(i).getItem().getItem(),medicalMixerRecipe.getItemInputs().get(i).getCount()));
            }
        }

        // --- Output Items (next 5) ---
        int[] outputX = {86, 104, 77, 95, 113};
        int[] outputY = {49, 49, 67, 67, 67};
        for (int i = 0; i < medicalMixerRecipe.getItemOutputs().size() && i < 5; i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, outputX[i], outputY[i])
                    .addItemStack(medicalMixerRecipe.getItemOutputs().get(i));
        }
    }

    @Override
    public void draw(MedicalMixerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int xStartOutput = 147;
        int yOutput = 9;

        int totalSeconds = recipe.getProcessingTime()/20;

        guiGraphics.drawString(Minecraft.getInstance().font, Util.formatDuration(totalSeconds),xStartOutput,yOutput,0xFFFFFF);

        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }


}
