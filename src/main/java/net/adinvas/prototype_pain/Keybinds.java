package net.adinvas.prototype_pain;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static KeyMapping OPEN_PAIN_GUI = new KeyMapping("key.protoype_pain.open_pain_gui", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P,"key.categories.prototype_pain");
    public static KeyMapping GIVE_UP = new KeyMapping("key.protoype_pain.give_up",InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K,"key.categories.prototype_pain");



    public static void register(RegisterKeyMappingsEvent event){
        event.register(OPEN_PAIN_GUI);
        event.register(GIVE_UP);
    }


}
