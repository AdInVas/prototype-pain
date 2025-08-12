package net.adinvas.prototype_pain;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static KeyMapping Open_PAIN_GUI = new KeyMapping("key.protoype_pain.open_pain_gui", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P,"key.categories.prototype_pain");




    public static void register(FMLClientSetupEvent event){
        Clientregistry.reg
    }
}
