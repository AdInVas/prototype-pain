package net.adinvas.prototype_pain.network;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.io.*;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SyncHealthPacket {
    final CompoundTag tag;
    final UUID target;

    public SyncHealthPacket(CompoundTag tag, UUID target){
        this.tag = tag;
        this.target = target;
    }

    public SyncHealthPacket(FriendlyByteBuf buf){
        this.tag = buf.readNbt();
        this.target = buf.readUUID();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeNbt(tag);
        buf.writeUUID(target);
    }
    public static void handle(SyncHealthPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context c = ctx.get();
        // This packet is registered PLAY_TO_CLIENT, but still guard & offload client code:
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.handleSyncHealth(msg));
        c.setPacketHandled(true);
    }


    public static CompoundTag decompressNBT(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try (GZIPInputStream gzip = new GZIPInputStream(bais);
             DataInputStream dis = new DataInputStream(gzip)) {
            return NbtIo.read(dis); // reads the tag from the stream
        }
    }
    public static byte[] compressNBT(CompoundTag tag) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos);
             DataOutputStream dos = new DataOutputStream(gzip)) {
            NbtIo.write(tag, dos); // writes the tag to the stream
        }
        return baos.toByteArray();
    }
}
