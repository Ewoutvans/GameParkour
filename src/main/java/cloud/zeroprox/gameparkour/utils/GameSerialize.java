package cloud.zeroprox.gameparkour.utils;

import cloud.zeroprox.gameparkour.GameParkour;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;

import java.util.List;

@ConfigSerializable
public class GameSerialize {

    @Setting(value = "name", comment = "Name of game, has to be unique")
    public String name;

    @Setting(value = "spawn", comment = "Location of spawn point")
    public Transform<World> spawn;

    @Setting(value = "box", comment = "Location of the reward box")
    public Transform<World> box;

    @Setting(value = "gametype")
    public GameParkour.GameType gameType;

    @Setting(value = "winningCommand", comment = "Runs this command from console when someone wins. %winner% is replaced with the name of the winner, %game% is replace with the gamename")
    public List<String> winningCommand;

    @Setting(value = "cooldown", comment = "Cooldown in minutes, time is per game")
    public int cooldown;

    @Setting(value = "boxitems", comment = "Items that can be earned from clicking the box, if you only want to use comments make this empty")
    public List<ParkourItemWeight> items;

    @Setting(value = "boxmax", comment = "Maximum items to give to player")
    public int boxmax;

    @Setting(value = "boxmin", comment = "Minimum items to give to player")
    public int boxmin;

    public GameSerialize() {

    }
}
