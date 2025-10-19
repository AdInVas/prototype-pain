package net.adinvas.prototype_pain.client.overlays;


import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.client.overlays.exp.IShaderOverlay;
import net.adinvas.prototype_pain.client.overlays.exp.PainShaderOverlay;
import net.adinvas.prototype_pain.client.overlays.ovr.ContiousnessOverlay;
import net.adinvas.prototype_pain.client.overlays.ovr.IOverlay;
import net.adinvas.prototype_pain.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
@Mod.EventBusSubscriber(modid = PrototypePain.MOD_ID, value = Dist.CLIENT)
public class OverlayController {
    private static final List<IOverlay> overlays = new ArrayList<>();
    private static final List<IShaderOverlay> shaderOverlays = new ArrayList<>();

    public static void registerOverlay(IOverlay overlay) {
        overlays.add(overlay);
    }
    public static void registerOverlay(IShaderOverlay overlay) {
        shaderOverlays.add(overlay);
    }

    public static <T extends IOverlay> T getOverlay(Class<T> clazz) {
        for (IOverlay overlay : overlays) {
            if (clazz.isInstance(overlay)) {
                return clazz.cast(overlay);
            }
        }
        return null;
    }

    public static boolean isExperiment(){
        return ClientConfig.EXPERIMENTAL_VISUALS.get();
    }

    static {
        registerOverlay(new PainShaderOverlay());
    }

   @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        GuiGraphics ms = event.getGuiGraphics();
       Minecraft mc = Minecraft.getInstance();
       ProfilerFiller profiler = mc.getProfiler();

       profiler.push("prototype_pain:overlay");
        float pt = event.getPartialTick();
        // Render all registered overlays that should draw
        for (IOverlay overlay : overlays) {
            if (overlay.shouldRender()) {
                if (isExperiment() && !(overlay instanceof ContiousnessOverlay)) {
                    continue;
                }
                overlay.render(ms, pt);
            }
        }
       profiler.pop();
    }

    @SubscribeEvent
    public static void onRenderExperimental(RenderLevelStageEvent event){
        if (!isExperiment())return;
        for (IShaderOverlay shaderOverlay:shaderOverlays){
            shaderOverlay.render(event);
        }
    }

}
