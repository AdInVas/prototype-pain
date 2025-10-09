package net.adinvas.prototype_pain.network;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(PrototypePain.MOD_ID, "prototype_pain_main"), // channel name (unique per mod)
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );


    public static void register() {
        // Register your packets here
        CHANNEL.registerMessage(0,SyncHealthPacket.class,SyncHealthPacket::write,SyncHealthPacket::new,SyncHealthPacket::handle);
        CHANNEL.registerMessage(1, GuiSyncTogglePacket.class, GuiSyncTogglePacket::write, GuiSyncTogglePacket::new, GuiSyncTogglePacket::handle);
        CHANNEL.registerMessage(2, UseMedItemPacket.class, UseMedItemPacket::write, UseMedItemPacket::new, UseMedItemPacket::handle);
        CHANNEL.registerMessage(3, MedicalActionPacket.class, MedicalActionPacket::write, MedicalActionPacket::new, MedicalActionPacket::handle);
        CHANNEL.registerMessage(4, UseNarcoticItemPacket.class,UseNarcoticItemPacket::write,UseNarcoticItemPacket::new,UseNarcoticItemPacket::handle);
        CHANNEL.registerMessage(5, LegUsePacket.class,LegUsePacket::toBytes,LegUsePacket::new,LegUsePacket::handle);
        CHANNEL.registerMessage(6, GiveUpPacket.class,GiveUpPacket::toBytes,GiveUpPacket::new,GiveUpPacket::handle);
        CHANNEL.registerMessage(7, FluidTransferPacket.class,FluidTransferPacket::write,FluidTransferPacket::new,FluidTransferPacket::handle);
        CHANNEL.registerMessage(8, UseSyringePacket.class,UseSyringePacket::write,UseSyringePacket::new,UseSyringePacket::handle);
        CHANNEL.registerMessage(9, SyringeFailPacket.class,SyringeFailPacket::write,SyringeFailPacket::new,SyringeFailPacket::handle);
        CHANNEL.registerMessage(10, ExchangeItemInHandPacket.class,ExchangeItemInHandPacket::write,ExchangeItemInHandPacket::new,ExchangeItemInHandPacket::handle);
    }
}
