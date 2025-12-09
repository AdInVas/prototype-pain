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
        add("add_heatpack",new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.14f*sanityScale).build()
                },       // no extra conditions
                ModItems.HeatPack.get()
        ));
        add("add_auto_pump",new AddFilledToChestsModifier(
                new LootItemCondition[] {
                        LootItemRandomChanceCondition.randomChance(0.01f*sanityScale).build()
                },       // no extra conditions
                ModItems.HeatPack.get()
        ));

    }
}
