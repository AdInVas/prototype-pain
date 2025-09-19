package net.adinvas.prototype_pain;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,PrototypePain.MOD_ID);

    public static final RegistryObject<SoundEvent> HEART_BEAT = register("heartbeat");
    public static final RegistryObject<SoundEvent> BANDAGE_USE = register("bandage");
    public static final RegistryObject<SoundEvent> SYRINGE_USE = register("syringe");
    public static final RegistryObject<SoundEvent> RINGING = register("ring");
    public static final RegistryObject<SoundEvent> PILL = register("pill");



    private static RegistryObject<SoundEvent> register(String name) {
        ResourceLocation id = new ResourceLocation(PrototypePain.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }
}
