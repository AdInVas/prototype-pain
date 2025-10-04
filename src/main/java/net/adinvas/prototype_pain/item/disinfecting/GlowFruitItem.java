package net.adinvas.prototype_pain.item.disinfecting;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.blocks.ModBlocks;
import net.adinvas.prototype_pain.item.IMedUsable;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.network.UseMedItemPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlowFruitItem extends Item implements IMedUsable {

    public GlowFruitItem() {
        super(new Item.Properties()
                .stacksTo(64)
                .food(new FoodProperties.Builder()
                        .effect(() -> new MobEffectInstance(MobEffects.GLOWING, 200, 0), 1.0F)
                        .effect(() -> new MobEffectInstance(MobEffects.POISON,100,3),1f)
                        .nutrition(1)
                        .saturationMod(1)
                        .build()
                )
        );
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        BlockState state = world.getBlockState(context.getClickedPos());

        boolean block_test = state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.STONE)||state.is(Blocks.DEEPSLATE)
                ||state.is(Blocks.ANDESITE)
                ||state.is(Blocks.DIORITE)
                ||state.is(Blocks.TUFF)
                ||state.is(Blocks.GRANITE)
                ||state.is(Blocks.COBBLESTONE)
                ||state.is(Blocks.COBBLED_DEEPSLATE);

        if (world.getBlockState(pos).isAir()&& block_test) {
            world.setBlock(pos, ModBlocks.GLOW_FRUIT_BUSH.get().defaultBlockState(), 3);
            context.getItemInHand().shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean onMedicalUse(Limb limb, ServerPlayer source, ServerPlayer target, ItemStack stack, InteractionHand hand) {
        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            h.setLimbSkinHealth(limb,h.getLimbSkinHealth(limb)-1);
            h.setLimbMuscleHealth(limb,h.getLimbMuscleHealth(limb)-4);
            h.setLimbDesinfected(limb,Math.max(h.getLimbDesinfected(limb),4400));
            List<Limb> conected = limb.getConnectedLimbs();
            for (Limb limb1: conected){
                h.setLimbMuscleHealth(limb1,h.getLimbMuscleHealth(limb1)-3);
                h.setLimbDesinfected(limb1,Math.max(h.getLimbDesinfected(limb1),2200));
            }
            stack.shrink(1);
        });
        return true;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.glow_fruit.discription").withStyle(ChatFormatting.GRAY));
    }
}
