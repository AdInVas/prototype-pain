package net.adinvas.prototype_pain.events;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.compat.FoodAndDrinkCompat;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.adinvas.prototype_pain.network.SyncTracker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.antlr.v4.codegen.model.Sync;

import java.util.ArrayList;
import java.util.List;
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
                if (player.gameMode.isCreative())return;
                ServerLevel level = player.serverLevel();
                ProfilerFiller profiler = level.getProfiler();

                profiler.push("prototype_pain:player_health_system");
                event.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(playerHealthData -> {
                    playerHealthData.tickUpdate(player);
                    boolean usingArm = player.isUsingItem();
                    if (usingArm){
                        InteractionHand hand = player.getUsedItemHand();
                        playerHealthData.onArmUse(hand,player);
                    }
                });
                profiler.pop();
            }
        }
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
    public void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack stack = event.getItem();
        FoodAndDrinkCompat.FoodEntry data = FoodAndDrinkCompat.get(stack.getItem());
        if (data != null)
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setTemperature(h.getTemperature()+ data.temperature);
                //TODO thirst
                //TODO sickness

            });
    }


    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ProfilerFiller profiler = server.getProfiler();
        profiler.push("prototype_pain:sync_tracker");
        if (event.phase == TickEvent.Phase.END) {
            SyncTracker.tick(server);
            SyncTracker.tickEveryone(server);
            SyncTracker.tickEveryoneReducedBroadcast(server);
        }
        profiler.pop();
    }

    @SubscribeEvent
    public static void onItemUse(LivingEntityUseItemEvent.Stop event){
        if (event.getEntity() instanceof Player player) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                Item item = event.getItem().getItem();
                if (h.getLimbFracture(Limb.HEAD)>0){
                    h.setLimbPain(Limb.HEAD, h.getLimbPain(Limb.HEAD)+3);
                }
                if (h.getLimbDislocated(Limb.CHEST)>0||h.getLimbFracture(Limb.CHEST)>0){
                    h.setLimbPain(Limb.CHEST, h.getLimbPain(Limb.CHEST)+4);
                }
                if (item.isEdible()){
                    if (h.getLimbDislocated(Limb.HEAD)>0){
                        h.setLimbPain(Limb.HEAD, h.getLimbPain(Limb.HEAD)+25);
                    }
                }
            });
        }

    }

    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event){
        if (event.getEntity() instanceof Player player) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                if (h.getLimbFracture(Limb.HEAD)>0){
                    h.setLimbPain(Limb.HEAD, h.getLimbPain(Limb.HEAD)+3);
                }
                if (h.getLimbDislocated(Limb.CHEST)>0||h.getLimbFracture(Limb.CHEST)>0){
                    h.setLimbPain(Limb.CHEST, h.getLimbPain(Limb.CHEST)+4);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event){
        if (event.getEntity() instanceof Player player) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                if (h.getLimbFracture(Limb.HEAD)>0){
                    h.setLimbPain(Limb.HEAD, h.getLimbPain(Limb.HEAD)+10);
                }
                if (h.getLimbDislocated(Limb.CHEST)>0){
                    h.setLimbPain(Limb.CHEST, h.getLimbPain(Limb.HEAD)+10);
                }
                List<Limb> templist= new ArrayList<>();
                templist.add(Limb.LEFT_LEG);
                templist.add(Limb.RIGHT_LEG);
                templist.add(Limb.LEFT_FOOT);
                templist.add(Limb.RIGHT_FOOT);
                for (Limb limb :templist){
                    if (h.getLimbDislocated(limb)>0||h.getLimbFracture(limb)>0){
                        h.setLimbPain(limb, h.getLimbPain(limb)+10);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void onJoin(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof ServerPlayer player){
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.isReducedDirty = true;
            });
            SyncTracker.onJoin((ServerPlayer)player,ServerLifecycleHooks.getCurrentServer());
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


}
