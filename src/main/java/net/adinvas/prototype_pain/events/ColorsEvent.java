package net.adinvas.prototype_pain.events;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.fluid_system.items.MedicalVial;
import net.adinvas.prototype_pain.fluid_system.items.SyringeItem;
import net.adinvas.prototype_pain.item.ModItems;
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
                        if (stack.getItem() instanceof MedicalVial syringe) {
                            if (syringe.getFilledTotal(stack)<=0){
                                return 0x00FFFFFF;
                            }
                            return SyringeItem.mixColors(syringe.getFuildAndRatio(stack)); // return full ARGB or RGB color
                        }
                    }
                    return 0xFFFFFFFF; // white = no tint
                },
                ModItems.MedicineVial.get(),
                ModItems.OpiumVial.get(),
                ModItems.HeroinVial.get(),
                ModItems.FentanylVial.get(),
                ModItems.MorphineVial.get()
        );
    }
}
