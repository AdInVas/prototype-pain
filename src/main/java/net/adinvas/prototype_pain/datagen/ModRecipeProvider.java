package net.adinvas.prototype_pain.datagen;

import net.adinvas.prototype_pain.ModMedicalFluids;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.item.ModItems;
import net.adinvas.prototype_pain.recipe.MedicalMixerRecipe;
import net.adinvas.prototype_pain.recipe.MedicalMixerRecipeBuilder;
import net.adinvas.prototype_pain.tags.ModItemTags;
import net.adinvas.prototype_pain.tags.ModMedicalFluidTags;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fluids.FluidStack;
import sereneseasons.init.ModTags;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ItemStack waterBottle = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
        Ingredient waterBottleIngredient = Ingredient.of(waterBottle);


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.RippedDressing.get())
                .requires(Items.STRING)
                .requires(Items.STRING)
                .requires(Items.STRING)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.BandAids.get())
                .pattern("PHP")
                .define('P',Items.PAPER.asItem())
                .define('H',Items.HONEY_BOTTLE)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.Dressing.get())
                .pattern(" S ")
                .pattern("RWR")
                .pattern(" S ")
                .define('S', Items.STRING)
                .define('R', ModItems.RippedDressing.get())
                .define('W', ItemTags.WOOL)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.PlasticDressing.get())
                .requires(ModItemTags.DRESSINGS)
                .requires(Items.SLIME_BALL)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.BoneWelding.get())
                .pattern("II")
                .pattern("RI")
                .pattern("RG")
                .define('I',Items.IRON_INGOT)
                .define('R',Items.REDSTONE)
                .define('G',Items.GUNPOWDER)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.Thermometer.get())
                .pattern(" C ")
                .pattern("CRC")
                .pattern("CGC")
                .define('C',Items.COPPER_INGOT)
                .define('R',Items.REDSTONE)
                .define('G',Items.GOLD_INGOT)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.MedicalSuture.get())
                .pattern("GSS")
                .pattern("G S")
                .pattern("N S")
                .define('N',Items.IRON_NUGGET)
                .define('G',Items.GOLD_INGOT)
                .define('S',Items.STRING)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.Ice_Pack.get())
                .requires(Items.ICE)
                .requires(Items.SLIME_BALL)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.SmallMedibag.get())
                .pattern("NNN")
                .pattern("WLW")
                .define('N',Items.IRON_INGOT)
                .define('W',ItemTags.WOOL)
                .define('L',Items.LEATHER)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.MediumMedibag.get())
                .pattern("GWG")
                .pattern("SLS")
                .define('W',ItemTags.WOOL)
                .define('L',Items.LEATHER)
                .define('G',Items.GOLD_INGOT)
                .define('S',ModItems.SmallMedibag.get())
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.LargeMedibag.get())
                .pattern("DWD")
                .pattern("MNM")
                .define('N',Items.IRON_NUGGET)
                .define('W',ItemTags.WOOL)
                .define('D',Items.DIAMOND)
                .define('M',ModItems.MediumMedibag.get())
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.Splint.get())
                .requires(Items.STICK)
                .requires(Items.STICK)
                .requires(Items.STRING)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.Tweezers.get())
                .pattern("IN")
                .pattern(" I")
                .define('N',Items.IRON_NUGGET)
                .define('I',Items.IRON_INGOT)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.Tourniquet.get())
                .pattern(" I ")
                .pattern("WLW")
                .define('W',ItemTags.WOOL)
                .define('L',Items.LEATHER)
                .define('I',Items.IRON_INGOT)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.HeatPack.get())
                .requires(Items.GUNPOWDER)
                .requires(ItemTags.WOOL)
                .requires(Items.GUNPOWDER)
                .requires(Items.IRON_NUGGET)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.BrownCapMush.get())
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .define('A',ModItems.BrownCap.get())
                .define('B',Items.BOWL)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.ExperimentalTreatment.get())
                .pattern("MMM")
                .pattern("MMM")
                .pattern("GDG")
                .define('M',ModItems.BrownCapMush.get())
                .define('G', Items.GLISTERING_MELON_SLICE)
                .define('D',Items.DIAMOND)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        MedicalMixerRecipeBuilder.mixer()
                .input(Fluids.WATER,10)
                .input(new ItemStack(ModItems.GLOW_FRUIT.get(),1))
                .output(ModMedicalFluids.REACTION_LIQUID.get(),10)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"reaction_liquid"));

        MedicalMixerRecipeBuilder.mixer()
                .input(Fluids.WATER,25)
                .input(new ItemStack(Items.POPPY,1))
                .input(new ItemStack(Items.SUGAR,2))
                .output(ModMedicalFluids.OPIUM.get(),25)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"opium"));

        MedicalMixerRecipeBuilder.mixer()
                .input(Items.MILK_BUCKET,1)
                .input(Items.COCOA_BEANS,8)
                .output(ModMedicalFluids.CHOCO_MILK.get(),1000)
                .output(Items.BUCKET,1)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"chocolate_milk"));

        MedicalMixerRecipeBuilder.mixer()
                .input(ModItemTags.ALCOHOL_CREATABLE,4)
                .input(ModMedicalFluids.REACTION_LIQUID.get(),10)
                .input(Fluids.WATER,100)
                .output(ModMedicalFluids.ALCOHOL.get(),100)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"alcohol"));

        MedicalMixerRecipeBuilder.mixer()
                .input(ModItemTags.ALCOHOL_CREATABLE,4)
                .input(Fluids.WATER,100)
                .processingTime(6000)
                .output(ModMedicalFluids.ALCOHOL.get(),100)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"alcohol_slow"));

        MedicalMixerRecipeBuilder.mixer()
                .inputM(ModMedicalFluidTags.DISINFECTING,100)
                .input(ModItemTags.DRESSINGS,1)
                .processingTime(200)
                .output(ModItems.SterilizedDressing.get(),1)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"sterilized_dressing"));

        MedicalMixerRecipeBuilder.mixer()
                .input(ModMedicalFluids.OPIUM.get(),100)
                .input(Items.FERMENTED_SPIDER_EYE,1)
                .output(ModMedicalFluids.MORPHINE.get(),50)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"morphine"));

        MedicalMixerRecipeBuilder.mixer()
                .input(Items.FERMENTED_SPIDER_EYE,1)
                .input(Items.SUGAR,2)
                .input(Fluids.WATER,100)
                .output(ModMedicalFluids.HEROIN.get(),100)
                .processingTime(1200)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"heroin"));

        MedicalMixerRecipeBuilder.mixer()
                .input(Items.GLISTERING_MELON_SLICE,4)
                .input(Items.FERMENTED_SPIDER_EYE,1)
                .input(ModMedicalFluids.MORPHINE.get(),40)
                .input(ModMedicalFluids.REACTION_LIQUID.get(),10)
                .output(ModMedicalFluids.FENTANYL.get(),5)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"fentanyl"));

        MedicalMixerRecipeBuilder.mixer()
                .input(Items.SUGAR,2)
                .inputM(ModMedicalFluidTags.OPIOIDS,25)
                .output(ModMedicalFluids.PAINKILLERS.get(), 50)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"painkillers"));

        MedicalMixerRecipeBuilder.mixer()
                .input(ItemTags.FISHES,8)
                .input(Items.EGG,2)
                .input(Fluids.WATER,20)
                .input(Items.GOLD_INGOT,1)
                .output(ModMedicalFluids.BRAINGROW.get(),20)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"brain_grow"));

        MedicalMixerRecipeBuilder.mixer()
                .input(ModMedicalFluids.ALCOHOL.get(),100)
                .input(Items.GUNPOWDER,1)
                .input(ModMedicalFluids.REACTION_LIQUID.get(),10)
                .output(ModMedicalFluids.ANTISEPTIC.get(),50)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"antiseptic"));

        MedicalMixerRecipeBuilder.mixer()
                .inputM(ModMedicalFluidTags.OPIOIDS,10)
                .input(Items.SLIME_BALL,1)
                .input(Fluids.WATER,100)
                .output(ModMedicalFluids.RELIEF_CREAM.get(), 100)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"relief_cream"));

        MedicalMixerRecipeBuilder.mixer()
                .input(Fluids.WATER,250)
                .input(Items.SUGAR,1)
                .output(ModMedicalFluids.SALINE.get(), 250)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"saline"));

        MedicalMixerRecipeBuilder.mixer()
                .input(Items.BROWN_MUSHROOM,1)
                .input(Items.SUGAR,1)
                .input(Items.FERMENTED_SPIDER_EYE,1)
                .output(ModMedicalFluids.ANTIBIOTICS.get(), 10)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"antibiotics"));

        MedicalMixerRecipeBuilder.mixer()
                .input(ModMedicalFluids.ANTIBIOTICS.get(),50)
                .input(Fluids.WATER,50)
                .input(Items.FERMENTED_SPIDER_EYE,1)
                .input(ModMedicalFluids.REACTION_LIQUID.get(),10)
                .output(ModMedicalFluids.ANTISERUM.get(), 25)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"antiserum"));

        MedicalMixerRecipeBuilder.mixer()
                .input(ModMedicalFluids.ANTISERUM.get(),10)
                .input(Fluids.WATER,10)
                .output(ModMedicalFluids.ANTISERUM.get(), 20)
                .processingTime(3000)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"antiserum_grow"));

        MedicalMixerRecipeBuilder.mixer()
                .processingTime(200)
                .input(Fluids.WATER,10)
                .input(ModMedicalFluids.REACTION_LIQUID.get(),50)
                .input(Items.GUNPOWDER,1)
                .output(ModMedicalFluids.CEFTRIAXONE.get(), 20)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"ceftriaxone"));

        MedicalMixerRecipeBuilder.mixer()
                .input(Items.SPIDER_EYE,2)
                .input(Items.SUGAR,1)
                .input(Fluids.WATER,20)
                .output(ModMedicalFluids.PROCOAGULANT.get(),20)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"procoagulant"));

        MedicalMixerRecipeBuilder.mixer()
                .input(Items.SUGAR,1)
                .input(Items.GUNPOWDER,2)
                .input(Items.GLOWSTONE_DUST,1)
                .input(Fluids.WATER,20)
                .output(ModMedicalFluids.STREPTOKINASE.get(), 20)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"streptokinase"));

        MedicalMixerRecipeBuilder.mixer()
                .inputM(ModMedicalFluidTags.OPIOIDS,50)
                .input(Items.GLISTERING_MELON_SLICE,2)
                .output(ModMedicalFluids.NALOXONE.get(),25)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID,"naloxone"));


    }
}
