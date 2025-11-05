package net.adinvas.prototype_pain.mixin;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow public abstract void setForcedPose(@Nullable Pose pose);

    @Inject(method = "updatePlayerPose",at = @At("HEAD"), cancellable = true)
    private void pp$forceLaydownPose(CallbackInfo ci) {
        Player self = (Player)(Object)this;
        self.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
            if (h.getContiousness() <= 10) {
                self.setPose(Pose.SWIMMING);
                ci.cancel(); // prevent vanilla from picking another pose
            }
            if (h.isAmputated(Limb.RIGHT_LEG)&&h.isAmputated(Limb.LEFT_LEG)&&!self.isPassenger()){
                self.setPose(Pose.SWIMMING);
                ci.cancel();
            }
        });
    }


}
