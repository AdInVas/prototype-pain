package net.adinvas.prototype_pain.fluid_system.n;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.fluid_system.MedicalEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class MedicalFluidType extends FluidType {
    private final ResourceLocation stillTexture;
    private final ResourceLocation flowTexture;
    private final MedicalEffect effect;
    private final int color;
    public MedicalFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowTexture, MedicalEffect effect, int color) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowTexture = flowTexture;
        this.effect = effect;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public MedicalEffect getEffect() {
        return effect;
    }

    public ResourceLocation getFlowTexture() {
        return flowTexture;
    }

    public ResourceLocation getStillTexture() {
        return stillTexture;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {

            @Override
            public ResourceLocation getStillTexture(FluidStack stack) {
                return stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture(FluidStack stack) {
                return flowTexture;
            }

            @Override
            public int getTintColor() {
                return color;
            }

            @Override
            public ResourceLocation getStillTexture() {
               return stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowTexture;
            }
        });
    }
}
