package net.adinvas.prototype_pain.compat.simple_voicechat;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
import de.maxhenkel.voicechat.api.events.ClientSoundEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.adinvas.prototype_pain.network.TalkPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.Random;

@ForgeVoicechatPlugin
public class MainVoicePlugin implements VoicechatPlugin {
    @Override
    public String getPluginId() {
        return "prototype_pain:main";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(ClientSoundEvent.class,this::onClientSound);

        registration.registerEvent(ClientReceiveSoundEvent.EntitySound.class,this::onClientReceiveEntitySound);
        registration.registerEvent(ClientReceiveSoundEvent.LocationalSound.class,this::onClientReceiveLocationalSound);
        registration.registerEvent(ClientReceiveSoundEvent.StaticSound.class,this::onClientReceiveStaticSound);
    }

    private void onClientSound(ClientSoundEvent event) {
       short [] in = event.getRawAudio();

       short[] out = applyAllOutMods(in);
       event.setRawAudio(out);
    }

    private void onClientReceiveEntitySound(ClientReceiveSoundEvent.EntitySound event) {
        short[] raw = event.getRawAudio();
        if (raw == null || raw.length == 0) return;
        short[] out = applyAllInMods(raw);
        event.setRawAudio(out);
    }

    private void onClientReceiveLocationalSound(ClientReceiveSoundEvent.LocationalSound event) {
        short[] raw = event.getRawAudio();
        if (raw == null || raw.length == 0) return;
        short[] out = applyAllInMods(raw);
        event.setRawAudio(out);
    }

    private void onClientReceiveStaticSound(ClientReceiveSoundEvent.StaticSound event) {
        short[] raw = event.getRawAudio();
        if (raw == null || raw.length == 0) return;
        short[] out = applyAllInMods(raw);
        event.setRawAudio(out);
    }

    private float getCons(Player player){
        return player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getContiousness).orElse(100f);
    }
    private short[] applyAllInMods(short[] in){
        short[] out;
        Player player = Minecraft.getInstance().player;
        float cons = getCons(player);
        float hearingloss = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(PlayerHealthData::getHearingLoss).orElse(0f);
        float hearing_scale = Math.max((100f-cons)/100f,hearingloss);
        out = applyMuffle(in,hearing_scale);
        return out;
    }
    private short[] applyAllOutMods(short[] in){
        Player player = Minecraft.getInstance().player;
        short[] out = in;
        if (sendDistorted(player)){
            out = applyMuffle(out,0.4f);
            out = applyVolumeVariation(out);
            out = applyEcho(out,0.3f,850);
        }
        ModNetwork.CHANNEL.sendToServer(new TalkPacket());
        boolean isBrainDamaged = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->h.getBrainHealth()<60).orElse(false);
        if (getCons(player)<10||isBrainDamaged) {
            for (int i=0;i<out.length;i++){
                out[i] *= 0;
            }
        }
        return out;
    }

    private boolean sendDistorted(Player player){
        return player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).map(h->h.getLimbDislocated(Limb.HEAD)>0||h.isMouthRemoved()).orElse(false);
    }

    private short[] applyEcho(short[] in, float power, int delay){
        short[] out = in;
        for (int i = delay; i < out.length; i++) {
            out[i] += (short) (out[i - delay] * power);
        }
        return out;
    }

    private short[] applyDistortion(short[] in){
        Random r = new Random();
        short[] out = in;
        for (int i = 0; i < out.length; i++) {
            if (Math.abs(out[i]) > 28000) out[i] *= 0.7f; // compress peaks
            if (r.nextFloat() < 0.01f) out[i] *= 0.5f;       // occasional drop
        }

        return out;
    }

    private float lastSample = 0f; // store between packets

    public short[] applyMuffle(short[] in, float amount) {
        if (in == null || in.length == 0 || amount <= 0f) return in;

        final float sampleRate = 48000f;

        // Use an exponential curve so small changes near 1 have bigger effect
        float curved = (float)Math.pow(amount, 2.5f);  // try 2.0–3.0 for more or less compression

        float minCutoff = 25f;
        float maxCutoff = 8000f;

// exponential curve: 0→maxCutoff, 1→minCutoff, smooth perception
        float cutoff = (float)(maxCutoff * Math.pow(minCutoff / maxCutoff, amount));

        float rc = 1f / (2f * (float)Math.PI * cutoff);
        float dt = 1f / sampleRate;
        float alpha = dt / (rc + dt);

        short[] out = new short[in.length];
        float prev = lastSample;

        // Optional: add light volume loss with same perceptual curve
        float volumeScale = 1f - 0.2f * curved;

        for (int i = 0; i < in.length; i++) {
            prev = prev + alpha * (in[i] - prev);
            out[i] = (short)(prev * volumeScale);
        }

        lastSample = prev;
        return out;
    }

    public static short[] applyVolumeVariation(short[] samples){
        Random r = new Random();
        for (int i = 0; i < samples.length; i++) {
            float variation = 0.8f + r.nextFloat() * 0.4f; // 0.8x – 1.2x volume
            samples[i] *= variation;
        }
        return samples;
    }

}
