package net.adinvas.prototype_pain.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.adinvas.prototype_pain.fluid_system.n.ModFluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JeiPlugin
public class JEIFluidPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(PrototypePain.MOD_ID, "jei_fluid");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
        MedicalFluidSubtypeInterpreter interpreter = new MedicalFluidSubtypeInterpreter();

        registration.registerSubtypeInterpreter(
                ForgeTypes.FLUID_STACK,
                ModFluids.SRC_MEDICAL.get(),  // source
                interpreter
        );
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        Collection<FluidStack> medicalFluids = new ArrayList<>();
        for (MedicalFluid medicalFluid:MedicalFluids.REGISTRY.values()) {
            FluidStack fs = new FluidStack(ModFluids.SRC_MEDICAL.get(), 1000);
            CompoundTag tag = new CompoundTag();
            tag.putString("MedicalId", medicalFluid.getId());
            fs.setTag(tag);
            medicalFluids.add(fs);
        }
        registration.addExtraIngredients(ForgeTypes.FLUID_STACK, medicalFluids);
    }

    public static class MedicalFluidSubtypeInterpreter implements IIngredientSubtypeInterpreter<FluidStack> {

        @Override
        public String apply(FluidStack stack, UidContext context) {

            if (!stack.hasTag()) return "";

            if (stack.getTag().contains("MedicalId"))
                return stack.getTag().getString("MedicalId");

            return "";
        }
    }
}
