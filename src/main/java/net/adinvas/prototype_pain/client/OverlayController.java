package net.adinvas.prototype_pain.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
@Mod.EventBusSubscriber(modid = PrototypePain.MOD_ID, value = Dist.CLIENT)
public class OverlayController {
    private static final List<IOverlay> overlays = new ArrayList<>();

    public static void registerOverlay(IOverlay overlay) {
        overlays.add(overlay);
    }

    public static <T extends IOverlay> T getOverlay(Class<T> clazz) {
        for (IOverlay overlay : overlays) {
            if (clazz.isInstance(overlay)) {
                return clazz.cast(overlay);
            }
        }
        return null;
    }

   @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {

        // Example: only render our overlays after the HOTBAR
        GuiGraphics ms = event.getGuiGraphics();
        float pt = event.getPartialTick();
        // Render all registered overlays that should draw
        for (IOverlay overlay : overlays) {
            if (overlay.shouldRender()) {
                overlay.render(ms, pt);
            }
        }
    }

}
