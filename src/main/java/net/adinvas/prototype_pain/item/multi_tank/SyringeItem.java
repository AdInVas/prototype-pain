package net.adinvas.prototype_pain.item.multi_tank;

import net.adinvas.prototype_pain.client.MinigameOpener;
import net.adinvas.prototype_pain.item.IAllowInMedicbags;
import net.adinvas.prototype_pain.item.IMedicalMinigameUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

public class SyringeItem extends MultiTankFluidItem implements IMedicalMinigameUsable, IAllowInMedicbags {


    @Override
    public int getCapacity() {
        return 100;
    }

    @Override
    public void openMinigameScreen(Player target, ItemStack stack, @Nullable Limb limb, InteractionHand hand) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () ->{
            MinigameOpener.OpenSyringeMinigame(target,stack,limb,hand);
        });
    }

    @Override
    public void openMinigameBagScreen(Player target, ItemStack stack, ItemStack bagStack, int slot, @Nullable Limb limb, InteractionHand hand) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () ->{
            MinigameOpener.OpenSyringeMinigame(target,stack,bagStack,slot,limb,hand);
        });
    }

    @Override
    public void useMinigameAction(float durability, Player target, @Nullable Limb limb) {

    }

}
