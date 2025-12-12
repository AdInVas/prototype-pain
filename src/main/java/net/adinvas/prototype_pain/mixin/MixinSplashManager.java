package net.adinvas.prototype_pain.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@Mixin(SplashManager.class)
public class MixinSplashManager {

    @Shadow
    private List<String> splashes;

    /**
     * Inject AFTER vanilla loads splashes into the list.
     */
    @Inject(
            method = "apply",
            at = @At("TAIL")
    )
    private void prototype$addCustomSplashes(List<String> vanillaList, ResourceManager rm, ProfilerFiller profiler, CallbackInfo ci) {
        // Add your text file of splashes
        List<String> extraSplashes = loadCustomSplashes();

        if (!extraSplashes.isEmpty()) {
            splashes.addAll(extraSplashes);
        }
    }

    /**
     * Load your mod’s splashes from: resources/texts/prototype_splashes.txt
     */
    private List<String> loadCustomSplashes() {
        try {
            ResourceLocation loc = new ResourceLocation("prototype_pain", "texts/splashes.txt");
            BufferedReader br = Minecraft.getInstance().getResourceManager().openAsReader(loc);

            return br.lines()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

        } catch (IOException e) {
            return List.of();
        }
    }
}
