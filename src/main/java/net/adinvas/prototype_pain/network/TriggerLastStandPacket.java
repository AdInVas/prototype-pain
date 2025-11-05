package net.adinvas.prototype_pain.network;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.io.*;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TriggerLastStandPacket {

    public TriggerLastStandPacket(){}

    public TriggerLastStandPacket(FriendlyByteBuf buf){
    }

    public void write(FriendlyByteBuf buf){
    }
    public static void handle(TriggerLastStandPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context c = ctx.get();
        // This packet is registered PLAY_TO_CLIENT, but still guard & offload client code:
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandlers::handleLastStand);
        c.setPacketHandled(true);
    }

}
