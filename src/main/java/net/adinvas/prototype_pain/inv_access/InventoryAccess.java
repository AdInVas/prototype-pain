package net.adinvas.prototype_pain.inv_access;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.config.ServerConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PrototypePain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InventoryAccess {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Player target)) return;

        if (!ServerConfig.TOGGLE_UNCONTIOUS_INVENTORY.get())return;
        Player actor = event.getEntity();
        if (!actor.isShiftKeyDown()) return; // only when sneaking

        target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
            if (h.getContiousness() <= 4) {
                // open target's inventory for the actor
                event.setCanceled(true);
            }
        });
    }
}