package cloud.zeroprox.gameparkour.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.Arrays;

public class AdminCmd implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        PaginationList.builder()
                .title(Text.of("Arena management"))
                .contents(
                        Arrays.asList(
                                Text.builder("Create new").color(TextColors.GREEN).onClick(TextActions.suggestCommand("/parkour admin build name <name>")).build(),
                                Text.builder("Stop setup").color(TextColors.GREEN).onClick(TextActions.suggestCommand("/parkour admin build stop")).build(),
                                Text.builder("Resume setup").color(TextColors.GREEN).onClick(TextActions.runCommand("/parkour admin build")).build(),
                                Text.builder("Disable area").color(TextColors.GREEN).onClick(TextActions.suggestCommand("/parkour admin toggle <name>")).build(),
                                Text.builder("Remove area").color(TextColors.GREEN).onClick(TextActions.suggestCommand("/parkour admin remove <name>")).build()
                                )
                )
                .build()
        .sendTo(src);
        return CommandResult.success();
    }
}
