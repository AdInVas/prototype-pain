package net.adinvas.prototype_pain.datagen;


import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = PrototypePain.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenretors {


    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput output =generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(),new ModWorldGenProvider(output,lookupProvider));
        generator.addProvider(event.includeServer(),new ModGlobaLootModifiersProvider(output));
    }
}
