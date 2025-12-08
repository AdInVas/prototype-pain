package net.adinvas.prototype_pain.fluid_system.n;

import net.adinvas.prototype_pain.ModSounds;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.adinvas.prototype_pain.item.IMedicalFluidContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class FluidVialItem extends Item implements IMedicalFluidContainer {

    public static final float CAPACITY = 100f;

    public FluidVialItem() {
        super(new Item.Properties().stacksTo(1));
    }

    public FluidVialItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public float getCapacity(ItemStack stack) {
        return CAPACITY;
    }

    @Override
    public float getFilledTotal(ItemStack stack) {
        float total = 0;
        ListTag list = getFluids(stack);

        for (int i = 0; i < list.size(); i++) {
            total += list.getCompound(i).getFloat("Amount");
        }
        return total;
    }

    @Override
    public ListTag getFluids(ItemStack stack) {
        return stack.getOrCreateTag().getList("Fluids", Tag.TAG_COMPOUND);
    }

    @Override
    public float getAmountOfFluid(ItemStack stack, MedicalFluid fluid) {
        return getAmountOfFluid(stack, (Object) fluid);
    }

    // -------------------- FLUID ACCESS --------------------

    public float getAmountOfFluid(ItemStack stack, Object fluid) {

        ListTag list = getFluids(stack);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag fTag = list.getCompound(i);

            // Medical fluids
            if (fluid instanceof MedicalFluid m) {
                if (fTag.contains("MedicalId") && fTag.getString("MedicalId").equals(m.getId())) {
                    return fTag.getFloat("Amount");
                }
            }

            // Forge fluid
            if (fluid instanceof FluidStack fs) {
                ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fs.getFluid());
                if (id != null && fTag.contains("ForgeFluid") &&
                        fTag.getString("ForgeFluid").equals(id.toString())) {
                    return fTag.getFloat("Amount");
                }
            }
        }
        return 0;
    }

    @Override
    public void addFluid(ItemStack stack, float ml, MedicalFluid fluid) {
        addFluid(stack, ml, (Object) fluid);
    }

    public void addFluid(ItemStack stack, float ml, Object fluid) {

        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = tag.getList("Fluids", 10);
        boolean merged = false;

        for (int i = 0; i < list.size(); i++) {
            CompoundTag fTag = list.getCompound(i);

            // Medical fluid merge
            if (fluid instanceof MedicalFluid m) {
                if (fTag.contains("MedicalId") && fTag.getString("MedicalId").equals(m.getId())) {
                    fTag.putFloat("Amount", fTag.getFloat("Amount") + ml);
                    merged = true;
                    break;
                }
            }

            // Forge fluid merge
            if (fluid instanceof FluidStack fs) {
                ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fs.getFluid());
                if (id != null && fTag.contains("ForgeFluid") &&
                        fTag.getString("ForgeFluid").equals(id.toString())) {

                    fTag.putFloat("Amount", fTag.getFloat("Amount") + ml);
                    merged = true;
                    break;
                }
            }
        }

        if (!merged) {
            CompoundTag fTag = new CompoundTag();

            if (fluid instanceof MedicalFluid m) {
                fTag.putString("MedicalId", m.getId());
            } else if (fluid instanceof FluidStack fs) {
                ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fs.getFluid());
                if (id != null) fTag.putString("ForgeFluid", id.toString());
            }

            fTag.putFloat("Amount", ml);
            list.add(fTag);
        }

        tag.put("Fluids", list);
    }

    @Override
    public void removeFluid(ItemStack stack, float ml, MedicalFluid fluid) {
        removeFluid(stack, ml, (Object) fluid);
    }

    public void removeFluid(ItemStack stack, float ml, Object fluid) {

        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = tag.getList("Fluids", 10);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag fTag = list.getCompound(i);

            boolean matches = false;

            // Medical
            if (fluid instanceof MedicalFluid m &&
                    fTag.contains("MedicalId") &&
                    fTag.getString("MedicalId").equals(m.getId())) {

                matches = true;
            }

            // Forge fluid
            if (fluid instanceof FluidStack fs &&
                    fTag.contains("ForgeFluid")) {

                ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fs.getFluid());
                if (id != null && fTag.getString("ForgeFluid").equals(id.toString())) {
                    matches = true;
                }
            }

            if (matches) {
                float newAmount = fTag.getFloat("Amount") - ml;
                if (newAmount > 0) fTag.putFloat("Amount", newAmount);
                else list.remove(i);
                break;
            }
        }

        tag.put("Fluids", list);
    }

    @Override
    public float getFluidRatio(ItemStack stack, MedicalFluid fluid, float totalfluids) {
        return getAmountOfFluid(stack, fluid) / totalfluids;
    }

    // -------------------- DRAIN --------------------

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        if (player.isCrouching()) {
            addFluid(stack, 1, MedicalFluids.PAINKILLERS);
            addFluid(stack, 1, new FluidStack(Fluids.LAVA, 1));
        }
    }

    @Override
    public Map<MedicalFluid, Float> drain(ItemStack stack, float ml) {

        Map<MedicalFluid, Float> drained = new LinkedHashMap<>();
        ListTag list = getFluids(stack);
        float total = getFilledTotal(stack);

        if (total <= 0 || list.isEmpty()) return drained;

        float toDrain = Math.min(ml, total);

        List<Object> snapshot = new ArrayList<>();
        List<Float> amounts = new ArrayList<>();

        // Drain only medical fluids
        for (int i = 0; i < list.size(); i++) {
            CompoundTag fTag = list.getCompound(i);

            if (fTag.contains("MedicalId")) {
                MedicalFluid m = MedicalFluids.get(fTag.getString("MedicalId"));
                if (m != null) {
                    snapshot.add(m);
                    amounts.add(fTag.getFloat("Amount"));
                }
            }
        }

        float sumDrained = 0f;

        for (int i = 0; i < snapshot.size(); i++) {
            Object fluid = snapshot.get(i);
            float amount = amounts.get(i);

            float drainedAmount =
                    (i == snapshot.size() - 1)
                            ? toDrain - sumDrained
                            : toDrain * (amount / total);

            removeFluid(stack, drainedAmount, fluid);

            if (fluid instanceof MedicalFluid m)
                drained.put(m, drainedAmount);

            sumDrained += drainedAmount;
        }

        return drained;
    }

    // -------------------- TOOLTIP --------------------

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {

        tooltip.add(Component.translatable("item.prototype_pain.medicine_vial.discription")
                .withStyle(ChatFormatting.GRAY));

        appendFluidListText(stack, level, tooltip, flag);
    }

    public void appendFluidListText(ItemStack stack, @Nullable Level level,
                                    List<Component> tooltip, TooltipFlag flag) {

        ListTag list = getFluids(stack);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag fTag = list.getCompound(i);
            float amount = fTag.getFloat("Amount");

            if (amount < 0.1f) continue;

            // Medical
            if (fTag.contains("MedicalId")) {
                MedicalFluid m = MedicalFluids.get(fTag.getString("MedicalId"));
                tooltip.add(
                        m.getDisplayName().copy()
                                .append(" (" + (Math.round(amount * 10) / 10f) + "mb)")
                                .withStyle(Style.EMPTY.withColor(m.getColor()))
                );

                if (Screen.hasShiftDown())
                    tooltip.add(m.getDescription().copy()
                            .withStyle(Style.EMPTY.withColor(darken(m.getColor(), 0.85f))));

                continue;
            }

            // Forge fluid
            if (fTag.contains("ForgeFluid")) {
                ResourceLocation id = new ResourceLocation(fTag.getString("ForgeFluid"));
                FluidStack fs = new FluidStack(ForgeRegistries.FLUIDS.getValue(id), (int) amount);
                int amountf = fs.getAmount();
                tooltip.add(fs.getDisplayName().copy().withStyle(ChatFormatting.RED).append("("+amountf+"mb)"));
            }
        }
    }

    public static int darken(int color, float factor) {
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (r << 16) | (g << 8) | b;
    }

    // -------------------- USE --------------------

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (player.isCrouching() && player.getXRot() < 85) {
            if (!level.isClientSide)
                player.startUsingItem(hand);

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return super.use(level, player, hand);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {

        if (getFilledTotal(stack) > 0) {
            drain(stack, 5);
            level.playSound(
                    null, entity.blockPosition(),
                    ModSounds.SYRINGE_LOOP.get(),
                    SoundSource.PLAYERS,
                    0.6f, 1f
            );
        }
    }
}