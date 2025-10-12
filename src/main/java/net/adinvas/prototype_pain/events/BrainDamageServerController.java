package net.adinvas.prototype_pain.events;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BrainDamageServerController {

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        float brain= player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getBrainHealth).orElse(100f);

        if (brain < 30f) { // unconscious
            event.setCanceled(true);
            return;
        }
        String msg = event.getMessage().getString();
        float clarity = Mth.clamp(brain / 100f,0,1);

        msg = distortScaled(msg, clarity);
        event.setMessage(Component.literal(msg));
    }

    private static String distortScaled(String input, float clarity) {
        Random r = new Random();

        // The lower the clarity, the higher the error chance
        float dropChance = (1f - clarity) * 0.35f;      // up to 25% dropped letters
        float dupChance  = (1f - clarity) * 0.25f;      // up to 15% doubles
        float caseChance = (1f - clarity) * 0.45f;       // up to 10% random case
        float gibChance  = (1f - clarity) * 0.3f;       // up to 20% gibberish

        String[] gib = {"rrh", "h", "mn", "zz", "thh", "nn"};

        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (r.nextFloat() < dropChance) continue;

            if (r.nextFloat() < gibChance)
                sb.append(gib[r.nextInt(gib.length)]);

            if (r.nextFloat() < dupChance)
                sb.append(c);

            if (r.nextFloat() < caseChance && Character.isLetter(c))
                c = r.nextBoolean() ? Character.toUpperCase(c) : Character.toLowerCase(c);

            sb.append(c);
        }

        return sb.toString();
    }


}
