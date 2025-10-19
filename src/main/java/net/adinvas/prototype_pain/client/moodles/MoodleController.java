package net.adinvas.prototype_pain.client.moodles;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = PrototypePain.MOD_ID, value = Dist.CLIENT)
public class MoodleController {
    private static final List<AbstractMoodleVisual> Moodles = new ArrayList<>();
    private static final int MOODLE_SIZE = 16;
    private static final int PADDING = 4;

    public static void registerMoodle(AbstractMoodleVisual overlay) {
        Moodles.add(overlay);
    }

    private static final OverflowMoodle overflowMoodle = new OverflowMoodle();

    static {
        registerMoodle(new BleedMoodle());
        registerMoodle(new BleedInternalMoodle());
        registerMoodle(new LowBloodMoodle());
        registerMoodle(new HighBloodMoodle());
        registerMoodle(new PainMoodle());
        registerMoodle(new OxygenMoodle());
        registerMoodle(new LungFaliureMoodle());
        registerMoodle(new NotBreathMoodle());
        registerMoodle(new OpiateMoodle());
        registerMoodle(new InfectionMoodle());
        registerMoodle(new FractureMoodle());
        registerMoodle(new DislocationMoodle());
        registerMoodle(new ConsiousnessMoodle());
        registerMoodle(new ShockMoodle());
        registerMoodle(new TemperatureMoodle());
        registerMoodle(new WiwdrawalMoodle());
        registerMoodle(new DirtynessMoodle());
        registerMoodle(new BrainHealthMoodle());
        registerMoodle(new HemothoraxMoodle());
    }

    /** Collects all visible moodles for given player */
    public static List<AbstractMoodleVisual> getVisibleMoodles(Player player) {
        List<AbstractMoodleVisual> visible = new ArrayList<>();
        for (AbstractMoodleVisual m : Moodles) {
            MoodleStatus status = m.calculateStatus(player);
            m.setMoodleStatus(status);
            if (m.shouldRender()) visible.add(m);
        }
        return visible;
    }

    /** Render moodles as HUD overlay (left-bottom, respecting hotbar) */
    public static void renderOnHud(GuiGraphics gfx, float partialTick, Player player, int screenWidth, int screenHeight) {
        int hotbarLeft = (screenWidth / 2) - 91;
        int y = screenHeight - MOODLE_SIZE - 4;

        List<AbstractMoodleVisual> visible = getVisibleMoodles(player);
        int x = 4;

        for (int i = 0; i < visible.size(); i++) {
            if (x + MOODLE_SIZE + 16 > hotbarLeft) {
                overflowMoodle.setLeftover(visible.size() - i);
                overflowMoodle.render(player, gfx, partialTick, x, y);
                break;
            }
            visible.get(i).render(player, gfx, partialTick, x, y);
            x += MOODLE_SIZE + PADDING;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        ProfilerFiller profiler = mc.getProfiler();

        profiler.push("prototype_pain:moodles");
        if (mc.player == null) return;
        renderOnHud(event.getGuiGraphics(), event.getPartialTick(), mc.player,
                mc.getWindow().getGuiScaledWidth(),
                mc.getWindow().getGuiScaledHeight());
        profiler.pop();
    }
}
