package net.adinvas.prototype_pain.item.usable;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ThermometerItem extends Item {

    public ThermometerItem() {
        super(new Properties().stacksTo(1));
    }

    int ticker = 0;
    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        if (slotIndex!=selectedIndex)return;
        if (ticker++>10){
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                float temperatureScale = Mth.clamp((h.getAmbientTemperature(player)-27)/15,0,1);
                int color = Util.gradient(temperatureScale,0x242bff,0xff3624);
                Component colorText  = Component.literal("(").withStyle(ChatFormatting.GRAY).append(Component.literal(Math.floor(h.getAmbientTemperature(player)*10)/10+"C").withStyle(Style.EMPTY.withColor(color))).append(Component.literal(")").withStyle(ChatFormatting.GRAY));
                player.displayClientMessage(colorText,true);
            });
            ticker=0;
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack,pLevel,pTooltipComponents,pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.prototype_pain.thermometer.discription").withStyle(ChatFormatting.GRAY));
    }


    @OnlyIn(Dist.CLIENT)
    public static class ClientThermoTooltip implements ClientTooltipComponent,TooltipComponent {

        @Override
        public int getHeight() {
            return 20;
        }

        @Override
        public int getWidth(Font font) {
            return 80;
        }

        @Override
        public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics) {
            pGuiGraphics.renderItem(ModItems.Thermometer.get().getDefaultInstance(), pX, pY);
        }
    }

}
