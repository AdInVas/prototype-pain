package net.adinvas.prototype_pain.events;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = PrototypePain.MOD_ID, value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientShaderEvents {
    public static ShaderInstance BRAIN_SHADER;

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        new ResourceLocation(PrototypePain.MOD_ID, "brain_distortion"),
                        DefaultVertexFormat.POSITION_TEX),
                shader -> BRAIN_SHADER = shader
        );
    }
}
