package net.adinvas.prototype_pain.compat.curios;

import net.minecraftforge.fml.ModList;

public class CuriosCompat {


    public static boolean isLoaded(){
        return ModList.get().isLoaded("curioApi");
    }
}
