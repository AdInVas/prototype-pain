package net.adinvas.prototype_pain.item;

import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IMedicalMinigameUsable {
   void openMinigameScreen(Player target, ItemStack stack, @Nullable Limb limb, InteractionHand hand);
   void openMinigameBagScreen(Player target, ItemStack stack,ItemStack bagStack,int slot, @Nullable Limb limb, InteractionHand hand);
   void useMinigameAction(float durability, Player target, @Nullable Limb limb);
}
