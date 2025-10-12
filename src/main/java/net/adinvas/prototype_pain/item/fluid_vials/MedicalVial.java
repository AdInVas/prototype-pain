package net.adinvas.prototype_pain.item.fluid_vials;

import net.adinvas.prototype_pain.ModSounds;
import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.adinvas.prototype_pain.item.IAllowInMedicbags;
import net.adinvas.prototype_pain.item.IMedicalFluidContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MedicalVial extends Item implements IMedicalFluidContainer, IAllowInMedicbags {
    public static final float CAPACITY = 100f;

    public MedicalVial() {
        super(new Item.Properties().stacksTo(1));
    }

    public MedicalVial(Item.Properties properties) {
        super(properties);
    }


    @Override
    public float getCapacity(ItemStack stack) {
        return CAPACITY;
    }

    @Override
    public float getFilledTotal(ItemStack stack) {
        float total = 0;
        CompoundTag nbt= stack.getOrCreateTag();
        ListTag list = nbt.getList("Fluids", Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            total +=tag.getFloat("Amount");
        }

        return total;
    }

    @Override
    public ListTag getFluids(ItemStack stack) {
        return stack.getOrCreateTag().getList("Fluids",Tag.TAG_COMPOUND);
    }

    @Override
    public float getAmountOfFluid(ItemStack stack, MedicalFluid fluid) {
        ListTag list = getFluids(stack);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag nbt = list.getCompound(i);
            if (nbt.getString("Fluid").equals(fluid.getId())){
                return nbt.getFloat("Amount");
            }
        }
        return 0;
    }

    public void setupDefaults(ItemStack stack){
    };

    @Override
    public Map<MedicalFluid, Float> drain(ItemStack stack, float ml) {
        Map<MedicalFluid, Float> finalmap = new LinkedHashMap<>();
        ListTag list = getFluids(stack);
        float totalFluid = getFilledTotal(stack);

        if (totalFluid <= 0f || list == null || list.size() == 0) return finalmap;

        // don't try to drain more than exists
        float toDrain = Math.min(ml, totalFluid);

        // Snapshot current fluids & amounts to avoid modifying while iterating
        List<MedicalFluid> fluids = new ArrayList<>();
        List<Float> amounts = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            String id = tag.getString("Fluid");
            MedicalFluid fluid = MedicalFluids.get(id);
            float amount = tag.getFloat("Amount");
            if (fluid != null && amount > 0f) {
                fluids.add(fluid);
                amounts.add(amount);
            }
        }

        // Drain proportionally using the snapshot.
        float sumDrained = 0f;
        for (int i = 0; i < fluids.size(); i++) {
            MedicalFluid fluid = fluids.get(i);
            float amount = amounts.get(i);

            // For last fluid, assign remaining to avoid rounding residue
            float drained = (i == fluids.size() - 1)
                    ? (toDrain - sumDrained)
                    : (toDrain * (amount / totalFluid));

            if (drained <= 0f) continue;

            // Record what we drained and remove from the real stack
            finalmap.put(fluid, drained);
            removeFluid(stack, drained, fluid);
            sumDrained += drained;
        }

        return finalmap;
    }

    public Map<MedicalFluid, Float> getFuildAndRatio(ItemStack stack) {
        Map<MedicalFluid,Float> finalmap = new HashMap<>();
        ListTag list = getFluids(stack);
        float totalFluid = getFilledTotal(stack);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            String id = tag.getString("Fluid");
            MedicalFluid fluid = MedicalFluids.get(id);
            finalmap.put(fluid,getFluidRatio(stack,fluid,totalFluid));
        }
        return finalmap;
    }




    @Override
    public void addFluid(ItemStack stack, float ml, MedicalFluid fluid) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = tag.getList("Fluids", 10); // 10 = CompoundTag type
        String fluidName = fluid.getId();

        boolean merged = false;

        for (int i = 0; i < list.size(); i++) {
            CompoundTag fluidTag = list.getCompound(i);
            if (fluidTag.getString("Fluid").equals(fluidName)) {
                float existing = fluidTag.getFloat("Amount");
                fluidTag.putFloat("Amount", existing + ml);
                merged = true;
                break;
            }
        }

        if (!merged) {
            CompoundTag fluidTag = new CompoundTag();
            fluidTag.putString("Fluid", fluidName);
            fluidTag.putFloat("Amount", ml);
            list.add(fluidTag);
        }

        tag.put("Fluids", list);
    }

    @Override
    public void removeFluid(ItemStack stack, float ml, MedicalFluid fluid) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = tag.getList("Fluids", 10); // 10 = CompoundTag type
        String fluidName = fluid.getId();

        for (int i = 0; i < list.size(); i++) {
            CompoundTag fluidTag = list.getCompound(i);
            if (fluidTag.getString("Fluid").equals(fluidName)) {
                float existing = fluidTag.getFloat("Amount");
                float newAmount = existing - ml;

                if (newAmount > 0) {
                    fluidTag.putFloat("Amount", newAmount);
                } else {
                    // Remove the fluid entirely if amount is 0 or negative
                    list.remove(i);
                }
                break; // Stop after removing from the first matching fluid
            }
        }

        // Update the tag
        tag.put("Fluids", list);
    }

    @Override
    public float getFluidRatio(ItemStack stack, MedicalFluid fluid,float totalfluids) {
        float amount = getAmountOfFluid(stack,fluid);
        return amount / totalfluids;
    }



    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.medicine_vial.discription").withStyle(ChatFormatting.GRAY));
        appendFluidListText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    public void appendOrigHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced){
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }



    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Only do something when crouching (sneaking)
        if (player.isCrouching()&&player.getXRot()<85) {
            if (!level.isClientSide) {
               player.startUsingItem(hand);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return super.use(level, player, hand);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000; // basically "infinite" duration until released
    }


    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE; // purely visual animation (can be NONE if you prefer)
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
        if (getFilledTotal(pStack)>0) {
            drain(pStack, 5);
            pLevel.playSound(null, pLivingEntity.blockPosition(), ModSounds.SYRINGE_LOOP.get(), SoundSource.PLAYERS, 0.6f, 1f);
        }
    }

    public void appendFluidListText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced){
        ListTag List = getFluids(pStack);
        for (int i=0;i<List.size();i++){
            CompoundTag tag = List.getCompound(i);
            String id = tag.getString("Fluid");
            float amount = tag.getFloat("Amount");
            if (amount>=0.1) {
                Component component = MedicalFluids.get(id).getDisplayName();
                pTooltipComponents.add(Component.empty().append(component).append(" (" + ((int) (amount * 10)) / 10f + "ml)").withStyle(Style.EMPTY.withColor(MedicalFluids.get(id).getColor())));
                if (Screen.hasShiftDown()) {
                    pTooltipComponents.add(MedicalFluids.get(id).getDescription().copy().withStyle(Style.EMPTY.withColor(darken(MedicalFluids.get(id).getColor(),0.85f))));
                    pTooltipComponents.add(Component.empty());
                }
            }
        }
    }

    public static int darken(int color, float factor) {
        int r = (int)(((color >> 16) & 0xFF) * factor);
        int g = (int)(((color >> 8) & 0xFF) * factor);
        int b = (int)((color & 0xFF) * factor);
        return (r << 16) | (g << 8) | b;
    }

    @Override
    public Component getName(ItemStack pStack) {
        Component finalcomp = super.getName(pStack);
        float fillprec = getFilledTotal(pStack)/getCapacity(pStack);
        finalcomp = Component.empty().append(finalcomp)
                .append(Component.literal(" (").withStyle(ChatFormatting.GRAY))
                .append(Component.literal((int)(fillprec*100)+"%").withStyle(Style.EMPTY.withColor(Util.getRedToGreenColor(fillprec))))
                .append(Component.literal(")").withStyle(ChatFormatting.GRAY));
        return finalcomp;
    }
}
