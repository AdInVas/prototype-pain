package net.adinvas.prototype_pain.item;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.item.bags.large.LargeMedibagItem;
import net.adinvas.prototype_pain.item.bags.medium.MediumMedibagItem;
import net.adinvas.prototype_pain.item.bags.small.SmallMedibagItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
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

                        for (RegistryObject<Item> itemRegistryObject : ModItems.ITEMS.getEntries()){
                            if (itemRegistryObject.get() instanceof MediumMedibagItem||itemRegistryObject.get() instanceof SmallMedibagItem||itemRegistryObject.get() instanceof LargeMedibagItem)continue;
                            output.accept(itemRegistryObject.get());
                        }
                    })
                    .build()
    );
}
