package net.adinvas.prototype_pain.fluid_system.n;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.fluid_system.MedicalEffect;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class MedicalFluidType extends FluidType {
    public MedicalFluidType(Properties props) {
        super(props);
    }

    @Override
    public Component getDescription(FluidStack stack) {
        if (stack.hasTag() && stack.getTag().contains("MedicalId")) {
            String id = stack.getTag().getString("MedicalId");
            MedicalFluid med = MedicalFluids.get(id);
            if (med != null) return med.getDisplayName(); // or med.getDescription() if you want tooltip
        }
        return super.getDescription(stack); // fallback
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {

            @Override
            public int getTintColor(FluidStack stack) {
                CompoundTag tag = stack.getTag();
                if (tag != null && tag.contains("MedicalId", Tag.TAG_STRING)) {
                    String medId = tag.getString("MedicalId");
                    MedicalFluid m = MedicalFluids.get(medId);
                    if (m != null) return m.getColor() | 0xFF000000; // ensure alpha set
                }
                // fallback color (transparent)
                return 0xFFFFFFFF;
            }

            @Override
            public ResourceLocation getStillTexture() {
               return new ResourceLocation("block/water_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return new ResourceLocation("block/water_flow");
            }

        });
    }
}
