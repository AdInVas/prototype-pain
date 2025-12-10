package net.adinvas.prototype_pain.events;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.Util;
import net.adinvas.prototype_pain.fluid_system.MultiTankHelper;
import net.adinvas.prototype_pain.item.ModItems;
import net.adinvas.prototype_pain.item.multi_tank.MultiTankFluidItem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PrototypePain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ColorsEvent {
    @SubscribeEvent
    public static void onItemColors(RegisterColorHandlersEvent.Item event){
        event.register(
                (stack,tintIndex)->{

                    if (tintIndex == 1) { // only tint the syringe liquid part
                        if (stack.getItem() instanceof MultiTankFluidItem) {
                            if (MultiTankHelper.getFilledTotal(stack) <=0){
                                return 0x00FFFFFF;
                            }
                            return Util.mixColors(MultiTankHelper.getColorRatios(stack, Minecraft.getInstance().level)); // return full ARGB or RGB color
                        }
                    }
                    return 0xFFFFFFFF; // white = no tint
                },
                ModItems.MedicineVial.get()

        );

    }
}
