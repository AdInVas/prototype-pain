package net.adinvas.prototype_pain.item.multi_tank;

import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.MultiFluidTankHandler;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MultiTankFluidItem extends Item{
    private final int capacity= 1000;
    public MultiTankFluidItem() {
        super(new Properties().stacksTo(1));
    }
    public MultiTankFluidItem(Properties properties){
        super(properties);
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {

            private final LazyOptional<IFluidHandlerItem> fluidCap =
                    LazyOptional.of(() -> new MultiFluidTankHandler(stack, getCapacity()));

            @Override
            @SuppressWarnings("unchecked")
            public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, net.minecraft.core.Direction side) {
                if (cap == ForgeCapabilities.FLUID_HANDLER_ITEM) {
                    return (LazyOptional<T>) fluidCap;
                }
                return LazyOptional.empty();
            }
        };
    }

    public MultiFluidTankHandler getHandler(ItemStack stack){
        if (stack.isEmpty() || stack.getItem() != this) return null;
        LazyOptional<IFluidHandlerItem> cap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (!cap.isPresent()) return null;

        IFluidHandlerItem handler = cap.orElse(null);
        if (handler instanceof MultiFluidTankHandler) {
            return (MultiFluidTankHandler) handler;
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
       appendDescription(stack,level,tooltip,flag);
        appendFluidText(stack,level,tooltip,flag);
    }

    public void appendDescription(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        String key = getDescriptionId(pStack) + ".description";
        if (I18n.exists(key)) {
            pTooltipComponents.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    public void setupDefault(ItemStack pStack){

    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        setupDefault(pStack);
    }

    public void appendFluidText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag){
        CompoundTag tag = stack.getTagElement("MultiFluidTank");
        boolean hasSpecial = false;
        if (tag == null) {
            return;
        }

        ListTag list = tag.getList("Fluids", Tag.TAG_COMPOUND);
        if (list.isEmpty()) {
            return;
        }

        tooltip.add(Component.literal("Contents:"));
        for (Tag t : list) {
            FluidStack fs = FluidStack.loadFluidStackFromNBT((CompoundTag) t);
            int color = 0xFF0088;
            if (fs.hasTag()) {
                MedicalFluid Mfluid = MedicalFluid.getFromId(fs.getTag().getString("MedicalId"));
                if (Mfluid != null) {
                    if (!Mfluid.showInTooltip(stack))continue;
                    hasSpecial = true;
                    color = Mfluid.getColor();
                    tooltip.add(Component.literal(fs.getDisplayName().getString()).withStyle(Style.EMPTY.withColor(color)).append("(" + fs.getAmount() + "mb)"));
                    if (Screen.hasShiftDown()) {
                        tooltip.add(Mfluid.getDescription().copy().withStyle(Style.EMPTY.withColor(color)));
                    }
                    continue;
                }
            }

            color = Util.getColorFromFluid(fs,level);
            tooltip.add(Component.literal(fs.getDisplayName().getString()).withStyle(Style.EMPTY.withColor(color)).append("("+fs.getAmount()+"mb)"));
        }

        if (hasSpecial&&!Screen.hasShiftDown()){
            tooltip.add(Component.translatable("prototype_pain.multi_tank.hint").withStyle(ChatFormatting.GRAY));
        }
    }


}