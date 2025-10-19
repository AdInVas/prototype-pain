package net.adinvas.prototype_pain.datagen;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.item.ModItems;
import net.adinvas.prototype_pain.loot.AddFilledToChestsModifier;
import net.adinvas.prototype_pain.loot.AddRandomFillToChestsModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class ModGlobaLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobaLootModifiersProvider(PackOutput output) {
        super(output, PrototypePain.MOD_ID);
    }

    public float sanityScale = 0.9f;
    @Override
    protected void start() {
        add("add_dr", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.12f*sanityScale).build()
                },       // no extra conditions
                ModItems.Dressing.get()
        ));
        add("add_oclussive_dressing", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.08f*sanityScale).build()
                },       // no extra conditions
                ModItems.PlasticDressing.get()
        ));
        add("add_sdressing", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.05f*sanityScale).build()
                },       // no extra conditions
                ModItems.SterilizedDressing.get()
        ));
        add("add_bandaid", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.225f*sanityScale).build()
                },       // no extra conditions
                ModItems.BandAids.get()
        ));
        add("add_ice", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.14f*sanityScale).build()
                },       // no extra conditions
                ModItems.Ice_Pack.get()
        ));
        add("add_saline", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.15f*sanityScale).build()
                },       // no extra conditions
                ModItems.Saline.get()
        ));
        add("add_painkiller", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.07f*sanityScale).build()
                },       // no extra conditions
                ModItems.PainKillers.get()
        ));
        add("add_antibiotics", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.05f*sanityScale).build()
                },       // no extra conditions
                ModItems.Antibiotics.get()
        ));
        add("add_clotting", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.075f*sanityScale).build()
                },       // no extra conditions
                ModItems.BloodClotting.get()
        ));
        add("add_thinner", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.075f*sanityScale).build()
                },       // no extra conditions
                ModItems.BloodThiner.get()
        ));
        add("add_aidgel", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.07f*sanityScale).build()
                },       // no extra conditions
                ModItems.AidGel.get()
        ));
        add("add_splint", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.05f*sanityScale).build()
                },       // no extra conditions
                ModItems.Splint.get()
        ));
        add("add_tweezers", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.05f*sanityScale).build()
                },       // no extra conditions
                ModItems.Tweezers.get()
        ));
        add("add_tourniquet", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.05f*sanityScale).build()
                },       // no extra conditions
                ModItems.Tourniquet.get()
        ));
        add("add_opium", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.05f*sanityScale).build()
                },       // no extra conditions
                ModItems.OpiumVial.get()
        ));
        add("add_fentanyl", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.025f*sanityScale).build()
                },       // no extra conditions
                ModItems.FentanylVial.get()
        ));
        add("add_heroin", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.04f*sanityScale).build()
                },       // no extra conditions
                ModItems.HeroinVial.get()
        ));
        add("add_morphine", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.05f*sanityScale).build()
                },       // no extra conditions
                ModItems.MorphineVial.get()
        ));
        add("add_naloxone", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.03f*sanityScale).build()
                },       // no extra conditions
                ModItems.NaloxoneVial.get()
        ));
        add("add_braingrow", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.05f*sanityScale).build()
                },       // no extra conditions
                ModItems.BrainGrow.get()
        ));
        add("add_antiseptic", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.1f*sanityScale).build()
                },       // no extra conditions
                ModItems.Antiseptic.get()
        ));
        add("add_antiserum", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.05f*sanityScale).build()
                },       // no extra conditions
                ModItems.Antiseptic.get()
        ));
        add("add_cef", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.025f*sanityScale).build()
                },       // no extra conditions
                ModItems.CEFTRIAXONE.get()
        ));
        add("add_cream", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.08f*sanityScale).build()
                },       // no extra conditions
                ModItems.ReliefCream.get()
        ));
        add("add_old_rag", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.30f*sanityScale).build()
                },       // no extra conditions
                ModItems.OldRag.get()
        ));
        add("add_rippeddressing", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.30f*sanityScale).build()
                },       // no extra conditions
                ModItems.RippedDressing.get()
        ));
        add("add_bruisekit", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.10f*sanityScale).build()
                },       // no extra conditions
                ModItems.BruiseKit.get()
        ));
        add("add_algdressing", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.10f*sanityScale).build()
                },       // no extra conditions
                ModItems.AlganateDressing.get()
        ));
        add("add_medicalgauze", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.08f*sanityScale).build()
                },       // no extra conditions
                ModItems.MedicalGauze.get()
        ));
        add("add_boneweld", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.04f*sanityScale).build()
                },       // no extra conditions
                ModItems.BoneWelding.get()
        ));
        add("add_medsuture", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.08f*sanityScale).build()
                },       // no extra conditions
                ModItems.MedicalSuture.get()
        ));
        add("add_lrd", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.01f*sanityScale).build()
                },       // no extra conditions
                ModItems.LRD.get()
        ));
        add("add_makeshiftlrd", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.02f*sanityScale).build()
                },       // no extra conditions
                ModItems.MakeshiftLRD.get()
        ));
        add("add_alcohol", new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.2f*sanityScale).build()
                },       // no extra conditions
                ModItems.Alcohol.get()
        ));
        add("add_heatpack",new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.14f*sanityScale).build()
                },       // no extra conditions
                ModItems.HeatPack.get()
        ));


        add("random_vial",new AddRandomFillToChestsModifier(
                new LootItemCondition[]{
                    LootItemRandomChanceCondition.randomChance(0.1f*sanityScale).build()
                },
                ModItems.MedicineVial.get()

        ));
        add("random_syringe",new AddRandomFillToChestsModifier(
                new LootItemCondition[]{
                        LootItemRandomChanceCondition.randomChance(0.1f*sanityScale).build()
                },
                ModItems.Syringe.get()
        ));

    }
}
