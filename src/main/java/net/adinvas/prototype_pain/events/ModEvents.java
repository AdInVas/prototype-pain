package net.adinvas.prototype_pain.events;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.client.OverlayController;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.adinvas.prototype_pain.network.SyncTracker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = PrototypePain.MOD_ID)
public class ModEvents {


    @SubscribeEvent
    public void onAttachCap(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).isPresent()){
                event.addCapability(new ResourceLocation(PrototypePain.MOD_ID,"properties"),new PlayerHealthProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event){
        if (event.isWasDeath()){
            event.getOriginal().getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(oldStore ->{
                event.getOriginal().getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(newStore ->{
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onRegisterCap(RegisterCapabilitiesEvent event){
        event.register(PlayerHealthData.class);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.side == LogicalSide.SERVER){
            if (event.phase!= TickEvent.Phase.START)return;
            if (event.player instanceof ServerPlayer player) {
                event.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(playerHealthData -> {
                    playerHealthData.tickUpdate(player);
                });
            }
        }
        Player player = event.player;
        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            boolean isUnc = h.getContiousness()<=4;
            if (isUnc){
                player.zza = 0;
                player.xxa = 0;
                player.yRotO = 0; // Stop looking around
                player.xRotO = 0;
                player.setPose(Pose.SLEEPING);
            }
        });

        /*
            if (event.player instanceof Player) {
                if (Keybinds.OPEN_PAIN_GUI.isDown()) {
                    Keybinds.OPEN_PAIN_GUI.consumeClick();

                    Player target = getLookedAtPlayer(event.player, 2);
                    boolean self = target==null||event.player.isShiftKeyDown();
                    if (event.side == LogicalSide.CLIENT) {
                        if (self){
                            Minecraft.getInstance().setScreen(new HealthScreen(event.player));
                        }else {
                            Minecraft.getInstance().setScreen(new HealthScreen(target));
                        }
                    }
                }
            }
            //Doesnt work IG.
         */

    }

    @SubscribeEvent
    public static void onPlayerDamage(LivingDamageEvent event){
        if (!(event.getEntity() instanceof ServerPlayer player)){

        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            SyncTracker.tick(ServerLifecycleHooks.getCurrentServer());
            SyncTracker.tickEveryone(ServerLifecycleHooks.getCurrentServer());
        }
    }



    public static Player getLookedAtPlayer(Player viewer, double maxDistance) {
        Vec3 eyePos = viewer.getEyePosition(1.0F);
        Vec3 lookVec = viewer.getLookAngle();
        Vec3 reachVec = eyePos.add(lookVec.scale(maxDistance));

        AABB searchBox = viewer.getBoundingBox()
                .expandTowards(lookVec.scale(maxDistance))
                .inflate(1.0D); // widen a bit so it's easier to hit

        // find closest player along the ray
        Player nearest = null;
        double nearestDist = maxDistance;

        for (Player target : viewer.level().getEntitiesOfClass(Player.class, searchBox)) {
            if (target == viewer) continue; // skip self

            AABB hitBox = target.getBoundingBox().inflate(0.3); // tolerance
            Optional<Vec3> hit = hitBox.clip(eyePos, reachVec);

            if (hit.isPresent()) {
                double dist = eyePos.distanceTo(hit.get());
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = target;
                }
            }
        }

        return nearest;
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getEntity();
        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            boolean isUnc = h.getContiousness()<=4;
            if (isUnc){
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            boolean isUnc = h.getContiousness()<=4;
            if (isUnc){
                event.setCanceled(true);
            }
        });
    }

}
