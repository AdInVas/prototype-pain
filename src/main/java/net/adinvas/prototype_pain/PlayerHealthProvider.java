package net.adinvas.prototype_pain;

import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerHealthProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PlayerHealthData> PLAYER_HEALTH_DATA = CapabilityManager.get(new CapabilityToken<PlayerHealthData>() {});

    private PlayerHealthData playerHealthData = null;
    private final LazyOptional<PlayerHealthData> optional = LazyOptional.of(this::createPlayerHealthData);

    private @NotNull PlayerHealthData createPlayerHealthData() {
        if (this.playerHealthData==null){
            this.playerHealthData = new PlayerHealthData();
        }
        return this.playerHealthData;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap==PLAYER_HEALTH_DATA){
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerHealthData().serializeNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerHealthData().deserializeNBT(nbt);
    }
}
