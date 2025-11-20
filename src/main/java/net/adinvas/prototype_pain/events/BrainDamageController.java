package net.adinvas.prototype_pain.events;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.adinvas.prototype_pain.network.BrainEventPacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BrainDamageController {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        float brain= player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getBrainHealth).orElse(100f);
        float dislocatedJaw= player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->h.getLimbDislocated(Limb.HEAD)).orElse(0f);
        boolean JawMissing = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::isMouthRemoved).orElse(false);
        if (brain < 30f) { // unconscious
            event.setCanceled(true);
            return;
        }
        String msg = event.getMessage().getString();
        float clarity = Mth.clamp(brain / 100f,0,1);

        msg = distortScaled(msg, clarity);
        if (dislocatedJaw>0){
            msg = DislocatedJaw(msg);
        }else if (JawMissing){
            msg = MissingJaw(msg);
        }
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

    private static String MissingJaw(String input){
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            boolean drop = isSame(c,'b')||
                    isSame(c,'p')||
                    isSame(c,'m')||
                    isSame(c,'f')||
                    isSame(c,'t')||
                    isSame(c,'h')||
                    isSame(c,'v');
            if (isSame(c,'t')){
                sb.append("tgh");
            }
            if (isSame(c,'d')){
                sb.append("dgh");
            }
            if (isSame(c,'n')){
                sb.append("ngh");
            }
            if (isSame(c,'l')){
                sb.append("lh");
            }
            if (isSame(c,'s')){
                sb.append("h");
            }
            if (isSame(c,'z')){
                sb.append("zh");
            }
            if (drop){
                sb.append("_");
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }
    private static String DislocatedJaw(String input){
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            boolean drop = isSame(c,'b')||
                    isSame(c,'p')||
                    isSame(c,'m')||
                    isSame(c,'f')||
                    isSame(c,'v');

            if (isSame(c,'s')){
                sb.append("th");
            }
            if (isSame(c,'z')){
                sb.append("ey");
            }
            if (isSame(c,'t')||isSame(c,'d')){
                sb.append("h");
            }
            if (drop){
                sb.append("_");
            }else{
                sb.append(c);
            }

        }
        return sb.toString();
    }

    public static boolean isSame(char a, char b){
        return Character.toLowerCase(a)==Character.toLowerCase(b);
    }



}
