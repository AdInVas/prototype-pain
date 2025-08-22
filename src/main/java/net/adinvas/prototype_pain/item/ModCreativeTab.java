package net.adinvas.prototype_pain.item;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PrototypePain.MOD_ID);

    public static final RegistryObject<CreativeModeTab> YOUR_TAB = CREATIVE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.prototype_pain_tab")) // lang key
                    .icon(() -> new ItemStack(ModItems.Dressing.get())) // icon for the tab
                    .displayItems((parameters, output) -> {
                        // Add all your mod’s items here:
                        output.accept(ModItems.Dressing.get());
                        output.accept(ModItems.Alcohol.get());
                        output.accept(ModItems.Ice_Pack.get());
                        output.accept(ModItems.Tourniquet.get());
                        output.accept(ModItems.Opium.get());
                        output.accept(ModItems.Saline.get());
                    })
                    .build()
    );
}
