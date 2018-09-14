package cloud.zeroprox.gameparkour.game;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameManager {

    public List<IGame> iGames = new ArrayList<>();

    public Optional<IGame> getGame(String gameName) {
        return iGames.stream().filter(game -> game.getName().equalsIgnoreCase(gameName)).findFirst();
    }

    public Optional<IGame> getGameFromLocation(Location<World> worldLocation) {
        return iGames.stream().filter(game -> game.isBox(worldLocation) ).findFirst();
    }

    public String getDefaultName() {
        return iGames.size() == 0 ? "DEFAULT" : iGames.get(0).getName();
    }
}
