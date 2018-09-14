package cloud.zeroprox.gameparkour;

import cloud.zeroprox.gameparkour.game.IGame;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class Listeners {

    @Listener
    public void onInteractBlockEvent(InteractBlockEvent event, @First Player player) {
        if (player != null) {
            Optional<Location<World>> optional = event.getTargetBlock().getLocation();
            if (optional.isPresent()) {
                Optional<IGame> gameOptional = GameParkour.getGameManager().getGameFromLocation(optional.get());
                if (gameOptional.isPresent()) {
                    gameOptional.get().playerInteract(player, event.getTargetBlock());
                    event.setCancelled(true);
                }
            }
        }
    }
}
