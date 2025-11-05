package net.adinvas.prototype_pain.events;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = PrototypePain.MOD_ID)
public class ExplosionEvents {

    private static final double HEARING_DISTANCE = 20.0;
    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        Level level = event.getLevel();
        Vec3 explosionPos = event.getExplosion().getPosition();
        // 1. This logic must run on the server
        if (level.isClientSide()) {
            return;
        }

        // 2. Create a bounding box 32 blocks in every direction from the explosion
        // This is a fast, efficient first-pass check.
        AABB checkBounds = new AABB(new BlockPos((int) explosionPos.x, (int) explosionPos.y, (int) explosionPos.z)).inflate(HEARING_DISTANCE);

        // 3. Get all players within that box
        for (Player player : level.getEntitiesOfClass(Player.class, checkBounds)) {

            // 4. Check the precise spherical distance
            double distance = player.position().distanceTo(explosionPos);
            if (distance > HEARING_DISTANCE) {
                continue; // Player was in the corner of the AABB but > 32 blocks away
            }
            float distanceScale = (float) Math.pow(1-distance/HEARING_DISTANCE,2f);
            if (!hasLineOfSight(level, player, explosionPos)){
                distanceScale/=2;
            }
            float finalDistanceScale = distanceScale+0.1f;
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                h.setContiousness(h.getContiousness()-(100* finalDistanceScale));
                h.setHearingLoss((float) (h.getHearingLoss()+Math.max(0.05, finalDistanceScale /2f)));
                h.setFlashHearingLoss(h.getFlashHearingLoss()+Math.min(0.25f, finalDistanceScale *4));
            });
            // 5. Check for a clear Line of Sight (LOS)
                // This player is within 32 blocks AND can see the explosion!
                //
                // TRIGGER YOUR HEARING LOSS LOGIC HERE
                // e.g., send a custom packet to this 'player' (who is a ServerPlayer)
                //
        }
    }

    /**
     * Checks if a player has a direct line of sight to a target position.
     * @param level The world
     * @param player The player
     * @param targetPos The position of the explosion
     * @return true if there is a clear line of sight, false otherwise
     */
    private static boolean hasLineOfSight(Level level, Player player, Vec3 targetPos) {
        // Start the raycast from the player's eyes
        Vec3 eyePos = player.getEyePosition();

        ClipContext clipContext = new ClipContext(
                eyePos,                 // Start of the ray
                targetPos,              // End of the ray
                ClipContext.Block.COLLIDER, // Checks against blocks with collision (e.g., stone, wood)
                ClipContext.Fluid.NONE,   // Ignores fluids
                player                  // The entity to ignore (the player themselves)
        );

        BlockHitResult hitResult = level.clip(clipContext);

        // If the raycast 'missed', it means it didn't hit a block.
        // Therefore, the player has a clear line of sight.
        return hitResult.getType() == HitResult.Type.MISS;
    }



}
