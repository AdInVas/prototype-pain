package net.adinvas.prototype_pain.mixin;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "updatePlayerPose",at = @At("HEAD"), cancellable = true)
    private void pp$forceLaydownPose(CallbackInfo ci) {
        Player self = (Player)(Object)this;
        self.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
            if (h.getContiousness() <= 4) {
                self.setPose(Pose.SWIMMING);
                ci.cancel(); // prevent vanilla from picking another pose
            }
        });
    }
}
