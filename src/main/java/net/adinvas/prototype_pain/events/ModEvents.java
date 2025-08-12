package net.adinvas.prototype_pain.events;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

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
            if (event.player instanceof ServerPlayer player) {
                event.player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(playerHealthData -> {
                    playerHealthData.tickUpdate(player);
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDamage(LivingDamageEvent event){
        if (!(event.getEntity() instanceof ServerPlayer player)){

        }
    }
}
