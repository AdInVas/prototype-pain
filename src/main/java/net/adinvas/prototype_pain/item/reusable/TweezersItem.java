package net.adinvas.prototype_pain.item.reusable;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.client.MinigameOpener;
import net.adinvas.prototype_pain.item.IAllowInMedicbags;
import net.adinvas.prototype_pain.item.IMedicalMinigameUsable;
import net.adinvas.prototype_pain.item.ISimpleMedicalUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TweezersItem extends Item implements IMedicalMinigameUsable, IAllowInMedicbags {
    public TweezersItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.tweezers.description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void openMinigameScreen(Player target, ItemStack stack, @Nullable Limb limb, InteractionHand hand) {
        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            if (h.hasLimbShrapnell(limb)>0){
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () ->{
                    MinigameOpener.OpenShrapnelMinigame(target,limb);
                });
            }
        });
    }
    @Override
    public void openMinigameBagScreen(Player target, ItemStack stack, ItemStack bagStack,int lost, @Nullable Limb limb, InteractionHand hand) {
        this.openMinigameScreen(target,stack,limb,hand);
    }
    @Override
    public void useMinigameAction(float durability, Player target, @Nullable Limb limb) {

    }
}
