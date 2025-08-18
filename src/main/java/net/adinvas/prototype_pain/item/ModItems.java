package net.adinvas.prototype_pain.item;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PrototypePain.MOD_ID);

    public static final RegistryObject<Item> Dressing = ITEMS.register("dressing",()->new DressingItem(new Item.Properties()
            .durability(3)
    ));
    public static final RegistryObject<Item> Tourniquet = ITEMS.register("tourniquet",()->new TourniquetItem(new Item.Properties().stacksTo(1)));



}
