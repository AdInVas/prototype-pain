package net.adinvas.prototype_pain.blocks.medical_mixer;

import cpw.mods.util.Lazy;
import net.adinvas.prototype_pain.ModMedicalFluids;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.blocks.ModBlockEntities;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.ModFluids;
import net.adinvas.prototype_pain.item.INbtDrivenDurability;
import net.adinvas.prototype_pain.network.FluidSyncS2CPacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.adinvas.prototype_pain.recipe.MedicalMixerRecipe;
import net.adinvas.prototype_pain.recipe.ModRecipes;
import net.adinvas.prototype_pain.recipe.ingridients.FluidIngredient;
import net.adinvas.prototype_pain.recipe.ingridients.ItemIngredient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class MedicalMixerBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(19);
    private final IFluidHandler fluidHandler = new IFluidHandler() {
        @Override
        public int getTanks() {
            return Tanks.length;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int i) {
            return Tanks[i].getFluid();
        }

        @Override
        public int getTankCapacity(int i) {
            return Tanks[i].getCapacity();
        }

        @Override
        public boolean isFluidValid(int i, @NotNull FluidStack fluidStack) {
            if (i > 2) return false; // only input tanks accept external fill
            FluidStack innerStack = getFluidInTank(i);
            return isSameFluidAndOrSameTag(innerStack, fluidStack) || innerStack.isEmpty();
        }

        private boolean isSameFluidAndOrSameTag(FluidStack innerStack, FluidStack fluidStack) {
            if (innerStack.isFluidEqual(fluidStack)) return true;
            return (innerStack.getTag() == null && fluidStack.getTag() == null)
                    || (innerStack.getTag() != null && innerStack.getTag().equals(fluidStack.getTag()));
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            int remaining = resource.getAmount();

            // Fill input tanks 0–2
            for (int i = 0; i <= 2; i++) {
                FluidTank tank = Tanks[i];
                if (!isFluidValid(i, resource)) continue;
                FluidStack stack = resource.copy();
                stack.setAmount(remaining);

                int filled = tank.fill(stack, action);
                if (filled > 0 && action.execute()) setChanged();
                remaining -= filled;
                if (remaining <= 0) break;
            }

            return resource.getAmount() - remaining;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            int remaining = resource.getAmount();
            FluidStack drainedTotal = new FluidStack(resource.getFluid(), 0);

            // Drain only from output tanks 3–5
            for (int i = 3; i <= 5; i++) {
                FluidTank tank = Tanks[i];
                if (!isSameFluidAndOrSameTag(tank.getFluid(), resource)) continue;

                int toDrain = Math.min(tank.getFluid().getAmount(), remaining);
                FluidStack drained = tank.drain(toDrain, action);
                if (drained.isEmpty())return FluidStack.EMPTY;
                drainedTotal.grow(drained.getAmount());
                remaining -= drained.getAmount();
                if (remaining <= 0) break;
            }

            return drainedTotal;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            FluidStack drainedTotal = FluidStack.EMPTY;

            // Drain only from output tanks 3–5
            for (int i = 3; i <= 5; i++) {
                FluidStack inTank = Tanks[i].getFluid();
                if (inTank.isEmpty()) continue;

                int toDrain = Math.min(inTank.getAmount(), maxDrain - drainedTotal.getAmount());
                FluidStack drained = Tanks[i].drain(toDrain, action);

                if (drainedTotal.isEmpty()) drainedTotal = drained.copy();
                else drainedTotal.grow(drained.getAmount());

                if (drainedTotal.getAmount() >= maxDrain) break;
            }

            return drainedTotal;
        }
    };
    private final int TANK_CAPACITY = 1000;
    private final FluidTank[] Tanks = new FluidTank[] {
            new FluidTank(TANK_CAPACITY){
                @Override
                protected void onContentsChanged() {
                    super.onContentsChanged();
                    if (!level.isClientSide()){
                        ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)),new FluidSyncS2CPacket(this.fluid,0,worldPosition));
                    }
                }
            },
            new FluidTank(TANK_CAPACITY){
                @Override
                protected void onContentsChanged() {
                    super.onContentsChanged();
                    if (!level.isClientSide()){
                        ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)),new FluidSyncS2CPacket(this.fluid,1,worldPosition));
                    }
                }
            },
            new FluidTank(TANK_CAPACITY){
                @Override
                protected void onContentsChanged() {
                    super.onContentsChanged();
                    if (!level.isClientSide()){
                        ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)),new FluidSyncS2CPacket(this.fluid,2,worldPosition));
                    }
                }
            },
            new FluidTank(TANK_CAPACITY){
                @Override
                protected void onContentsChanged() {
                    super.onContentsChanged();
                    if (!level.isClientSide()){
                        ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)),new FluidSyncS2CPacket(this.fluid,3,worldPosition));
                    }
                }
            },
            new FluidTank(TANK_CAPACITY){
                @Override
                protected void onContentsChanged() {
                    super.onContentsChanged();
                    if (!level.isClientSide()){
                        ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)),new FluidSyncS2CPacket(this.fluid,4,worldPosition));
                    }
                }
            },
            new FluidTank(TANK_CAPACITY){
                @Override
                protected void onContentsChanged() {
                    super.onContentsChanged();
                    if (!level.isClientSide()){
                        ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)),new FluidSyncS2CPacket(this.fluid,5,worldPosition));
                    }
                }
            }
    };

    protected final ContainerData data;
    private int progress =0;
    private int maxProgress = 60;

    public void setFluidInTank(FluidStack stack, int id){
        if (id>=0&&id<Tanks.length)
            Tanks[id].setFluid(stack);
    }

    public FluidStack getFluidInTank(int id) {
        if (id>=0&&id<Tanks.length)
            return Tanks[id].getFluid();
        return FluidStack.EMPTY;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

    public MedicalMixerBlockEntity( BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MEDICAL_MIXER_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i){
                    case 0 -> MedicalMixerBlockEntity.this.progress;
                    case 1 -> MedicalMixerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int i1) {
                switch (i){
                    case 0 -> MedicalMixerBlockEntity.this.progress = i1;
                    case 1 -> MedicalMixerBlockEntity.this.maxProgress = i1;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

    }



    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER){
            return lazyItemHandler.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER){
            return lazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(()-> itemHandler);
        lazyFluidHandler = LazyOptional.of(()->fluidHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    public void drops(){
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i =0;i<itemHandler.getSlots();i++){
            inventory.setItem(i,itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level,this.worldPosition,inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.prototype_pain.medical_mixer");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new MedicalMixerMenu(i,inventory,this,this.data);
    }


    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("Inventory",itemHandler.serializeNBT());
        pTag.putInt("medical_mixer.progress",progress);
        ListTag tankList = new ListTag();
        for (FluidTank tank: Tanks){
            CompoundTag tankTag = new CompoundTag();
            tank.writeToNBT(tankTag);
            tankList.add(tankTag);
        }
        pTag.put("Tanks",tankList);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("Inventory"));
        progress = pTag.getInt("medical_mixer.progress");
        ListTag tankList = pTag.getList("Tanks", Tag.TAG_COMPOUND);
        for (int i = 0; i < tankList.size() && i < Tanks.length; i++) {
            CompoundTag tankTag = tankList.getCompound(i);
            Tanks[i].readFromNBT(tankTag);
        }
    }


    public void tick(Level pLevel, BlockPos pBlockPos, BlockState pState) {
        handleFluidItems();
        if (hasRecipe()){
            increseCraftingProgress();
            setChanged(pLevel,pBlockPos,pState);
            if (hasProgressFinished()){
                craftItem();
                resetProgress();
                setChanged();
            }
        }else{
            resetProgress();
        }
    }

    private void handleFluidItems() {
        for (int slot = 10; slot <= 12; slot++) {
            ItemStack itemStack = itemHandler.getStackInSlot(slot);
            if (itemStack.isEmpty()) continue;

            int internalTankIndex = slot - 10;
            FluidTank internalTank = Tanks[internalTankIndex];

            int finalSlot = slot;
            itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(itemTank -> {
                FluidStack simulatedDrain = itemTank.drain(1000, IFluidHandler.FluidAction.SIMULATE);
                if (simulatedDrain.isEmpty()) return;

                int filled = internalTank.fill(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);
                if (filled <= 0) return;

                FluidStack drained = itemTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                internalTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);

                // 🔴 THIS IS THE MISSING PART
                ItemStack newContainer = itemTank.getContainer();
                itemHandler.setStackInSlot(finalSlot, newContainer);

                setChanged();
            });
        }

        // --- INTERNAL TANKS -> OUTPUT SLOTS ---
        for (int slot = 13; slot <= 15; slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            int internalTankIndex = slot - 13;
            FluidTank internalTank = Tanks[internalTankIndex];

            int finalSlot = slot;
            stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(itemTank -> {
                FluidStack available = internalTank.getFluid();
                if (available.isEmpty()) return;

                FluidStack toFill = available.copy();
                toFill.setAmount(Math.min(1000, toFill.getAmount()));

                int filled = itemTank.fill(toFill, IFluidHandler.FluidAction.SIMULATE);
                if (filled <= 0) return;

                FluidStack drained = internalTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                itemTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);

                // 🔴 REQUIRED
                ItemStack newContainer = itemTank.getContainer();
                itemHandler.setStackInSlot(finalSlot, newContainer);

                setChanged();
            });
        }

        for (int slot = 16; slot <= 18; slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            int internalTankIndex = slot - 16 + 3; // 16→3, 17→4, 18→5
            FluidTank internalTank = Tanks[internalTankIndex];

            stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(itemTank -> {
                FluidStack fluidToFill = internalTank.getFluid().copy();
                if (fluidToFill.isEmpty()) return;
                fluidToFill.setAmount(Math.min(100, fluidToFill.getAmount()));
                int filled = itemTank.fill(fluidToFill, IFluidHandler.FluidAction.SIMULATE);
                if (filled > 0) {
                    FluidStack toDrain = internalTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    itemTank.fill(toDrain, IFluidHandler.FluidAction.EXECUTE);
                    setChanged();
                }
            });
        }
    }

    private void resetProgress() {
        progress=0;
    }

    private boolean hasProgressFinished() {
        return progress >=maxProgress;
    }

    private void increseCraftingProgress() {
        progress++;
    }
    private MedicalMixerRecipe cashedRecipe;
    private boolean hasRecipe() {
        List<MedicalMixerRecipe> recipes = this.level.getRecipeManager().getAllRecipesFor(ModRecipes.MEDICAL_MIXER_RECIPE.get());

        for (MedicalMixerRecipe recipe:recipes){

            if(recipe.matches(itemHandler,getFluidsinTanks())){

                if (canInsertInOutputSlot(recipe.getItemOutputs().toArray(new ItemStack[0])) &&
                        canInsertInOutputTank(recipe.getFluidOutputs().toArray(new FluidStack[0]))){
                    cashedRecipe = recipe;
                    maxProgress = cashedRecipe.getProcessingTime();
                    return true;
                }
            }
        }
        return false;
    }

    private List<FluidStack> getFluidsinTanks() {
        List<FluidStack> stacks = new ArrayList<>();
        for (FluidTank tank: Tanks){
            stacks.add(tank.getFluid());
        }
        return stacks;
    }

    private void craftItem() {
        if (cashedRecipe!=null){
            List<ItemIngredient> inputitems = cashedRecipe.getItemInputs();
            for (ItemIngredient ingredient:inputitems){
                ingredient.consume(itemHandler,0,4);
            }
            List<FluidIngredient> inputfluids = cashedRecipe.getFluidInputs();
            for (FluidIngredient ingredient: inputfluids){
                extractFluidFromInputTanks(ingredient.getAsFluidStack());
            }
            List<ItemStack> stacks = cashedRecipe.getItemOutputs();
            addItemsToOutputSlot(stacks.toArray(new ItemStack[0]));
            List<FluidStack> fluidOutputs = cashedRecipe.getFluidOutputs();
            addFluidsToOutputTanks(fluidOutputs.toArray(new FluidStack[0]));
            setChanged();

        }
    }

    private void addFluidsToOutputTanks(FluidStack[] fluidResult) {
        for (FluidStack fs : fluidResult){
            PrototypePain.LOGGER.info(" t {}",fs.getOrCreateTag().getString("MedicalId"));
            for (int i=0;i<Tanks.length;i++){
                if (i<=2)continue;
                FluidTank tank = Tanks[i];
                if (tank.isEmpty()){
                    tank.fill(fs, IFluidHandler.FluidAction.EXECUTE);
                    break;
                }
                FluidStack fluidStack = tank.getFluid();
                if (isSameFluidAndOrSameTag(fs,fluidStack)){
                    tank.fill(fs, IFluidHandler.FluidAction.EXECUTE);
                    break;
                }
            }
        }
    }

    private void addItemsToOutputSlot(ItemStack[] itemResults) {
        for (ItemStack itemStack : itemResults) {
            int amountLeft = itemStack.getCount();
            Item item = itemStack.getItem();

            for (int i = 5; i <= 9 && amountLeft > 0; i++) {
                ItemStack slotStack = itemHandler.getStackInSlot(i);

                if (slotStack.isEmpty()) {
                    ItemStack toInsert = new ItemStack(item, amountLeft);
                    if (toInsert.getItem() instanceof INbtDrivenDurability nbtDrivenDurability){
                        nbtDrivenDurability.setupDefaults(toInsert);
                    }
                    ItemStack leftover = itemHandler.insertItem(i, toInsert, false);
                    amountLeft = leftover.getCount();
                } else if (slotStack.is(item)) {
                    ItemStack toInsert = new ItemStack(item, amountLeft);
                    if (toInsert.getItem() instanceof INbtDrivenDurability nbtDrivenDurability){
                        nbtDrivenDurability.setupDefaults(toInsert);
                    }
                    ItemStack leftover = itemHandler.insertItem(i, toInsert, false);
                    amountLeft = leftover.getCount();
                }
            }

            if (amountLeft > 0) {
                PrototypePain.LOGGER.warn("Could not insert full output stack of " + itemStack.getItem() + ", leftover: " + amountLeft);
            }
        }
    }

    private void extractFluidFromInputTanks(FluidStack toDrain) {
        int remaining = toDrain.getAmount();

        for (FluidTank tank : Tanks) {
            FluidStack inTank = tank.getFluid();
            if (isSameFluidAndOrSameTagExtended(inTank, toDrain) && !inTank.isEmpty()) {
                int drainedAmount = Math.min(inTank.getAmount(), remaining);
                tank.drain(drainedAmount, IFluidHandler.FluidAction.EXECUTE);
                remaining -= drainedAmount;

                if (remaining <= 0) return; // fully drained
            }
        }
    }

    private boolean canInsertInOutputTank(FluidStack[] fluidResult) {
        for (FluidStack fs : fluidResult){
            boolean filled = false;
            for (int i=0; i<Tanks.length;i++){
                if (i<=2)continue;
                FluidTank tank = Tanks[i];
                if (tank.fill(fs, IFluidHandler.FluidAction.SIMULATE)>=fs.getAmount()){
                    filled = true;
                    break;
                }
            }
            if (!filled)return false;
        }
        return true;
    }

    private boolean canInsertInOutputSlot(ItemStack[] itemResults) {
        for (ItemStack itemResult : itemResults) {
            boolean filled = false;
            for (int i = 5; i < itemHandler.getSlots(); i++) {
                if (i > 9) break;
                ItemStack toCheck = itemHandler.getStackInSlot(i);
                if (toCheck.isEmpty()) {
                    filled = true;
                    break;
                }
                if (itemResult.is(toCheck.getItem())) {
                    if (toCheck.getCount() + itemResult.getCount() <= toCheck.getMaxStackSize()) {
                        filled = true;
                        break;
                    }
                }
            }
            if (!filled) return false;
        }
        return true;
    }




    public boolean isSameFluidAndOrSameTag(FluidStack innerStack,FluidStack fluidStack){
        if (innerStack.isFluidEqual(fluidStack))return true;
        return (innerStack.getTag() == null && fluidStack.getTag() == null)
                || (innerStack.getTag() != null && innerStack.getTag().equals(fluidStack.getTag()));
    }

    public boolean isSameFluidAndOrSameTagExtended(FluidStack stack, FluidStack other) {
        if (stack.isEmpty() || other.isEmpty()) return false;

        // Exact fluid equality
        if (stack.getFluid().isSame(other.getFluid())) {
            // Check NBT if present
            if (stack.hasTag() && other.hasTag()) {
                return stack.getTag().equals(other.getTag());
            }
            return true;
        }

        // Forge fluid tags
        for (FluidIngredient ingredient : cashedRecipe.getFluidInputs()) {
            if (ingredient.getFluidTag() != null && other.getFluid().is(ingredient.getFluidTag())) return true;

            // MedicalFluid tag
            if (ingredient.getMedicalTag() != null
                    && other.getFluid().isSame(ModFluids.SRC_MEDICAL.get())
                    && other.hasTag()
            ) {
                String id = other.getTag().getString("MedicalId");
                if (!id.isEmpty()) {
                    MedicalFluid medical = MedicalFluid.getFromId(id);
                    if (medical != null && medical.is(ingredient.getMedicalTag())) return true;
                }
            }
        }

        return false;
    }


    public void sendUpdates(Level pLevel, Player pPlayer) {
        if (pLevel.isClientSide())return;
        for (int i=0; i<Tanks.length;i++){
            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(()-> (ServerPlayer) pPlayer)
                    ,new FluidSyncS2CPacket(getFluidInTank(i),i,worldPosition));
        }
    }
}
