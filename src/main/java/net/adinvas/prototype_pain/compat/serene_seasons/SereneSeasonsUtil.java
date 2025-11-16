package net.adinvas.prototype_pain.compat.serene_seasons;

import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.compat.TempCompat;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.core.SereneSeasons;

public class SereneSeasonsUtil {

    public static boolean isLoaded(){
        return ModList.get().isLoaded("sereneseasons");
    }

    public static float getSeasonScale(Level level, TempCompat.BiomeTemperatureEntry entry){
        if (!isLoaded())return entry.temperature;
        ISeasonState seasonState = SeasonHelper.getSeasonState(level);
        Season.SubSeason sub = seasonState.getSubSeason();

        int subIndex = sub.ordinal();

        int seasonIndex = subIndex / 3;
        float blend = (subIndex % 3) / 3f;

        Season[] seasons = Season.values();
        Season currentSeason = seasons[seasonIndex];
        Season nextSeason = seasons[(seasonIndex+1)%4];

        float startTemp = switch (currentSeason){
            case WINTER -> {
                yield entry.winter;
            }
            case SUMMER -> {
                yield entry.summer;
            }
            case AUTUMN -> {
                yield entry.fall;
            }
            case SPRING -> {
                yield entry.spring;
            }
        };
        float targetTemp =switch (nextSeason){
            case WINTER -> {
                yield entry.winter;
            }
            case SUMMER -> {
                yield entry.summer;
            }
            case AUTUMN -> {
                yield entry.fall;
            }
            case SPRING -> {
                yield entry.spring;
            }
        };
        return Mth.lerp(blend,startTemp,targetTemp);
    }
}
