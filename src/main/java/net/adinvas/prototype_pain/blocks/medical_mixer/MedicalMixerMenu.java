package net.adinvas.prototype_pain.blocks.medical_mixer;

import net.adinvas.prototype_pain.FluidTankRenderer;
import net.adinvas.prototype_pain.ModMenus;
import net.adinvas.prototype_pain.blocks.ModBlocks;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class MedicalMixerMenu extends AbstractContainerMenu {
    public final MedicalMixerBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private FluidStack[] tanks = new FluidStack[6];

    public MedicalMixerMenu(int pContainerId, Inventory inv, FriendlyByteBuf buf){
        this(pContainerId,inv,inv.player.level().getBlockEntity(buf.readBlockPos()),new SimpleContainerData(19));
    }

    public MedicalMixerMenu(int pContainerId, Inventory inv, BlockEntity blockEntity,ContainerData data){
        super(ModMenus.MEDICAL_MIXER.get(),pContainerId);
        checkContainerSize(inv,2);
        this.blockEntity = ((MedicalMixerBlockEntity) blockEntity);
        this.level = inv.player.level();
        this.data = data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        for (int i=0;i<tanks.length;i++){
            tanks[i] = this.blockEntity.getFluidInTank(i);
        }


        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            int id = 0;
            this.addSlot(new SlotItemHandler(iItemHandler,id++,82,8));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,100,8));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,118,8));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,91,26));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,109,26));

            this.addSlot(new SlotItemHandler(iItemHandler,id++,91,54));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,109,54));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,82,72));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,100,72));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,118,72));

            this.addSlot(new SlotItemHandler(iItemHandler,id++,8,8));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,28,8));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,48,8));

            this.addSlot(new SlotItemHandler(iItemHandler,id++,8,72));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,28,72));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,48,72));

            this.addSlot(new SlotItemHandler(iItemHandler,id++,152,72));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,172,72));
            this.addSlot(new SlotItemHandler(iItemHandler,id++,192,72));

        });



        addDataSlots(data);


    }

    public boolean isCrafting(){
        return data.get(0)>0;
    }

    public float getScaledProgress(){
        int progress = this.data.get(0);
        int maxProgress= this.data.get(1);
        return maxProgress !=0 &&progress!=0 ? (float) progress /maxProgress :0;
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 19;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }


    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level,blockEntity.getBlockPos()),player, ModBlocks.MEDICAL_MIXER.get());
    }


    private void addPlayerInventory(Inventory inv){
        int startX = 28;
        int startY = 104;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, startX + col * 18, startY + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv){
        int startX = 28;
        int startY = 162;
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col, startX + col * 18, startY));
        }
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    public void setFluidInTank(FluidStack fluid, int tankID) {
        tanks[tankID] = fluid;
    }
}
