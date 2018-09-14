package cloud.zeroprox.gameparkour.utils;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ParkourAmount {

    @Setting(value = "fixed", comment = "Boolean, give fixed amount or variable amount (variable is done by min/max)")
    public boolean fixed;

    @Setting(value = "min", comment = "Maximum variable amount to give")
    public int min;

    @Setting(value = "max", comment = "Minimum variable amount to give")
    public int max;

    public ParkourAmount() {}

    public ParkourAmount(boolean fixed, int min, int max) {
        this.fixed = fixed;
        this.min = min;
        this.max = max;
    }
}
