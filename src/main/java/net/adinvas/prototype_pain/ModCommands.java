package net.adinvas.prototype_pain;

import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        // dispatcher = event.getDispatcher()
        event.getDispatcher().register(
                Commands.literal("prototypeheal")
                        .requires(cs -> cs.hasPermission(0)) // permission level (0 = everyone)
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> {
                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                    CommandSourceStack source = ctx.getSource();

                                    target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(PlayerHealthData::resetToDefaults);
                                    return 1; // success result
                                })
                        )
        );
    }

}
