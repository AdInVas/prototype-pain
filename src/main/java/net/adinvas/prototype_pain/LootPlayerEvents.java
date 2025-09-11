package net.adinvas.prototype_pain;

import net.adinvas.prototype_pain.config.ServerConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

@Mod.EventBusSubscriber
public class LootPlayerEvents {

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteract event){
        if (!(event.getTarget() instanceof Player target)) return;

        if (!ServerConfig.TOGGLE_UNCONTIOUS_INVENTORY.get())return;
        Player actor = event.getEntity();
        if (actor.isShiftKeyDown()) return;

        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
            if (h.getContiousness() <= 4) {
                if (!actor.level().isClientSide) {
                    NetworkHooks.openScreen(
                            (ServerPlayer) actor,
                            new SimpleMenuProvider(
                                    (id, inv, p) -> new LootPlayerMenu(id, inv, target),
                                    Component.literal("Looting " + target.getName().getString())
                            ),
                            buf -> buf.writeVarInt(target.getId())
                    );
                }
                event.setCanceled(true);
            }
        });
    }
}
