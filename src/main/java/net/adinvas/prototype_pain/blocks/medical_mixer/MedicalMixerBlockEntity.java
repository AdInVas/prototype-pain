package net.adinvas.prototype_pain.blocks.medical_mixer;

import cpw.mods.util.Lazy;
import net.adinvas.prototype_pain.ModMedicalFluids;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.blocks.ModBlockEntities;
import net.adinvas.prototype_pain.fluid_system.MedicalFluid;
import net.adinvas.prototype_pain.fluid_system.ModFluids;
import net.adinvas.prototype_pain.network.FluidSyncS2CPacket;
import net.adinvas.prototype_pain.network.ModNetwork;
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


public class MedicalMixerBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(19);
    private IFluidHandler fluidHandler = new IFluidHandler() {
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
            if (i>2)return false;
            FluidStack innerStack = getFluidInTank(i);
            return isSameFluidAndOrSameTag(innerStack,fluidStack);
        }

        public boolean isSameFluidAndOrSameTag(FluidStack innerStack,FluidStack fluidStack){
            if (innerStack.isFluidEqual(fluidStack))return true;
            return (innerStack.getTag() == null && fluidStack.getTag() == null)
                    || (innerStack.getTag() != null && innerStack.getTag().equals(fluidStack.getTag()));
        }

        @Override
        public int fill(FluidStack fluidStack, FluidAction fluidAction) {
            for (int i = 0; i < Tanks.length; i++) {
                FluidTank tank = Tanks[i];
                if (i>2)return 0;

                // Only fill tanks that are valid for this fluid
                if (!isFluidValid(i, fluidStack)) continue;

                // Use the tank's internal fill
                int filled = tank.fill(fluidStack, fluidAction);
                if (filled > 0 && fluidAction.execute()) {
                    setChanged();
                }
                return filled;
            }
            return 0;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
            if (fluidStack.isEmpty()) return FluidStack.EMPTY;

            for (FluidTank tank : Tanks) {
                if (isSameFluidAndOrSameTag(tank.getFluid(),fluidStack)){
                    FluidStack drained = tank.drain(fluidStack.getAmount(),fluidAction);
                    if (!drained.isEmpty()){
                        if (fluidAction.execute()){
                            setChanged();
                        }
                        return drained;
                    }
                }
            }
            return FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(int i, FluidAction fluidAction) {
            if (i <= 0) return FluidStack.EMPTY;
            for (FluidTank tank : Tanks) {
                if (!tank.isEmpty()) {
                    FluidStack drained = tank.drain(i, fluidAction);
                    if (!drained.isEmpty()) {
                        if (fluidAction.execute()){
                            setChanged();
                        }
                        return drained;
                    }
                }
            }
            return FluidStack.EMPTY;
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

            itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(itemTank -> {
                FluidStack simulatedDrain = itemTank.drain(100, IFluidHandler.FluidAction.SIMULATE);
                int filled = internalTank.fill(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);
                if (filled > 0) {
                    FluidStack drained = itemTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    internalTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                    setChanged();
                }
            });
        }

        // --- INTERNAL TANKS -> OUTPUT SLOTS ---
        for (int slot = 13; slot <= 15; slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            int internalTankIndex = slot - 13;
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

    private boolean hasRecipe() {
        boolean hasCraftingRecipe = hasItemInInputSlot(Items.CHARCOAL,4)&&
                hasFluidInInputTanks(new FluidStack(Fluids.WATER,1000));

        FluidStack[] fluidResult = {
                makeMedicalFluid(ModMedicalFluids.CLEAN_WATER.get(), 500)
        };
        ItemStack[] itemResults = {

        };
        return hasCraftingRecipe &&
                canInsertInOutputSlot(itemResults) &&
                canInsertInOutputTank(fluidResult);
    }

    private void craftItem() {
        FluidStack[] fluidResult = {
                makeMedicalFluid(ModMedicalFluids.CLEAN_WATER.get(), 500)
        };
        ItemStack[] itemResults = {

        };
        extractItemFromInputSlots(Items.CHARCOAL,4);
        extractFluidFromInputTanks(new FluidStack(Fluids.WATER,1000));

        addItemsToOutputSlot(itemResults);
        addFluidsToOutputTanks(fluidResult);
        setChanged();
    }

    private void addFluidsToOutputTanks(FluidStack[] fluidResult) {
        for (FluidStack fs : fluidResult){
            for (int i=0;i<Tanks.length;i++){
                if (i<=2)continue;
                FluidTank tank = Tanks[i];
                if (tank.isEmpty()){
                    tank.fill(fs, IFluidHandler.FluidAction.EXECUTE);
                    PrototypePain.LOGGER.info("filled");
                    break;
                }
                FluidStack fluidStack = tank.getFluid();
                if (isSameFluidAndOrSameTag(fs,fluidStack)){
                    tank.fill(fs, IFluidHandler.FluidAction.EXECUTE);
                    PrototypePain.LOGGER.info("filled");
                    break;
                }
            }
        }
    }

    private void addItemsToOutputSlot(ItemStack[] itemResults) {
        for (ItemStack itemStack : itemResults){
            for (int i = 5; i < itemHandler.getSlots(); i++) {
                if (i > 9) break;
                ItemStack toCheck = itemHandler.getStackInSlot(i);
                if (toCheck.isEmpty()) {
                    itemHandler.insertItem(i,itemStack,false);
                    break;
                }
                if (itemStack.is(toCheck.getItem())) {
                    if (toCheck.getCount() + itemStack.getCount() <= toCheck.getMaxStackSize()) {
                        itemHandler.insertItem(i,new ItemStack(toCheck.getItem(),toCheck.getCount()+itemStack.getCount()),false);
                        break;
                    }
                }
            }
        }
    }

    private void extractFluidFromInputTanks(FluidStack fluidStack) {
        for (FluidTank tank : Tanks) {
            FluidStack inTank = tank.getFluid();
            if (isSameFluidAndOrSameTag(inTank, fluidStack)) {
                tank.drain(fluidStack.getAmount(), IFluidHandler.FluidAction.EXECUTE);
                return;
            }
        }
    }

    private void extractItemFromInputSlots(Item item, int count) {
        for (int i=0;i<itemHandler.getSlots();i++){
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getItem()==item){
                if (stack.getCount()>=count){
                    itemHandler.extractItem(i,count,false);
                    return;
                }
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
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                if (i <= 4) continue;
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


    private boolean hasFluidInInputTanks(FluidStack fluidStack) {
        for (int i=0;i<Tanks.length;i++){
            if (i>2)break;
            FluidTank tank = Tanks[i];
            FluidStack fs = tank.getFluid();
            if (isSameFluidAndOrSameTag(fs,fluidStack)){
                if (fs.getAmount()>=fluidStack.getAmount()){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSameFluidAndOrSameTag(FluidStack innerStack,FluidStack fluidStack){
        if (innerStack.isFluidEqual(fluidStack))return true;
        return (innerStack.getTag() == null && fluidStack.getTag() == null)
                || (innerStack.getTag() != null && innerStack.getTag().equals(fluidStack.getTag()));
    }

    private boolean hasItemInInputSlot(Item item,int count) {
        for (int i =0;i<itemHandler.getSlots();i++){
            if (i>4)break;
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getItem() == item){
                if (stack.getCount()>=count){
                    return true;
                }
            }
        }
        return false;
    }

    public FluidStack makeMedicalFluid(MedicalFluid medicalFluid,int amount){
        FluidStack stack = new FluidStack(ModFluids.SRC_MEDICAL.get().getSource(),amount);
        stack.getOrCreateTag().putString("MedicalId",medicalFluid.getRegistryId().toString());
        return stack;
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
