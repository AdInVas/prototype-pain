package net.adinvas.prototype_pain.recipe.ingridients;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.adinvas.prototype_pain.ModMedicalRegistry;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.ModFluids;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class FluidIngredient {

    @Nullable
    private final Fluid fluid;

    @Nullable
    private final TagKey<Fluid> fluidTag;

    @Nullable
    private final TagKey<MedicalFluid> medicalTag;

    private final int amount;

    @Nullable
    private final CompoundTag nbt;

    /* ------------------------------------------------------------ */
    /* Constructors */
    /* ------------------------------------------------------------ */

    // Exact fluid
    public FluidIngredient(Fluid fluid, int amount, @Nullable CompoundTag nbt) {
        this.fluid = fluid;
        this.fluidTag = null;
        this.medicalTag = null;
        this.amount = amount;
        this.nbt = nbt;
    }

    // Forge fluid tag
    public FluidIngredient(TagKey<Fluid> tag, int amount, @Nullable CompoundTag nbt) {
        this.fluid = null;
        this.fluidTag = tag;
        this.medicalTag = null;
        this.amount = amount;
        this.nbt = nbt;
    }

    // Medical fluid tag
    public FluidIngredient(TagKey<MedicalFluid> medicalTag, int amount) {
        this.fluid = ModFluids.SRC_MEDICAL.get();
        this.fluidTag = null;
        this.medicalTag = medicalTag;
        this.amount = amount;
        this.nbt = null;
    }



    /* ------------------------------------------------------------ */
    /* Info */
    /* ------------------------------------------------------------ */

    public boolean isTagged() {
        return fluidTag != null || medicalTag != null;
    }

    public boolean isNormal(){
        return fluidTag != null;
    }

    public boolean isMedical() {
        return medicalTag != null;
    }

    @Nullable
    public TagKey<Fluid> getFluidTag() {
        return fluidTag;
    }

    @Nullable
    public Fluid getFluid() {
        return fluid;
    }

    public int getAmount() {
        return amount;
    }

    @Nullable
    public CompoundTag getNbt() {
        return nbt;
    }

    @Nullable
    public TagKey<MedicalFluid> getMedicalTag() {
        return medicalTag;
    }
    /* ------------------------------------------------------------ */
    /* Matching */
    /* ------------------------------------------------------------ */

    public boolean matches(FluidStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getAmount() < amount) return false;

        /* ---------- Medical fluid ---------- */
        if (medicalTag != null) {
            if (!stack.getFluid().isSame(ModFluids.SRC_MEDICAL.get())) return false;
            if (!stack.hasTag()) return false;

            String id = stack.getTag().getString("MedicalId");
            if (id.isEmpty()) return false;

            MedicalFluid medical = MedicalFluid.getFromId(id);
            return medical != null && medical.is(medicalTag);
        }

        /* ---------- Forge fluid tag ---------- */
        if (fluidTag != null) {
            if (!stack.getFluid().is(fluidTag)) return false;
        }

        /* ---------- Exact fluid ---------- */
        if (fluid != null) {
            if (!stack.getFluid().isSame(fluid)) return false;
        }

        /* ---------- NBT ---------- */
        if (nbt != null) {
            return stack.hasTag() && nbt.equals(stack.getTag());
        }

        return true;
    }

    /* ------------------------------------------------------------ */
    /* Output */
    /* ------------------------------------------------------------ */

    public FluidStack getAsFluidStack() {
        FluidStack stack = new FluidStack(
                fluid != null ? fluid : Fluids.EMPTY,
                amount
        );

        if (nbt != null) {
            stack.setTag(nbt.copy());
        }

        return stack;
    }

    /* ------------------------------------------------------------ */
    /* JSON */
    /* ------------------------------------------------------------ */

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("amount", amount);

        if (medicalTag != null) {
            json.addProperty("medical_tag", medicalTag.location().toString());
        } else if (fluidTag != null) {
            json.addProperty("tag", fluidTag.location().toString());
        } else if (fluid != null) {
            json.addProperty("fluid",
                    ForgeRegistries.FLUIDS.getKey(fluid).toString());
        }



        if (nbt != null) {
            json.add("nbt", JsonParser.parseString(nbt.toString()));
        }

        return json;
    }

    public static FluidIngredient fromJson(JsonObject obj) {
        int amount = GsonHelper.getAsInt(obj, "amount");

        if (obj.has("fluid")) {
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(obj, "fluid")));
            CompoundTag nbt = null;
            if (obj.has("nbt")) {
                try {
                    nbt = TagParser.parseTag(obj.get("nbt").toString());
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid NBT in fluid ingredient", e);
                }
            }
            return new FluidIngredient(fluid, amount, nbt);
        } else if (obj.has("tag")) {
            TagKey<Fluid> tag = TagKey.create(Registries.FLUID, new ResourceLocation(GsonHelper.getAsString(obj, "tag")));
            return new FluidIngredient(tag, amount, null);
        } else if (obj.has("medical_tag")) {
            TagKey<MedicalFluid> tag = TagKey.create(ModMedicalRegistry.MEDICAL_FLUIDS_KEY, new ResourceLocation(GsonHelper.getAsString(obj, "medical_tag")));
            return new FluidIngredient(tag, amount);
        }

        throw new IllegalArgumentException("Invalid fluid ingredient JSON: " + obj);
    }

}
