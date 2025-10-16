package net.adinvas.prototype_pain.item;

import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface IBandage {
    void useBandageAction(float scalableAmount, Player target, @Nullable Limb limb);
}
