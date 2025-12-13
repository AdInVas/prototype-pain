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
        int id = 0;
        CHANNEL.registerMessage(id++,SyncHealthPacket.class,SyncHealthPacket::write,SyncHealthPacket::new,SyncHealthPacket::handle);
        CHANNEL.registerMessage(id++, GuiSyncTogglePacket.class, GuiSyncTogglePacket::write, GuiSyncTogglePacket::new, GuiSyncTogglePacket::handle);
        CHANNEL.registerMessage(id++, UseMedItemPacket.class, UseMedItemPacket::write, UseMedItemPacket::new, UseMedItemPacket::handle);
        CHANNEL.registerMessage(id++, MedicalActionPacket.class, MedicalActionPacket::write, MedicalActionPacket::new, MedicalActionPacket::handle);
        CHANNEL.registerMessage(id++, LegUsePacket.class,LegUsePacket::toBytes,LegUsePacket::new,LegUsePacket::handle);
        CHANNEL.registerMessage(id++, GiveUpPacket.class,GiveUpPacket::toBytes,GiveUpPacket::new,GiveUpPacket::handle);
        CHANNEL.registerMessage(id++, FluidTransferPacket.class,FluidTransferPacket::write,FluidTransferPacket::new,FluidTransferPacket::handle);
        CHANNEL.registerMessage(id++, SyringeFailPacket.class,SyringeFailPacket::write,SyringeFailPacket::new,SyringeFailPacket::handle);
        CHANNEL.registerMessage(id++, UseSyringePacket.class,UseSyringePacket::write,UseSyringePacket::new,UseSyringePacket::handle);
        CHANNEL.registerMessage(id++, ExchangeItemInHandPacket.class,ExchangeItemInHandPacket::write,ExchangeItemInHandPacket::new,ExchangeItemInHandPacket::handle);
        CHANNEL.registerMessage(id++, UseBandagePacket.class,UseBandagePacket::write,UseBandagePacket::new,UseBandagePacket::handle);
        CHANNEL.registerMessage(id++,DislocationTryPacket.class,DislocationTryPacket::write,DislocationTryPacket::new,DislocationTryPacket::handle);
        CHANNEL.registerMessage(id++,ShrapnelFailPacket.class,ShrapnelFailPacket::write,ShrapnelFailPacket::new,ShrapnelFailPacket::handle);
        CHANNEL.registerMessage(id++, AdjustShrapnelPacket.class,AdjustShrapnelPacket::write,AdjustShrapnelPacket::new,AdjustShrapnelPacket::handle);
        CHANNEL.registerMessage(id++, UseBagMedItemPacket.class,UseBagMedItemPacket::write,UseBagMedItemPacket::new,UseBagMedItemPacket::handle);
        CHANNEL.registerMessage(id++, ExchangeItemInBagPacket.class,ExchangeItemInBagPacket::write,ExchangeItemInBagPacket::new,ExchangeItemInBagPacket::handle);
        CHANNEL.registerMessage(id++, CauterizeActionPacket.class,CauterizeActionPacket::write,CauterizeActionPacket::new,CauterizeActionPacket::handle);
        CHANNEL.registerMessage(id++, TalkPacket.class,TalkPacket::toBytes,TalkPacket::new,TalkPacket::handle);
        CHANNEL.registerMessage(id++, CPRPacket.class,CPRPacket::write,CPRPacket::new,CPRPacket::handle);
        CHANNEL.registerMessage(id++, TriggerLastStandPacket.class,TriggerLastStandPacket::write,TriggerLastStandPacket::new,TriggerLastStandPacket::handle);
        CHANNEL.registerMessage(id++, BlindnessViewSyncPacket.class,BlindnessViewSyncPacket::encode,BlindnessViewSyncPacket::decode,BlindnessViewSyncPacket::handle);
        CHANNEL.registerMessage(id++,FluidSyncS2CPacket.class,FluidSyncS2CPacket::write,FluidSyncS2CPacket::new,FluidSyncS2CPacket::handle,Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
