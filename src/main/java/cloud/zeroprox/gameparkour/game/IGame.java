package cloud.zeroprox.gameparkour.game;

import cloud.zeroprox.gameparkour.GameParkour;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public interface IGame {

    String getName();

    boolean isBox(Location<World> worldLocation);

    GameParkour.Mode getMode();

    void addPlayer(Player player);

    void playerInteract(Player player, BlockSnapshot targetBlock);

    void toggleStatus();

    void stop();
}
