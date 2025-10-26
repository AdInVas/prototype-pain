package net.adinvas.prototype_pain;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {




    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("prototypepain")
                        .requires(source -> source.hasPermission(0))

                        .then(Commands.literal("heal")
                                .requires(source ->source.hasPermission(2))
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> {
                                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "targets");

                                            for (ServerPlayer player : targets) {
                                                player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(PlayerHealthData::resetToDefaults);
                                            }

                                            ctx.getSource().sendSuccess(() ->
                                                    Component.literal("Healed " + targets.size() + " player(s)."), true);

                                            return targets.size();
                                        })
                                )
                        )

                        .then(Commands.literal("checklimb")
                                .requires(source ->source.hasPermission(2))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("limb", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {
                                                    for (Limb e : Limb.values()) {
                                                        builder.suggest(e.name().toLowerCase()); // lowercase is more user-friendly
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(ctx -> {
                                                    String raw = StringArgumentType.getString(ctx, "limb");
                                                    Limb limb = Limb.valueOf(raw.toUpperCase());

                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");

                                                    target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                                                        ctx.getSource().sendSuccess(
                                                                () -> h.getLimbDataText(limb),
                                                                false
                                                        );
                                                    });

                                                    return 1;
                                                })
                                        )
                                )
                        )

                        .then(Commands.literal("checkbody")
                                .requires(source ->source.hasPermission(2))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(ctx->{
                                            ServerPlayer target = EntityArgument.getPlayer(ctx,"target");
                                            Optional<String> text = target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::baseToString);
                                            ctx.getSource().sendSuccess(()->
                                                    Component.literal(String.valueOf(text)),false);
                                            return 1;
                                        })
                                )
                        )

                        .then(Commands.literal("setlimb")
                                .requires(source ->source.hasPermission(2))
                                .then(Commands.argument("target",EntityArgument.player())
                                        .then(Commands.argument("limb",StringArgumentType.word())
                                                .suggests((ctx, builder) -> {
                                                    for (Limb e : Limb.values()) {
                                                        builder.suggest(e.name().toLowerCase()); // lowercase is more user-friendly
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .then(Commands.argument("field",StringArgumentType.word())
                                                        .suggests((ctx,builder)->{
                                                            builder.suggest("skinhealth");
                                                            builder.suggest("musclehealth");
                                                            builder.suggest("pain");
                                                            builder.suggest("infection");
                                                            builder.suggest("fracturetimer");
                                                            builder.suggest("dislocatedtimer");
                                                            builder.suggest("bleedrate");
                                                            builder.suggest("desinfectiontimer");
                                                            return builder.buildFuture();
                                                        })
                                                        .then(Commands.argument("value", FloatArgumentType.floatArg())
                                                                .executes(ctx->{
                                                                    String raw = StringArgumentType.getString(ctx,"limb");
                                                                    Limb limb = Limb.valueOf(raw.toUpperCase());
                                                                    raw = StringArgumentType.getString(ctx,"field");
                                                                    float value = FloatArgumentType.getFloat(ctx,"value");
                                                                    ServerPlayer target = EntityArgument.getPlayer(ctx,"target");

                                                                    String finalRaw = raw;
                                                                    target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                                                                        switch (finalRaw){
                                                                            case "skinhealth" ->{
                                                                                h.setLimbSkinHealth(limb,value);
                                                                            }
                                                                            case "musclehealth" ->{
                                                                                h.setLimbMuscleHealth(limb,value);
                                                                            }
                                                                            case "pain" ->{
                                                                                h.setLimbPain(limb,value);
                                                                            }
                                                                            case "infection" ->{
                                                                                h.setLimbInfection(limb,value);
                                                                            }
                                                                            case "fracturetimer" ->{
                                                                                h.setLimbFracture(limb,value);
                                                                            }
                                                                            case "dislocatedtimer" ->{
                                                                                h.setLimbDislocation(limb,value);
                                                                            }
                                                                            case "desinfectiontimer" ->{
                                                                                h.setLimbDesinfected(limb,value);
                                                                            }
                                                                            case "bleedrate" ->{
                                                                                h.setLimbBleedRate(limb,value);
                                                                            }
                                                                            default -> {
                                                                                ctx.getSource().sendFailure(Component.literal("Unknown field: " + finalRaw));
                                                                            }
                                                                        }

                                                                    });
                                                                    ctx.getSource().sendSuccess(() ->
                                                                                    Component.literal("Applied value " + value + " to " + limb +" | "+finalRaw+ " for " + target.getName().getString()),
                                                                            false);
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )

                        .then(Commands.literal("setbody")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("field", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {
                                                    // suggest all available fields
                                                    builder.suggest("blood");
                                                    builder.suggest("contiousness");
                                                    builder.suggest("contiousnessCap");
                                                    builder.suggest("hemothorax");
                                                    builder.suggest("internalBleeding");
                                                    builder.suggest("Oxygen");
                                                    builder.suggest("OxygenCap");
                                                    builder.suggest("Opioids");
                                                    builder.suggest("bloodViscosity");
                                                    builder.suggest("brainhealth");
                                                    builder.suggest("drug_addiction");
                                                    builder.suggest("dirtyness");
                                                    builder.suggest("painshock");
                                                    builder.suggest("temperature");
                                                    return builder.buildFuture();
                                                })
                                                .then(Commands.argument("value", FloatArgumentType.floatArg())
                                                        .executes(ctx -> {
                                                            String field = StringArgumentType.getString(ctx, "field").toLowerCase();
                                                            float value = FloatArgumentType.getFloat(ctx, "value");
                                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");

                                                            // Access the player's capability or component that stores these stats
                                                            target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                                                                switch (field) {
                                                                    case "blood" -> {
                                                                        h.setBloodVolume(value);
                                                                    }
                                                                    case "contiousness" -> {
                                                                        h.setContiousness(value);
                                                                    }
                                                                    case "contiousnesscap" -> {
                                                                        h.setContiousnessCap(value);
                                                                    }
                                                                    case "hemothorax" -> {
                                                                        h.setHemothorax(value);
                                                                    }
                                                                    case "internalbleeding" -> {
                                                                        h.setInternalBleeding(value);
                                                                    }
                                                                    case "oxygen" -> {
                                                                        h.setOxygen(value);
                                                                    }
                                                                    case "oxygencap" -> {
                                                                        h.setOxygenCap(value);
                                                                    }
                                                                    case "opioids" -> {
                                                                        h.setPendingOpioids(value);
                                                                    }
                                                                    case "bloodviscosity" -> {
                                                                        h.setBloodViscosity(value);
                                                                    }
                                                                    case "brainhealth" -> {
                                                                        h.setBrainHealth(value);
                                                                    }
                                                                    case "drug_addiction" -> {
                                                                        h.setDrug_addition(value);
                                                                    }
                                                                    case "dirtyness" -> {
                                                                        h.setDirtyness(value);
                                                                    }
                                                                    case "painshock" -> {
                                                                        h.setShock(value);
                                                                    }
                                                                    case "temperature" -> {
                                                                        h.setTemperature(value);
                                                                    }
                                                                    default -> {
                                                                        ctx.getSource().sendFailure(Component.literal("Unknown field: " + field));
                                                                    }
                                                                }
                                                            });

                                                            ctx.getSource().sendSuccess(() ->
                                                                            Component.literal("Applied value " + value + " to " + field + " for " + target.getName().getString()),
                                                                    false);
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )

                        .then(Commands.literal("amputate")
                                .requires(source ->source.hasPermission(2))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("limb", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {
                                                    for (Limb e : Limb.values()) {
                                                        builder.suggest(e.name().toLowerCase()); // lowercase is more user-friendly
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(ctx -> {
                                                    String raw = StringArgumentType.getString(ctx, "limb");
                                                    Limb limb = Limb.valueOf(raw.toUpperCase());

                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");

                                                    target.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                                                        h.dismember(limb);
                                                        ctx.getSource().sendSuccess(()->Component.literal("amputated "+raw),true);
                                                    });

                                                    return 1;
                                                })
                                        )
                                )
                        )



        );

    }

}
