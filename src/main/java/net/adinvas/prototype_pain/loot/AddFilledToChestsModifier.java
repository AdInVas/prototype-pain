package net.adinvas.prototype_pain.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.adinvas.prototype_pain.item.INbtDrivenDurability;
import net.adinvas.prototype_pain.item.fluid_vials.MedicalVial;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AddFilledToChestsModifier extends LootModifier {
    public static final Supplier<Codec<AddFilledToChestsModifier>> CODEC = Suppliers.memoize(()->
            RecordCodecBuilder.create(inst->codecStart(inst).and(ForgeRegistries.ITEMS.getCodec()
                    .fieldOf("item").forGetter(m -> m.item)).apply(inst, AddFilledToChestsModifier::new)));
    private final Item item;


    public AddFilledToChestsModifier(LootItemCondition[] conditionsIn, Item item) {
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
        if (stack.getItem() instanceof MedicalVial vial) {
            vial.setupDefaults(stack);
        }
        if (stack.getItem() instanceof INbtDrivenDurability nbtDrivenDurability){
            nbtDrivenDurability.setupDefaults(stack);
        }

        generated.add(stack);
        return generated;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
