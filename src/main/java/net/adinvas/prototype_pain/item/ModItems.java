package net.adinvas.prototype_pain.item;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.blocks.ModBlocks;
import net.adinvas.prototype_pain.item.bags.large.LargeMedibagItem;
import net.adinvas.prototype_pain.item.bags.medium.MediumMedibagItem;
import net.adinvas.prototype_pain.item.bags.small.SmallMedibagItem;
import net.adinvas.prototype_pain.item.bandages.*;
import net.adinvas.prototype_pain.item.fluid_vials.*;
import net.adinvas.prototype_pain.item.fluid_vials.bottles.AlcoholItem;
import net.adinvas.prototype_pain.item.fluid_vials.bottles.WaterBottleItem;
import net.adinvas.prototype_pain.item.misc.BrownCapItem;
import net.adinvas.prototype_pain.item.reusable.SplintItem;
import net.adinvas.prototype_pain.item.reusable.TourniquetItem;
import net.adinvas.prototype_pain.item.reusable.TweezersItem;
import net.adinvas.prototype_pain.item.usable.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PrototypePain.MOD_ID);

    public static final RegistryObject<Item> OldRag = ITEMS.register("old_rag", OldRagItem::new);
    public static final RegistryObject<Item> RippedDressing = ITEMS.register("ripped_dressing", RippedDressingItem::new);
    public static final RegistryObject<Item> BandAids = ITEMS.register("band_aids", BandAidItem::new);
    public static final RegistryObject<Item> Dressing = ITEMS.register("dressing", DressingItem::new);
    public static final RegistryObject<Item> BruiseKit = ITEMS.register("bruise_kit", BruiseKitItem::new);
    public static final RegistryObject<Item> AlganateDressing = ITEMS.register("alganate_dressing", AlganiteDressingItem::new);
    public static final RegistryObject<Item> MedicalGauze = ITEMS.register("medical_gauze", MedicalGauzeItem::new);
    public static final RegistryObject<Item> PlasticDressing = ITEMS.register("plastic_dressing", PlasticDressingItem::new);
    public static final RegistryObject<Item> SterilizedDressing = ITEMS.register("sterilized_dressing", SterilizedDressingItem::new);

    public static final RegistryObject<Item> BoneWelding = ITEMS.register("bone_welding", BoneWeldingItem::new);
    public static final RegistryObject<Item> LRD = ITEMS.register("lrd", LRDItem::new);
    public static final RegistryObject<Item> MakeshiftLRD = ITEMS.register("makeshift_lrd", MakeshiftLRDItem::new);
    public static final RegistryObject<Item> MedicalSuture = ITEMS.register("medical_suture", MedicalSutureItem::new);
    public static final RegistryObject<Item> Ice_Pack = ITEMS.register("ice_pack", IcePackItem::new);
    public static final RegistryObject<Item> HeatPack = ITEMS.register("heat_pack", HeatPackItem::new);
    public static final RegistryObject<Item> GLOW_FRUIT = ITEMS.register("glow_fruit", GlowFruitItem::new);

    public static final RegistryObject<Item> SmallMedibag = ITEMS.register("small_medibag", SmallMedibagItem::new);
    public static final RegistryObject<Item> MediumMedibag = ITEMS.register("medium_medibag", MediumMedibagItem::new);
    public static final RegistryObject<Item> LargeMedibag = ITEMS.register("large_medibag", LargeMedibagItem::new);

    public static final RegistryObject<Item> Splint = ITEMS.register("splint", SplintItem::new);
    public static final RegistryObject<Item> Tweezers = ITEMS.register("tweezers", TweezersItem::new);
    public static final RegistryObject<Item> Tourniquet = ITEMS.register("tourniquet", TourniquetItem::new);

    public static final RegistryObject<Item> Saline = ITEMS.register("saline", SalineItem::new);
    public static final RegistryObject<Item> Alcohol = ITEMS.register("alcohol", AlcoholItem::new);
    public static final RegistryObject<Item> PainKillers = ITEMS.register("painkillers", PainKillersItem::new);
    public static final RegistryObject<Item> Antibiotics = ITEMS.register("antibiotics", AntibioticsItem::new);
    public static final RegistryObject<Item> BloodClotting = ITEMS.register("blood_clotting", BloodClottingItem::new);
    public static final RegistryObject<Item> BloodThiner = ITEMS.register("blood_thinner", BloodThinnerItem::new);
    public static final RegistryObject<Item> AidGel = ITEMS.register("aid_gel", ReliefGel::new);

    public static final RegistryObject<Item> MedicineVial = ITEMS.register("medicine_vial", MedicalVial::new);

    public static final RegistryObject<Item> OpiumVial = ITEMS.register("opium_vial", OpiumVialItem::new);
    public static final RegistryObject<Item> FentanylVial = ITEMS.register("fentanyl_vial", FentVialItem::new);
    public static final RegistryObject<Item> HeroinVial = ITEMS.register("heroin_vial", HeroinVialItem::new);
    public static final RegistryObject<Item> MorphineVial = ITEMS.register("morphine_vial", MorphineVialItem::new);
    public static final RegistryObject<Item> NaloxoneVial = ITEMS.register("naloxone_vial", NaloxoneVialItem::new);
    public static final RegistryObject<Item> ReactionVial = ITEMS.register("reaction_vial", ReactionVialItem::new);
    public static final RegistryObject<Item> Syringe = ITEMS.register("syringe", SyringeItem::new);

    public static final RegistryObject<Item> BrainGrow =  ITEMS.register("brain_grow", BrainGrowItem::new);
    public static final RegistryObject<Item> Antiseptic =  ITEMS.register("antiseptic", AntisepticItem::new);
    public static final RegistryObject<Item> AntiSerum =  ITEMS.register("antiserum", AntiSerumItem::new);
    public static final RegistryObject<Item> CEFTRIAXONE =  ITEMS.register("ceftriaxone", CeftriaxoneItem::new);
    public static final RegistryObject<Item> ReliefCream = ITEMS.register("relief_cream",ReliefCreamItem::new);
    public static final RegistryObject<Item> BrownCap = ITEMS.register("brown_cap", BrownCapItem::new);
    public static final RegistryObject<Item> AutoPump = ITEMS.register("auto_pump", AutoPumpItem::new);

    public static final RegistryObject<Item> Thermometer = ITEMS.register("thermometer", ThermometerItem::new);
    public static final RegistryObject<Item> LifeStraw = ITEMS.register("life_straw", LifeStrawItem::new);
    public static final RegistryObject<Item> WaterBottle = ITEMS.register("water_bottle", WaterBottleItem::new);

    public static final RegistryObject<Item> ScavPlush = ITEMS.register("plush",()->
            new BlockItem(ModBlocks.SCAV_BLOCK.get(),new Item.Properties()));
}
