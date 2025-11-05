package net.adinvas.prototype_pain.client.overlays.ovr;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class ReducedContiousnessOverlay implements IOverlay {
    private float intensity= 1;
    private float brain = 100;
    private boolean dying = false;

    private float lastInt=0;

    @Override
    public void render(GuiGraphics ms, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        intensity = (float) Mth.lerp(0.25,lastInt,intensity);
        lastInt = intensity;
        if (intensity>0.95){
            Component text = Component.translatable("prototype_pain.gui.give_up",Component.keybind("key.protoype_pain.give_up"));
            ms.drawCenteredString(mc.font,text,width/2,height/2,0xFFFFFF);
        }
        if (dying){
            ms.blit(new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/brain.png"),width/2-16,height/4-40,0,0,32,32,32,32);
            ms.fill(width/2-50,height/4, (int) (width/2-50+brain),height/4+10,0xFFFF4444);
        }
    }

    @Override
    public boolean shouldRender() {
        return intensity>0;
    }


    public void calculate(Player player) {
        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
            intensity = (100-h.getContiousness())/100;
            dying = h.getOxygen()<4;
            brain = h.getBrainHealth();
        });
    }
}
