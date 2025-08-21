package net.adinvas.prototype_pain.limbs;

import net.minecraft.nbt.CompoundTag;

public class DelayedChangeEntry {
    private Limb limb;
    private float amount_per_tick;
    private int ticks;

    public DelayedChangeEntry(float amount_per_tick, int ticks,Limb limb){
        this.limb = limb;
        this.ticks = ticks;
        this.amount_per_tick = amount_per_tick;
    }

    public float getAmount_per_tick() {
        return amount_per_tick;
    }

    public void setAmount_per_tick(float amount_per_tick) {
        this.amount_per_tick = amount_per_tick;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public void setLimb(Limb limb) {
        this.limb = limb;
    }

    public Limb getLimb() {
        return limb;
    }

    public int getTicks() {
        return ticks;
    }

    public void reduceTicks(int value){
        this.ticks -= value;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("lifetime", ticks);
        tag.putFloat("PerTick", amount_per_tick);
        tag.putString("Limb", limb.name());
        return tag;
    }

    public static DelayedChangeEntry fromNBT(CompoundTag tag) {
        int v1 = tag.getInt("lifetime");
        float v2 = tag.getFloat("PerTick");
        Limb limb = Limb.valueOf(tag.getString("Limb"));

        return new DelayedChangeEntry(v2, v1, limb);
    }
}
