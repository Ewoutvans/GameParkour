package cloud.zeroprox.gameparkour.commands;

import cloud.zeroprox.gameparkour.GameParkour;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class HelpCmd implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        PaginationList.builder()
                .title(GameParkour.mM().PARKOUR_HELP_TITLE.apply().build())
                .padding(Text.of(TextColors.GOLD, "="))
                .contents(
                        Text.builder("/parkour tp ").color(TextColors.GREEN).append(Text.of(TextColors.WHITE, "[area]")).onClick(TextActions.suggestCommand("/parkour tp ")).build(),
                        Text.builder("/parkour list").color(TextColors.GREEN).onClick(TextActions.runCommand("/parkour list")).build(),
                        Text.builder("/parkour admin").color(src.hasPermission("gameparkour.admin") ? TextColors.GREEN : TextColors.RED).onClick(TextActions.runCommand("/parkour admin")).build()
                )
                .build()
                .sendTo(src);
        return CommandResult.success();
    }
}
