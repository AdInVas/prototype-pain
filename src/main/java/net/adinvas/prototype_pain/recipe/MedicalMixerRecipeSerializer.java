package net.adinvas.prototype_pain.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.adinvas.prototype_pain.ModMedicalRegistry;
import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.recipe.ingridients.FluidIngredient;
import net.adinvas.prototype_pain.recipe.ingridients.ItemIngredient;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MedicalMixerRecipeSerializer implements RecipeSerializer<MedicalMixerRecipe> {
    @Override
    public MedicalMixerRecipe fromJson(ResourceLocation resourceLocation, JsonObject json) {
        int time = GsonHelper.getAsInt(json, "processingTime", 100);

        List<ItemIngredient> itemInputs = parseItemIngredients(json);
        List<FluidIngredient> fluidInputs = parseFluidIngredients(json);
        List<ItemStack> itemOutputs = parseItemOutputs(json);
        List<FluidStack> fluidOutputs = parseFluidOutputs(json);

        return new MedicalMixerRecipe(resourceLocation,itemInputs,fluidInputs,itemOutputs,fluidOutputs,time);
    }

    private List<FluidStack> parseFluidOutputs(JsonObject json) {
        List<FluidStack> fluidOutputs = new ArrayList<>();
        if (json.has("fluid_outputs")) {
            for (var el : GsonHelper.getAsJsonArray(json, "fluid_outputs")) {
                JsonObject obj = el.getAsJsonObject();
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(obj, "fluid")));
                int amount = GsonHelper.getAsInt(obj, "amount");
                FluidStack stack = new FluidStack(fluid, amount);

                if (obj.has("nbt")) {
                    try {
                        String snbt = GsonHelper.getAsString(obj, "nbt");
                        stack.setTag(TagParser.parseTag(snbt));
                    } catch (CommandSyntaxException e) {
                        throw new RuntimeException("Invalid SNBT in fluid_outputs", e);
                    }
                }

                fluidOutputs.add(stack);
            }
        }
        return fluidOutputs;
    }

    private List<ItemStack> parseItemOutputs(JsonObject json) {
        List<ItemStack> itemOutputs = new ArrayList<>();
        if (json.has("item_outputs")) {
            for (var el : GsonHelper.getAsJsonArray(json, "item_outputs")) {
                JsonObject obj = el.getAsJsonObject();
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(obj, "item")));
                int count = GsonHelper.getAsInt(obj, "count", 1);
                itemOutputs.add(new ItemStack(item, count));
            }
        }
        return itemOutputs;
    }


    private List<FluidIngredient> parseFluidIngredients(JsonObject json) {
        List<FluidIngredient> list = new ArrayList<>();
        if (json.has("fluid_inputs")) {
            for (var el : GsonHelper.getAsJsonArray(json, "fluid_inputs")) {
                FluidIngredient ingredient = FluidIngredient.fromJson(el.getAsJsonObject());
                list.add(ingredient);

            }
        }
        return list;
    }

    private List<ItemIngredient> parseItemIngredients(JsonObject json) {
        List<ItemIngredient> list = new ArrayList<>();
        if (json.has("item_inputs")) {
            for (var el : GsonHelper.getAsJsonArray(json, "item_inputs")) {
                ItemIngredient ingredient = ItemIngredient.fromJson(el.getAsJsonObject());
                list.add(ingredient);
            
            }
        }
        return list;
    }

    @Override
    public @Nullable MedicalMixerRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buf) {
        int time = buf.readInt();

        int itemInputCount = buf.readInt();
        List<ItemIngredient> itemInputs = new ArrayList<>(itemInputCount);
        for (int i = 0; i < itemInputCount; i++) {
         switch (buf.readInt()){
             case 0->{
                 ItemStack stack = buf.readItem();
                 int count = buf.readInt();
                 itemInputs.add(new ItemIngredient(stack,count));
             }
             case 1->{
                 TagKey<Item> tag = TagKey.create(Registries.ITEM,buf.readResourceLocation());
                 int count = buf.readInt();
                 itemInputs.add(new ItemIngredient(tag,count));
             }
         }
        }
        int fluidInputsCount = buf.readInt();
        List<FluidIngredient> fluidInputs =  new ArrayList<>(fluidInputsCount);
        for (int i =0; i<fluidInputsCount;i++){
            switch (buf.readInt()){
                case 0->{
                    FluidStack stack = buf.readFluidStack();
                    fluidInputs.add(new FluidIngredient(stack.getFluid(),stack.getAmount(),stack.getTag()));
                }
                case 1->{
                    TagKey<Fluid> tag = TagKey.create(Registries.FLUID,buf.readResourceLocation());
                    int amount = buf.readInt();
                    fluidInputs.add(new FluidIngredient(tag,amount,null));
                }
                case 2->{
                    TagKey<MedicalFluid> tag = TagKey.create(ModMedicalRegistry.MEDICAL_FLUIDS_KEY,buf.readResourceLocation());
                    int amount = buf.readInt();
                    fluidInputs.add(new FluidIngredient(tag,amount));
                }
            }
        }

        int itemOutputsCount = buf.readInt();
        List<ItemStack> itemOutputs =  new ArrayList<>(itemOutputsCount);
        for (int i=0;i<itemOutputsCount;i++){
            ItemStack stack = buf.readItem();
            itemOutputs.add(stack);
        }

        int fluidOutputsCount = buf.readInt();
        List<FluidStack> fluidOutputs =  new ArrayList<>(fluidOutputsCount);
        for (int i=0;i<fluidOutputsCount;i++){
           fluidOutputs.add(buf.readFluidStack());
        }
        return new MedicalMixerRecipe(resourceLocation,itemInputs,fluidInputs,itemOutputs,fluidOutputs,time);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, MedicalMixerRecipe recipe) {
        // --- Processing Time ---
        buf.writeInt(recipe.getProcessingTime());

        // --- Item Inputs ---
        List<ItemIngredient> itemInputs = recipe.getItemInputs();
        buf.writeInt(itemInputs.size());

        for (ItemIngredient ing : itemInputs) {
            if (ing.isTagged()) {
                // Tagged item
                buf.writeInt(1); // type ID for tag
                buf.writeResourceLocation(ing.getTag().location());
            } else {
                // Plain item
                buf.writeInt(0); // type ID for plain item
                buf.writeItem(ing.getItem());
            }
            buf.writeInt(ing.getCount());
        }

        // --- Fluid Inputs ---
        List<FluidIngredient> fluidInputs = recipe.getFluidInputs();
        buf.writeInt(fluidInputs.size());

        for (FluidIngredient ing : fluidInputs) {
            if (ing.isTagged()) {
                if (ing.getFluidTag().registry() == Registries.FLUID) {
                    buf.writeInt(1); // vanilla fluid tag
                    buf.writeResourceLocation(ing.getFluidTag().location());
                } else {
                    // assume MedicalFluid tag
                    buf.writeInt(2);
                    buf.writeResourceLocation(ing.getMedicalTag().location());
                }
                buf.writeInt(ing.getAmount());
            } else {
                // plain fluid
                buf.writeInt(0);
                buf.writeFluidStack(ing.getAsFluidStack());
            }
        }

        // --- Item Outputs ---
        List<ItemStack> itemOutputs = recipe.getItemOutputs();
        buf.writeInt(itemOutputs.size());
        for (ItemStack stack : itemOutputs) {
            buf.writeItem(stack);
        }

        // --- Fluid Outputs ---
        List<FluidStack> fluidOutputs = recipe.getFluidOutputs();
        buf.writeInt(fluidOutputs.size());
        for (FluidStack stack : fluidOutputs) {
            buf.writeFluidStack(stack);
        }
    }



}
