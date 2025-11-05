package net.adinvas.prototype_pain;

import net.minecraft.world.level.GameRules;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModGamerules {
    public static GameRules.Key<GameRules.BooleanValue> INVENTORY_STEAL;
    public static GameRules.Key<GameRules.IntegerValue> LAST_STAND_CHANCE;

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            INVENTORY_STEAL = GameRules.register(
                    "doInventoryStealing", // gamerule name
                    GameRules.Category.PLAYER,
                    GameRules.BooleanValue.create(false) // default value
            );
            LAST_STAND_CHANCE = GameRules.register(
                    "lastStandChancePercentage",
                    GameRules.Category.PLAYER,
                    GameRules.IntegerValue.create(10)
            );
        });
    }
}
