package net.adinvas.prototype_pain.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.adinvas.prototype_pain.ModMedicalRegistry;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.ModFluids;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;
import net.adinvas.prototype_pain.item.multi_tank.MultiTankFluidItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public class AddRandomFillToChestsModifier extends LootModifier {
    public static final Supplier<Codec<AddRandomFillToChestsModifier>> CODEC = Suppliers.memoize(()->
            RecordCodecBuilder.create(inst->codecStart(inst).and(ForgeRegistries.ITEMS.getCodec()
                    .fieldOf("item").forGetter(m -> m.item)).apply(inst, AddRandomFillToChestsModifier::new)));
    private final Item item;


    public AddRandomFillToChestsModifier(LootItemCondition[] conditionsIn, Item item) {
        super(conditionsIn);
        this.item = item;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generated, LootContext lootContext) {
        for (LootItemCondition condition : this.conditions) {
            if (!condition.test(lootContext)) {
                return generated;
            }
        }

        // Get which loot table triggered this modifier
        ResourceLocation tableId = lootContext.getQueriedLootTableId();
        if (tableId == null) return generated;

        // ✅ Only run for chest loot tables (including modded ones)
        if (!tableId.getPath().startsWith("chests/")) {
            return generated;
        }

        // --- At this point, we know it's a chest loot table ---

        ItemStack stack = new ItemStack(this.item);

        // (Optional) Random NBT data example

        if (stack.getItem() instanceof MultiTankFluidItem vial) {
            Random random = new Random();
            float capacity = MultiTankHelper.getCapacity(stack);
            float origCap = capacity;
            ServerLevel level = lootContext.getLevel();
            do {
                float addamount = random.nextFloat()*50;
                addamount = Math.min(addamount,capacity);


                Registry<MedicalFluid> fluids = level.registryAccess()
                            .registryOrThrow(ModMedicalRegistry.MEDICAL_FLUIDS_KEY);

                List<MedicalFluid> valid = fluids.stream()
                            .toList();

                MedicalFluid fluid = valid.get(random.nextInt(valid.size()));
                if (!fluid.showInJEI())continue;

                MultiTankHelper.addMedicalFluid(stack,addamount,fluid.getRegistryId().toString(),new FluidStack(ModFluids.SRC_MEDICAL.get().getSource(),1));
                capacity -= addamount;
                if (random.nextBoolean()&&capacity/origCap<0.5){
                    break;
                }
            }while (capacity>0);
        }



        generated.add(stack);
        return generated;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
