package net.adinvas.prototype_pain.datagen;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.item.ModItems;
import net.adinvas.prototype_pain.tags.ModItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, PrototypePain.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        this.tag(ModItemTags.DRESSINGS)
                .add(ModItems.Dressing.get(),
                    ModItems.PlasticDressing.get(),
                    ModItems.SterilizedDressing.get(),
                    ModItems.MedicalGauze.get(),
                    ModItems.AlganateDressing.get());

        this.tag(ModItemTags.VIAL_ITEMS)
                .add(ModItems.MedicineVial.get(),
                        ModItems.OpiumVial.get(),
                        ModItems.MorphineVial.get(),
                        ModItems.FentanylVial.get(),
                        ModItems.Antiseptic.get(),
                        ModItems.PainKillers.get(),
                        ModItems.Antibiotics.get(),
                        ModItems.BloodThiner.get(),
                        ModItems.BloodClotting.get(),
                        ModItems.BrainGrow.get(),
                        ModItems.ReliefCream.get(),
                        ModItems.ReactionVial.get(),
                        ModItems.CEFTRIAXONE.get(),
                        ModItems.AntiSerum.get(),
                        ModItems.NaloxoneVial.get(),
                        ModItems.Alcohol.get());

        this.tag(ModItemTags.ALCOHOL_CREATABLE)
                .add(
                        Items.APPLE,
                        Items.POTATO,
                        Items.POISONOUS_POTATO,
                        Items.BROWN_MUSHROOM,
                        Items.RED_MUSHROOM,
                        Items.GLOW_BERRIES,
                        Items.SWEET_BERRIES
                );

        this.tag(ModItemTags.CAUTERIZE)
                .add(
                        Items.TORCH,
                        Items.SOUL_TORCH,
                        Items.MAGMA_BLOCK,
                        Items.LAVA_BUCKET
                );

    }
}
