package net.adinvas.prototype_pain.hitbox;


import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class HitboxEvents {
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event){
        if (!(event.getEntity() instanceof Player player))return;
        Entity attacker = event.getSource().getEntity();
        HitResult result = HitDection.detecthit(attacker,player,event.getSource(),event.getAmount());
        if (result!=null) {

            DamageSource src = event.getSource();
            if (src.is(DamageTypes.FALL)) {

            } else if (src.is(DamageTypes.GENERIC)) {

            }


        }
        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(health ->{
            health.HandleRandomDamage(event.getAmount());
        });
        if (event.getSource().is(DamageTypes.GENERIC_KILL)){
            return;
        }
        event.setCanceled(true);
    }
}
