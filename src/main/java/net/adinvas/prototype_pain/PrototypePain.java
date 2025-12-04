package net.adinvas.prototype_pain;

import com.mojang.logging.LogUtils;
import net.adinvas.prototype_pain.blocks.ModBlocks;
import net.adinvas.prototype_pain.compat.prototype_physics.PhysicsEvents;
import net.adinvas.prototype_pain.config.ClientConfig;
import net.adinvas.prototype_pain.config.ServerConfig;
import net.adinvas.prototype_pain.events.ModEvents;
import net.adinvas.prototype_pain.item.ModCreativeTab;
import net.adinvas.prototype_pain.item.ModItems;
import net.adinvas.prototype_pain.item.special.bags.large.LargeMedibagScreen;
import net.adinvas.prototype_pain.item.special.bags.medium.MediumMedibagScreen;
import net.adinvas.prototype_pain.item.special.bags.small.SmallMedibagScreen;
import net.adinvas.prototype_pain.item.usable.ThermometerItem;
import net.adinvas.prototype_pain.loot.ModLootModifier;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.adinvas.prototype_pain.recipe.ModRecipes;
import net.adinvas.prototype_pain.visual.particles.VomitParticle;
import net.adinvas.prototype_pain.visual.particles.ModParticles;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.function.Function;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PrototypePain.MOD_ID)
public class PrototypePain {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "prototype_pain";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public PrototypePain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ModEvents());

        ModItems.ITEMS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModLootModifier.register(modEventBus);
        ModCreativeTab.CREATIVE_TABS.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ModSounds.register(modEventBus);
        ModParticles.register(modEventBus);
        modEventBus.addListener(ModGamerules::onCommonSetup);
        if (ModList.get().isLoaded("prototype_physics")) {
            MinecraftForge.EVENT_BUS.register(new PhysicsEvents());
            LOGGER.info("PrototypePhysics detected — registered compatibility listeners");
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenus.SMALL_MEDIBAG.get(), SmallMedibagScreen::new);
            MenuScreens.register(ModMenus.MEDIUM_MEDIBAG.get(), MediumMedibagScreen::new);
            MenuScreens.register(ModMenus.LARGE_MEDIBAG.get(), LargeMedibagScreen::new);
            MenuScreens.register(ModMenus.LOOT_PLAYER.get(),LootPlayerScreen::new);
        }

        @SubscribeEvent
        public static void registerKeybindings(RegisterKeyMappingsEvent event){
            Keybinds.register(event);
        }

        @SubscribeEvent
        public static void registerParticleProvider(RegisterParticleProvidersEvent event){
            event.registerSpriteSet(ModParticles.BLOOD_PARTICLE.get(), VomitParticle.Provider::new);
        }

        @SubscribeEvent
        public static void registerTooltips(RegisterClientTooltipComponentFactoriesEvent event){
            event.register(ThermometerItem.ClientThermoTooltip.class, Function.identity());
        }
    }
}
