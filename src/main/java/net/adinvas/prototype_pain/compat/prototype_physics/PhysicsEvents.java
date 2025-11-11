package net.adinvas.prototype_pain.compat.prototype_physics;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_physics.RagdollPart;
import net.adinvas.prototype_physics.events.PlayerPartHitEvent;
import net.adinvas.prototype_physics.events.RagdollClickEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class PhysicsEvents {

    @SubscribeEvent
    public void onRagdollHit(PlayerPartHitEvent event){
        if (!PhysicsUtil.isPhysicsActivated(event.getPlayer()))return;
        RagdollPart part = event.getPartName();
        ServerPlayer player = event.getPlayer();
        float vel = event.getImpactForce();
        if (vel>2) {
            Limb limb = switch (part){
                case HEAD -> {
                    yield Limb.HEAD;
                }
                case LEFT_ARM -> {
                    yield Math.random()>0.5f?Limb.LEFT_ARM:Limb.LEFT_HAND;
                }
                case LEFT_LEG -> {
                    yield Math.random()>0.5f?Limb.LEFT_LEG:Limb.LEFT_FOOT;
                }
                case RIGHT_LEG -> {
                    yield Math.random()>0.5f?Limb.RIGHT_FOOT:Limb.RIGHT_LEG;
                }
                case RIGHT_ARM -> {
                    yield Math.random()>0.5f?Limb.RIGHT_HAND:Limb.RIGHT_ARM;
                }
                default -> {
                    yield Limb.CHEST;
                }
            };

            float damage = vel*1.5f;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.handleBluntDamage(damage,player,limb);
            });
        }
    }

}
