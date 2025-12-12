package net.adinvas.prototype_pain.fluid_system;

import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.item.multi_tank.MultiTankFluidItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiTankHelper {
    public static float getCapacity(ItemStack stack) {
        MultiFluidTankHandler handler = getHandler(stack);
        if (handler == null) return 0;
        return handler.getTankCapacity(0);
    }

    public static float getFilledTotal(ItemStack stack) {
        MultiFluidTankHandler handler = getHandler(stack);
        if (handler == null) return 0;

        int total = 0;
        for (FluidStack fs : handler.getTank().getFluids()) {
            total += fs.getAmount();
        }
        return total;
    }

    public static ListTag getFluids(ItemStack stack) {
        MultiFluidTankHandler handler = getHandler(stack);
        ListTag list = new ListTag();
        if (handler == null) return list;

        for (FluidStack fs : handler.getTank().getFluids()) {
            CompoundTag tag = new CompoundTag();
            fs.writeToNBT(tag);
            list.add(tag);
        }
        return list;
    }

    public static float getAmountOfFluid(ItemStack stack, FluidStack fluid) {
        MultiFluidTankHandler handler = getHandler(stack);
        if (handler == null || fluid.isEmpty()) return 0;

        int amount = 0;
        for (FluidStack fs : handler.getTank().getFluids()) {
            if (fs.isFluidEqual(fluid)) amount += fs.getAmount();
        }
        return amount;
    }

    public static void addFluid(ItemStack stack, float ml, FluidStack fluid) {
        MultiFluidTankHandler handler = getHandler(stack);
        if (handler == null || fluid.isEmpty()) return;

        handler.getTank().fill(new FluidStack(fluid.getFluid(), (int) ml, fluid.getTag()), IFluidHandler.FluidAction.EXECUTE);
        handler.saveToNBT();
    }

    public static void removeFluid(ItemStack stack, float ml, FluidStack fluid) {
        MultiFluidTankHandler handler = getHandler(stack);
        if (handler == null || fluid.isEmpty()) return;

        handler.getTank().drain(new FluidStack(fluid.getFluid(), (int) ml, fluid.getTag()), IFluidHandler.FluidAction.EXECUTE);
        handler.saveToNBT();
    }

    public static float getFluidRatio(ItemStack stack, FluidStack fluid, float totalFluids) {
        if (totalFluids <= 0) return 0;
        return getAmountOfFluid(stack, fluid) / totalFluids;
    }


    public static void addMedicalFluid(ItemStack stack, float ml, String medicalId, FluidStack baseFluid) {
        if (baseFluid.isEmpty() || medicalId == null || medicalId.isEmpty()) return;

        FluidStack fs = new FluidStack(baseFluid.getFluid(), (int) ml);
        CompoundTag tag = fs.getOrCreateTag();
        tag.putString("MedicalId", medicalId);
        fs.setTag(tag);

        addFluid(stack, ml, fs);
    }

    private static MultiFluidTankHandler getHandler(ItemStack stack) {
        if (stack.getItem() instanceof MultiTankFluidItem) {
            // Access capability directly
            return ((MultiTankFluidItem) stack.getItem()).getHandler(stack);
        }
        return null;
    }

    public static Map<FluidStack, Float> getFluidsAsMap(ItemStack stack) {
        MultiFluidTankHandler handler = getHandler(stack);
        Map<FluidStack,Float> map = new HashMap<>();
        if (handler == null) return map;
        for (FluidStack fs : handler.getTank().getFluids()){
            FluidStack fss = fs.copy();
            float amount = fss.getAmount();
            fss.setAmount(1);
            map.put(fss,amount);
        }
        return map;
    }

    public static List<FluidStack> drain(ItemStack stack, float ml) {
        MultiFluidTankHandler handler = getHandler(stack);
        List<FluidStack> drained = new ArrayList<>();
        if (handler == null) return drained;

        List<FluidStack> fluids = new ArrayList<>(handler.getTank().getFluids());
        int total = handler.getTank().getTotalFluid();
        int drainAmount = Math.min(total, (int) ml);

        if (total <= 0 || drainAmount <= 0) return drained;

        // Edge case: drain all fluids
        if (drainAmount == total) {
            for (FluidStack fs : fluids) {
                int amt = fs.getAmount();
                FluidStack out = new FluidStack(fs.getFluid(), amt);
                if (fs.hasTag()) out.setTag(fs.getTag().copy());

                drained.add(out);
                handler.getTank().drain(fs.copy(), IFluidHandler.FluidAction.EXECUTE);
            }
            handler.saveToNBT();
            return drained;
        }

        int remaining = drainAmount;

        for (int i = 0; i < fluids.size(); i++) {
            FluidStack fs = fluids.get(i);
            int amountToDrain;

            if (i == fluids.size() - 1) {
                // Last fluid takes all remaining to guarantee total
                amountToDrain = remaining;
            } else {
                amountToDrain = Math.max(1, (int) Math.ceil(drainAmount * ((float) fs.getAmount() / total)));
                amountToDrain = Math.min(amountToDrain, fs.getAmount());
            }

            if (amountToDrain <= 0) continue;

            // drain from tank
            handler.getTank().drain(new FluidStack(fs.getFluid(), amountToDrain, fs.getTag()), IFluidHandler.FluidAction.EXECUTE);

            // copy drained fluid
            FluidStack out = new FluidStack(fs.getFluid(), amountToDrain);
            if (fs.hasTag()) out.setTag(fs.getTag().copy());

            drained.add(out);

            remaining -= amountToDrain;
            if (remaining <= 0) break;
        }

        handler.saveToNBT();
        return drained;
    }

    public static void setFluidsDirect(ItemStack stack, Map<FluidStack, Float> fluids) {
        MultiFluidTankHandler handler = getHandler(stack);
        if (handler == null) return;

        MultiFluidTank tank = handler.getTank();
        tank.clearFluids();  // remove everything currently in the tank

        for (Map.Entry<FluidStack, Float> entry : fluids.entrySet()) {
            FluidStack key = entry.getKey();
            float amount = entry.getValue();

            if (key.isEmpty() || amount <= 0) continue;

            // Create full FluidStack with correct integer amount
            FluidStack fs = key.copy();
            fs.setAmount((int) amount);

            tank.fill(fs, IFluidHandler.FluidAction.EXECUTE);
        }

        handler.saveToNBT();
    }

    public static Map<Integer,Float> getColorRatios(ItemStack stack, Level level){
        MultiFluidTankHandler handler = getHandler(stack);
        Map<Integer,Float> colors = new HashMap<>();
        if (handler == null) return colors;
        for (FluidStack fs : handler.getTank().getFluids()){
            int color = Util.getColorFromFluid(fs,level);
            if (color==0xFFFFFF)continue;
            colors.put(color,getFluidRatio(stack,fs,getFilledTotal(stack)));
        }
        return colors;
    }

    public static Map<FluidStack, Float> getFluidRatios(ItemStack stack){
        MultiFluidTankHandler handler = getHandler(stack);
        Map<FluidStack, Float> flMap = getFluidsAsMap(stack);
        Map<FluidStack, Float> ratioMap = new HashMap<>();
        if (handler == null) return ratioMap;
        for (FluidStack fs: flMap.keySet()){
            float ratio = getFluidRatio(stack,fs,getFilledTotal(stack));
            ratioMap.put(fs,ratio);
        }
        return ratioMap;
    }
}
