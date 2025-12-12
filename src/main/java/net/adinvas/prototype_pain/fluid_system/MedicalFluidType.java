package net.adinvas.prototype_pain.fluid_system;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class MedicalFluidType extends FluidType {
    public MedicalFluidType(Properties props) {
        super(props);
    }

    @Override
    public String getDescriptionId(FluidStack stack) {
        MedicalFluid med = MedicalFluid.getFromId(stack.getTag().getString("MedicalId"));
        if (med!=null)
            return med.getNameId();

        return stack.getTag().getString("MedicalId");
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {

            @Override
            public int getTintColor(FluidStack stack) {
                CompoundTag tag = stack.getTag();
                if (tag != null && tag.contains("MedicalId", Tag.TAG_STRING)) {
                    String medId = tag.getString("MedicalId");
                    MedicalFluid m = MedicalFluid.getFromId(medId);
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
