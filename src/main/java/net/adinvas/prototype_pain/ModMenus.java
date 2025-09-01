package net.adinvas.prototype_pain;

import net.adinvas.prototype_pain.item.bags.large.LargeMedibagMenu;
import net.adinvas.prototype_pain.item.bags.medium.MediumMedibagMenu;
import net.adinvas.prototype_pain.item.bags.small.SmallMedibagMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, PrototypePain.MOD_ID);

    public static final RegistryObject<MenuType<SmallMedibagMenu>> SMALL_MEDIBAG =
            MENUS.register("small_medibag",
                    () -> IForgeMenuType.create((id, inv, buf) -> {
                        boolean mainHand = buf.readBoolean();
                        ItemStack stack = mainHand ? inv.player.getMainHandItem() : inv.player.getOffhandItem();
                        return new SmallMedibagMenu(id, inv, stack);
                    }));

    public static final RegistryObject<MenuType<MediumMedibagMenu>> MEDIUM_MEDIBAG =
            MENUS.register("medium_medibag",
                    () -> IForgeMenuType.create((id, inv, buf) -> {
                        boolean mainHand = buf.readBoolean();
                        ItemStack stack = mainHand ? inv.player.getMainHandItem() : inv.player.getOffhandItem();
                        return new MediumMedibagMenu(id, inv, stack);
                    }));

    public static final RegistryObject<MenuType<LargeMedibagMenu>> LARGE_MEDIBAG =
            MENUS.register("large_medibag",
                    () -> IForgeMenuType.create((id, inv, buf) -> {
                        boolean mainHand = buf.readBoolean();
                        ItemStack stack = mainHand ? inv.player.getMainHandItem() : inv.player.getOffhandItem();
                        return new LargeMedibagMenu(id, inv, stack);
                    }));
}
