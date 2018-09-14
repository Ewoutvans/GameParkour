package cloud.zeroprox.gameparkour.commands;

import cloud.zeroprox.gameparkour.GameParkour;
import cloud.zeroprox.gameparkour.game.IGame;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class ListCmd implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        List<Text> arenas = new ArrayList<>();
        for (IGame iGame : GameParkour.getGameManager().iGames) {
            arenas.add(Text.builder(iGame.getName()).color(iGame.getMode() == GameParkour.Mode.DISABLED ? TextColors.RED : TextColors.GREEN).onClick(TextActions.runCommand("/parkour join " + iGame.getName())).build());
        }

        PaginationList.builder()
                .title(GameParkour.mM().PARKOUR.apply().build())
                .padding(Text.of(TextColors.GOLD, "="))
                .contents(arenas)
                .build()
                .sendTo(src);
        return CommandResult.success();
    }
}
