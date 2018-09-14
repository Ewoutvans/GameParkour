package cloud.zeroprox.gameparkour.utils;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.UUID;

@ConfigSerializable
public class CooldownValue {

    @Setting("player")
    public UUID player;

    @Setting("time")
    public Long time;

    public CooldownValue() {}

    public CooldownValue(UUID uniqueId, long l) {
        this.player = uniqueId;
        this.time = l;
    }
}
