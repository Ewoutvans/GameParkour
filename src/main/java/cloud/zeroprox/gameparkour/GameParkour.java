package cloud.zeroprox.gameparkour;

import cloud.zeroprox.gameparkour.commands.AdminCmd;
import cloud.zeroprox.gameparkour.commands.HelpCmd;
import cloud.zeroprox.gameparkour.commands.JoinCmd;
import cloud.zeroprox.gameparkour.commands.ListCmd;
import cloud.zeroprox.gameparkour.commands.admin.BuildCmd;
import cloud.zeroprox.gameparkour.commands.admin.DisableCmd;
import cloud.zeroprox.gameparkour.commands.admin.RemoveCmd;
import cloud.zeroprox.gameparkour.game.GameClassic;
import cloud.zeroprox.gameparkour.game.GameManager;
import cloud.zeroprox.gameparkour.game.IGame;
import cloud.zeroprox.gameparkour.utils.CooldownManager;
import cloud.zeroprox.gameparkour.utils.GameSerialize;
import cloud.zeroprox.gameparkour.utils.MessageManager;
import cloud.zeroprox.gameparkour.utils.TransformWorldSerializer;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.*;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Plugin(id = "gameparkour", name = "GameParkour", description = "A parkour minigame", url = "http://zeroprox.cloud", authors = {"ewoutvs_"})
public class GameParkour {


    //#region variables
    @Inject
    public Logger logger;
    private static GameParkour instance;
    private static GameManager gameManager;
    private static MessageManager messageManager;
    private static CooldownManager cooldownManager;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManagerDefaultConfig;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;

    private ConfigurationNode rootNodeDefaultConfig;
    //#endregion

    //#region commands
    CommandSpec joinCmd = CommandSpec.builder()
            .description(Text.of("Join a game"))
            .arguments(
                    GenericArguments.optional(new GameArgument(Text.of("game")))
            )
            .permission("gameparkour.join")
            .executor(new JoinCmd())
            .build();

    CommandSpec adminBuildCmd = CommandSpec.builder()
            .description(Text.of("Build"))
            .arguments(
                    GenericArguments.optional(GenericArguments.enumValue(Text.of("type"), AdminBuildTypes.class)),
                    GenericArguments.optional(GenericArguments.string(Text.of("name")))
            )
            .executor(new BuildCmd())
            .build();

    CommandSpec adminToggleCmd = CommandSpec.builder()
            .description(Text.of("Toggle arena"))
            .arguments(
                    GenericArguments.optional(new GameArgument(Text.of("game")))
            )
            .executor(new DisableCmd())
            .build();

    CommandSpec adminRemoveCmd = CommandSpec.builder()
            .description(Text.of("Remove arena"))
            .arguments(
                    GenericArguments.optional(new GameArgument(Text.of("game")))
            )
            .executor(new RemoveCmd())
            .build();

    CommandSpec adminCmd = CommandSpec.builder()
            .description(Text.of("Area management"))
            .permission("gameparkour.admin")
            .executor(new AdminCmd())
            .child(adminBuildCmd, "build")
            .child(adminToggleCmd, "toggle")
            .child(adminRemoveCmd, "remove")
            .build();

    CommandSpec listCmd = CommandSpec.builder()
            .description(Text.of("Show game list"))
            .executor(new ListCmd())
            .permission("gameparkour.join")
            .build();

    CommandSpec parkourCmd = CommandSpec.builder()
            .description(Text.of("Main command"))
            .child(joinCmd, "join", "tp", "goto", "warp")
            .child(listCmd, "list")
            .child(adminCmd, "admin")
            .executor(new HelpCmd())
            .build();
    //#endregion

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        Sponge.getCommandManager().register(this, parkourCmd, "gameparkour", "parkour");
        Sponge.getEventManager().registerListeners(this, new Listeners());
        TypeToken<Transform<World>> transformTypeToken = new TypeToken<Transform<World>>() {};
        TypeSerializers.getDefaultSerializers().registerType(transformTypeToken, new TransformWorldSerializer());

        gameManager = new GameManager();
        instance = this;
        configManagerDefaultConfig = HoconConfigurationLoader.builder().setPath(defaultConfig).build();
        try {
            rootNodeDefaultConfig = configManagerDefaultConfig.load();
            loadMessages();
            loadCooldowns();
            loadConfig();
        } catch(IOException e) {
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public static GameParkour getInstance() {
        return instance;
    }

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public static MessageManager mM() {
        return messageManager;
    }


    @Listener
    public void onGameReload(GameReloadEvent event) {
        try {
            rootNodeDefaultConfig = configManagerDefaultConfig.load();
            loadMessages();
            loadCooldowns();
            loadConfig();
        } catch (IOException e) {
        } catch (ObjectMappingException e) {
        }
    }

    @Listener
    public void onGameStopped(GameStoppedServerEvent event) {
        for (IGame iGame : gameManager.iGames) {
            iGame.stop();
        }
    }

    private void loadMessages() {
        messageManager = new MessageManager(privateConfigDir);
    }

    private void loadCooldowns() {
        cooldownManager = new CooldownManager(privateConfigDir);
    }

    private void loadConfig() throws IOException, ObjectMappingException {
        if (rootNodeDefaultConfig.getNode("areas").isVirtual()) {
            logger.info("Creating games configuration");

            rootNodeDefaultConfig.getNode("areas").setValue(new TypeToken<List<GameSerialize>>(){}, Arrays.asList());
            configManagerDefaultConfig.save(rootNodeDefaultConfig);
            loadConfig();
        } else {
            getGameManager().iGames.clear();
            List<GameSerialize> gameSerializeList = rootNodeDefaultConfig.getNode("areas").getList(TypeToken.of(GameSerialize.class));
            for (GameSerialize gameSerialize : gameSerializeList) {
                IGame iGame = null;

                if (gameSerialize.gameType == GameType.CLASSIC) {
                    iGame = new GameClassic(gameSerialize, cooldownManager.loadCooldownsForKey(gameSerialize.name));
                }
                getGameManager().iGames.add(iGame);
            }



            logger.info("Loaded: " + getGameManager().iGames.size() + " games");
        }
    }

    public void addArena(GameSerialize gameSerialize) {
        try {
            List<GameSerialize> gameSerializeList = rootNodeDefaultConfig.getNode("areas").getList(TypeToken.of(GameSerialize.class));
            List<GameSerialize> gameList = new ArrayList<>();
            gameList.addAll(gameSerializeList);
            gameList.add(gameSerialize);
            rootNodeDefaultConfig.getNode("areas").setValue(new TypeToken<List<GameSerialize>>(){}, gameList);
            configManagerDefaultConfig.save(rootNodeDefaultConfig);
            loadConfig();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeArena(IGame iGame) {
        try {
            List<GameSerialize> gameSerializeList = rootNodeDefaultConfig.getNode("areas").getList(TypeToken.of(GameSerialize.class));
            List<GameSerialize> gameList = new ArrayList<>();
            gameList.addAll(gameSerializeList);
            gameList.removeIf(gameSerialize -> gameSerialize.name.equalsIgnoreCase(iGame.getName()));
            rootNodeDefaultConfig.getNode("areas").setValue(new TypeToken<List<GameSerialize>>(){}, gameList);
            configManagerDefaultConfig.save(rootNodeDefaultConfig);
            loadConfig();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum GameType {
        CLASSIC
    }

    public enum Mode {
        DISABLED, READY
    }

    public enum AdminBuildTypes {
        NAME, SPAWN, BOX, SAVE, STOP, TYPE
    }

    public class GameArgument extends CommandElement {

        protected GameArgument(@Nullable Text key) {
            super(key);
        }

        @Nullable
        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            return args.next();
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            List<String> games = new ArrayList<>();
            for (IGame iGame : GameParkour.getGameManager().iGames) {
                games.add(iGame.getName());
            }
            return games;
        }
    }
}
