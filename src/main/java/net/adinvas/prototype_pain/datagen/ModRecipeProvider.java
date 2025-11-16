package net.adinvas.prototype_pain.datagen;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.adinvas.prototype_pain.item.ModItems;
import net.adinvas.prototype_pain.recipe.ShapelessWithMedicalContainerRecipe;
import net.adinvas.prototype_pain.recipe.ShapelessWithMedicalContainerRecipeBuilder;
import net.adinvas.prototype_pain.tags.ModItemTags;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.data.ForgeItemTagsProvider;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ItemStack waterBottle = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
        Ingredient waterBottleIngredient = Ingredient.of(waterBottle);

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.OpiumVial.get())
                .requiresTag("prototype_pain:vial_items",true,true,true)
                .requires(Items.POPPY)
                .requires(Items.SUGAR)
                .resultFluid(MedicalFluids.OPIUM.getId(), 25f)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "opium"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.Alcohol.get())
                .requires(waterBottleIngredient)
                .resultFluid(MedicalFluids.WATER.getId(), 250)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "water"));

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

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.BruiseKit.get())
                .requires(ModItemTags.DRESSINGS)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluidTag("prototype_pain:opioids",-25)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluid("prototype_pain:reaction_liquid",-10)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID, "bruise_kit"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.AlganateDressing.get())
                .requires(ItemTags.WOOL)
                .requires(ItemTags.WOOL)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluidTag("prototype_pain:antiseptics",-10)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID, "alganate_dressing"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.MedicalGauze.get())
                .requires(ItemTags.WOOL)
                .requires(Items.STRING)
                .requires(Items.STRING)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluidTag("prototype_pain:opioids",-10)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID, "medical_gauze"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.ReactionVial.get())
                .requires(ModItems.GLOW_FRUIT.get())
                .requires(ModItemTags.VIAL_ITEMS,true,true,true)
                .resultFluid("prototype_pain:reaction_liquid",10)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID, "reaction_liquid"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.PlasticDressing.get())
                .requires(ModItemTags.DRESSINGS)
                .requires(Items.SLIME_BALL)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.SterilizedDressing.get())
                .requires(ModItemTags.DRESSINGS)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluidTag("prototype_pain:antiseptics",-100)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID, "sterilized_dressing"));

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

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.LRD.get())
                .requires(Items.DIAMOND)
                .requires(ModItems.MakeshiftLRD.get())
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluidTag("prototype_pain:antiseptics",-50)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluidTag("prototype_pain:opioids",-100)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluid("prototype_pain:reaction_liquid",-10)
                .requires(Items.IRON_INGOT)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID, "lrd"));


        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.MakeshiftLRD.get())
                .requires(Items.COPPER_INGOT)
                .requires(Items.COPPER_INGOT)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluidTag("prototype_pain:antiseptics",-50)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluid("prototype_pain:reaction_liquid",-10)
                .requires(Items.IRON_INGOT)
                .save(consumer,new ResourceLocation(PrototypePain.MOD_ID, "makeshift_lrd"));

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

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.Saline.get())
                .requires(waterBottleIngredient)
                .requires(waterBottleIngredient)
                .requires(waterBottleIngredient)
                .requires(Items.SUGAR)
                .requires(Items.SUGAR)
                .resultFluid(MedicalFluids.SALINE.getId(), 750)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "saline"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.Alcohol.get())
                .requires(waterBottleIngredient)
                .requires(ModItemTags.ALCOHOL_CREATABLE)
                .requires(ModItemTags.ALCOHOL_CREATABLE)
                .requires(ModItemTags.ALCOHOL_CREATABLE)
                .requires(ModItemTags.ALCOHOL_CREATABLE)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluid("prototype_pain:reaction_liquid",-10)
                .resultFluid(MedicalFluids.ALCOHOL.getId(), 400)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "alcohol"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.PainKillers.get())
                .requires(Items.SUGAR)
                .requires(ModItemTags.VIAL_ITEMS,true,true,true)
                .requiresFluidTag("prototype_pain:opioids",-10)
                .resultFluid(MedicalFluids.PAINKILLERS.getId(), 10)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "painkillers"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.Antibiotics.get())
                .requires(Items.SUGAR)
                .requires(Items.FERMENTED_SPIDER_EYE)
                .requires(ModItemTags.VIAL_ITEMS,true,true,true)
                .resultFluid(MedicalFluids.ANTIBIOTICS.getId(), 30)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "antibiotics"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.BloodClotting.get())
                .requires(Items.SPIDER_EYE)
                .requires(Items.GLISTERING_MELON_SLICE)
                .requires(ModItemTags.VIAL_ITEMS,true,true,true)
                .requiresFluid(MedicalFluids.REACTION_LIQUID.getId(), -5)
                .resultFluid(MedicalFluids.PROCOAGULANT.getId(), 15)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "blood_clotting"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.BloodThiner.get())
                .requires(Items.REDSTONE)
                .requires(Items.GLISTERING_MELON_SLICE)
                .requires(ModItemTags.VIAL_ITEMS,true,true,true)
                .requiresFluid(MedicalFluids.REACTION_LIQUID.getId(), -10)
                .resultFluid(MedicalFluids.STREPTOKINASE.getId(), 15)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "blood_thinner"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.AidGel.get())
                .requires(Items.MOSS_BLOCK)
                .requires(Items.HONEY_BOTTLE)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.MedicineVial.get())
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,Items.GLASS_BOTTLE)
                .requires(ModItemTags.VIAL_ITEMS)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "glass_from_other"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.MorphineVial.get())
                .requires(Items.FERMENTED_SPIDER_EYE)
                .requires(ModItemTags.VIAL_ITEMS,true,true,true)
                .requiresFluid(MedicalFluids.OPIUM.getId(), -10)
                .resultFluid(MedicalFluids.MORPHINE.getId(), 5)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "morphine_vial"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.HeroinVial.get())
                .requires(Items.REDSTONE)
                .requires(Items.IRON_INGOT)
                .requires(Items.IRON_INGOT)
                .requires(Items.FERMENTED_SPIDER_EYE)
                .requires(Items.FERMENTED_SPIDER_EYE)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluid(MedicalFluids.OPIUM.getId(), -100)
                .resultFluid(MedicalFluids.HEROIN.getId(), 150)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "heroin"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.FentanylVial.get())
                .requires(Items.NETHER_WART)
                .requires(Items.GLISTERING_MELON_SLICE)
                .requires(Items.FERMENTED_SPIDER_EYE)
                .requires(ModItemTags.VIAL_ITEMS,true,true,true)
                .requiresFluid(MedicalFluids.MORPHINE.getId(), -50)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluid(MedicalFluids.REACTION_LIQUID.getId(), -10)
                .resultFluid(MedicalFluids.FENTANYL.getId(), 2)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "fentanyl"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.Syringe.get())
                .pattern(" I ")
                .pattern(" I ")
                .pattern(" N ")
                .define('N',Items.IRON_NUGGET)
                .define('I',Items.IRON_INGOT)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.NaloxoneVial.get())
                .requires(Items.GLISTERING_MELON_SLICE)
                .requires(ModItemTags.VIAL_ITEMS,true,true,true)
                .requiresFluidTag("prototype_pain:opioids", -10)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluid(MedicalFluids.REACTION_LIQUID.getId(), -10)
                .resultFluid(MedicalFluids.NALOXONE.getId(), 20)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "naloxone"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.BrainGrow.get())
                .requires(Items.GLASS_BOTTLE)
                .requires(ItemTags.FISHES)
                .requires(ItemTags.FISHES)
                .requires(ItemTags.FISHES)
                .requires(ItemTags.FISHES)
                .requires(ItemTags.FISHES)
                .requires(Items.REDSTONE)
                .requires(Items.DIAMOND)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.Antiseptic.get())
                .requires(Items.GUNPOWDER)
                .requires(ModItemTags.VIAL_ITEMS,true,true,true)
                .requiresFluid(MedicalFluids.ALCOHOL.getId(), -100)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluid(MedicalFluids.REACTION_LIQUID.getId(), -10)
                .resultFluid(MedicalFluids.ANTISEPTIC.getId(), 50)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "antiseptic"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.AntiSerum.get())
                .requires(Items.GLISTERING_MELON_SLICE)
                .requires(ModItemTags.VIAL_ITEMS,true,true,true)
                .requiresFluid(MedicalFluids.ANTIBIOTICS.getId(), -10)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluid(MedicalFluids.REACTION_LIQUID.getId(), -10)
                .resultFluid(MedicalFluids.ANTISERUM.getId(), 50)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "antiserum"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.CEFTRIAXONE.get())
                .requires(Items.REDSTONE)
                .requires(ModItemTags.VIAL_ITEMS,true,true,true)
                .requiresFluid(MedicalFluids.ANTISERUM.getId(), -10)
                .requires(ModItemTags.VIAL_ITEMS,true,false,false)
                .requiresFluid(MedicalFluids.REACTION_LIQUID.getId(), -10)
                .resultFluid(MedicalFluids.CEFTRAIAXONE.getId(), 25)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "ceftriaxone"));

        ShapelessWithMedicalContainerRecipeBuilder.shapeless(ModItems.ReliefCream.get())
                .requires(Items.HONEY_BOTTLE)
                .requires(ModItemTags.VIAL_ITEMS,true,true,true)
                .requiresFluidTag("prototype_pain:opioids", -10)
                .resultFluid(MedicalFluids.RELIEF_CREAM.getId(), 25)
                .save(consumer, new ResourceLocation(PrototypePain.MOD_ID, "relief_cream"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.HeatPack.get())
                .requires(Items.GUNPOWDER)
                .requires(ItemTags.WOOL)
                .requires(Items.GUNPOWDER)
                .requires(Items.IRON_NUGGET)
                .unlockedBy("has_poppy", has(Items.AIR))
                .save(consumer);
    }
}
