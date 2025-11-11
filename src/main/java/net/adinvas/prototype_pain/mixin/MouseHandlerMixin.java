package net.adinvas.prototype_pain.mixin;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.compat.prototype_physics.PhysicsUtil;
import net.adinvas.prototype_pain.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void disableMouseTurn(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            if (!(PhysicsUtil.isPhysicsLoaded()&& ServerConfig.PHYS_INTEGRATION.get())) {
                mc.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                    if (h.getContiousness() <= 10) {
                        ci.cancel();
                    }
                });
            }
        }
    }
}
