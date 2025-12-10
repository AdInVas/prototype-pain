package net.adinvas.prototype_pain.datagen;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.item.ModItems;
import net.adinvas.prototype_pain.tags.ModItemTags;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

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
    }
}
