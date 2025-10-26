package net.adinvas.prototype_pain.visual;

import net.minecraft.client.player.LocalPlayer;

public interface IModelPartHider {
    void hidePart(LocalPlayer player,String partname);
    void showPart(LocalPlayer player,String partname);
}
