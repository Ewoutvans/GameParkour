package cloud.zeroprox.gameparkour.commands.admin;

import cloud.zeroprox.gameparkour.GameParkour;
import cloud.zeroprox.gameparkour.utils.GameSerialize;
import cloud.zeroprox.gameparkour.utils.ParkourAmount;
import cloud.zeroprox.gameparkour.utils.ParkourItemWeight;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class BuildCmd implements CommandExecutor {

    GameSerialize gameSerialize;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of(TextColors.RED, "You need to be a player"));
        }
        Player player = (Player)src;
        Optional<GameParkour.AdminBuildTypes> adminOptional = args.getOne(Text.of("type"));
        if (!adminOptional.isPresent()) {
            showProgress(src);
            return CommandResult.empty();
        }
        GameParkour.AdminBuildTypes adminType = adminOptional.get();
        if (adminType.equals(GameParkour.AdminBuildTypes.SAVE)) {
            GameParkour.getInstance().addArena(gameSerialize);

            src.sendMessage(Text.of("Saved"));
            return CommandResult.success();
        }
        if (adminType.equals(GameParkour.AdminBuildTypes.NAME)) {
            Optional<String> name = args.getOne(Text.of("name"));
            this.gameSerialize = new GameSerialize();
            this.gameSerialize.gameType = GameParkour.GameType.CLASSIC;
            this.gameSerialize.name = name.orElse(new Random().nextLong() + "");
            this.gameSerialize.winningCommand = Arrays.asList("give %player% minecraft:diamond 1", "say %player% got a diamond for finishing parkour %game%!");
            this.gameSerialize.cooldown = 1;
            this.gameSerialize.boxmax = 5;
            this.gameSerialize.boxmin = 2;
            this.gameSerialize.items = Arrays.asList(
                    new ParkourItemWeight(ItemStack.of(ItemTypes.DIAMOND, 1), 1, new ParkourAmount(false, 1, 20)),
                    new ParkourItemWeight(ItemStack.of(ItemTypes.IRON_INGOT, 1), 1, new ParkourAmount(false, 1, 20)),
                    new ParkourItemWeight(ItemStack.of(ItemTypes.GOLD_INGOT, 1), 1, new ParkourAmount(false, 1, 20)),
                    new ParkourItemWeight(ItemStack.of(ItemTypes.APPLE, 1), 1, new ParkourAmount(false, 1, 20)),
                    new ParkourItemWeight(ItemStack.of(ItemTypes.GOLDEN_APPLE, 1), 1, new ParkourAmount(false, 1, 20)),
                    new ParkourItemWeight(ItemStack.of(ItemTypes.BEEF, 1), 1, new ParkourAmount(false, 1, 20)),
                    new ParkourItemWeight(ItemStack.of(ItemTypes.PUMPKIN_PIE, 1), 1, new ParkourAmount(false, 20, 40))
            );
            showProgress(src);
            return CommandResult.success();
        }
        if (adminType.equals(GameParkour.AdminBuildTypes.STOP)) {
            this.gameSerialize = null;
            src.sendMessage(Text.of(TextColors.GREEN, "Setup stopped"));
            return CommandResult.success();
        }

        switch (adminType) {
            case SPAWN:
                gameSerialize.spawn = player.getTransform();
                break;
            case BOX:
                gameSerialize.box = player.getTransform();
                break;
            default:
        }
        showProgress(src);
        return CommandResult.empty();
    }

    private void showProgress(CommandSource src) {
        List<Text> textArray = new ArrayList<>();
        if (gameSerialize == null) {
            textArray.add(Text.builder("No new builder start -click me- to start").onClick(TextActions.suggestCommand("/parkour admin build NAME <name>")).build());
        } else {
            textArray.add(Text.builder("Name: ").color(TextColors.GRAY).append(Text.builder(gameSerialize.name).color(TextColors.GREEN).build()).build());
            textArray.add(Text.builder("Spawn: ").color(TextColors.GRAY).append(colorVariable(gameSerialize.spawn)).onClick(TextActions.runCommand("/parkour admin build SPAWN")).build());
            textArray.add(Text.builder("RewardBox: ").color(TextColors.GRAY).append(colorVariable(gameSerialize.box)).onClick(TextActions.runCommand("/parkour admin build BOX")).build());
            if (gameSerialize.name != null
                    && gameSerialize.box != null
                    && gameSerialize.spawn != null) {
                textArray.add(Text.builder("Save").color(TextColors.AQUA).onClick(TextActions.runCommand("/parkour admin build SAVE")).build());
            }
        }
        PaginationList.builder()
                .title(Text.of("New build arena"))
                .contents(textArray)
                .build()
        .sendTo(src);
    }

    private Text colorVariable(Object object) {
        if (object == null) {
            return Text.builder(" --").color(TextColors.GREEN).build();
        } else if (object instanceof List) {
            int amount = ((List)object).size();
            return Text.builder(" Amount: " + amount).color((amount == 0 ? TextColors.RED: TextColors.GREEN)).build();
        } else {
            return Text.builder(" Okay").color(TextColors.GREEN).build();
        }
    }
}
