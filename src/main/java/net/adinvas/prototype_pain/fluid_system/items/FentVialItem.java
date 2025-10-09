package net.adinvas.prototype_pain.fluid_system.items;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.fluid_system.MedicalFluids;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FentVialItem extends MedicalVial {


    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        super.onCraftedBy(pStack, pLevel, pPlayer);
        if (!pStack.hasTag()) {
            PrototypePain.LOGGER.info("filled");
            addFluid(pStack, 10, MedicalFluids.FENTANYL);
            addFluid(pStack, 90, MedicalFluids.WATER);
        }
    }

    @Override
    public void setupDefaults(ItemStack stack) {
        addFluid(stack, 10, MedicalFluids.FENTANYL);
        addFluid(stack, 90, MedicalFluids.WATER);
    }

}
