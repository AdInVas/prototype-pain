package net.adinvas.prototype_pain.fluid_system;

import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.server.level.ServerPlayer;

public interface MedicalEffect {
    default void applyIngested(ServerPlayer player, float ml){}
    default void applyInjected(ServerPlayer player, float ml, Limb limb){}
    default void applyOnSkin(ServerPlayer player, float ml, Limb limb){}
}
