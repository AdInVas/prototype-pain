package net.adinvas.prototype_pain.visual;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerLimbHider {
    private static final List<Limb> limbsToSearch = List.of(
            Limb.LEFT_ARM,
            Limb.RIGHT_ARM,
            Limb.LEFT_LEG,
            Limb.RIGHT_LEG,
            Limb.HEAD
    );

    private static final Map<Player, Map<Limb, Boolean>> previousVisibility = new HashMap<>();

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        PlayerRenderer renderer = event.getRenderer();
        var model = renderer.getModel();

        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(data -> {
            Map<Limb, Boolean> prev = new HashMap<>();
            for (Limb limb : limbsToSearch) {
                switch (limb){
                    case RIGHT_LEG -> {
                        boolean isVisible = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->h.isAmputated(limb)).orElse(false);
                        prev.put(limb,model.rightLeg.visible);
                        model.rightLeg.visible = !isVisible;
                        model.rightPants.visible = !isVisible;
                    }
                    case RIGHT_ARM -> {
                        boolean isVisible = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->h.isAmputated(limb)).orElse(false);
                        prev.put(limb,model.rightArm.visible);
                        model.rightArm.visible = !isVisible;
                        model.rightSleeve.visible = !isVisible;
                    }
                    case LEFT_ARM -> {
                        boolean isVisible = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->h.isAmputated(limb)).orElse(false);
                        prev.put(limb,model.leftArm.visible);
                        model.leftArm.visible = !isVisible;
                        model.leftSleeve.visible = !isVisible;
                    }
                    case LEFT_LEG -> {
                        boolean isVisible = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->h.isAmputated(limb)).orElse(false);
                        prev.put(limb,model.leftLeg.visible);
                        model.leftLeg.visible = !isVisible;
                        model.leftPants.visible = !isVisible;
                    }
                    case HEAD ->{
                        boolean isVisible = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->h.isAmputated(limb)).orElse(false);
                        prev.put(limb,model.head.visible);
                        model.head.visible = !isVisible;
                        model.hat.visible = !isVisible;
                    }
                }
            }
            previousVisibility.put(player, prev);
        });
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        PlayerRenderer renderer = event.getRenderer();
        var model = renderer.getModel();

        Map<Limb, Boolean> prev = previousVisibility.remove(player);
        if (prev != null) {
            for (Map.Entry<Limb, Boolean> entry : prev.entrySet()) {
                switch (entry.getKey()){
                    case RIGHT_LEG -> {
                        model.rightLeg.visible = entry.getValue();
                        model.rightPants.visible = entry.getValue();
                    }
                    case RIGHT_ARM -> {
                        model.rightArm.visible = entry.getValue();
                        model.rightSleeve.visible = entry.getValue();
                    }
                    case LEFT_ARM -> {
                        model.leftArm.visible = entry.getValue();
                        model.leftSleeve.visible = entry.getValue();
                    }
                    case LEFT_LEG -> {
                        model.leftLeg.visible = entry.getValue();
                        model.leftPants.visible = entry.getValue();
                    }
                    case HEAD ->{
                        model.head.visible = entry.getValue();
                        model.hat.visible = entry.getValue();
                    }
                }
            }
        }
    }
}
