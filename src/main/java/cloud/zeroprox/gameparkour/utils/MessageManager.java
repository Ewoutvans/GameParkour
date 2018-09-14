package cloud.zeroprox.gameparkour.utils;

import cloud.zeroprox.gameparkour.GameParkour;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.spongepowered.api.text.TextTemplate.arg;
import static org.spongepowered.api.text.TextTemplate.of;

public class MessageManager {

    public TextTemplate PARKOUR;
    public TextTemplate PARKOUR_HELP_TITLE;
    public TextTemplate NEED_TO_BE_PLAYER;
    public TextTemplate GAME_NAME_NOT_FOUND;
    public TextTemplate COOLDOWN;


    private Path messagesConfig;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode rootNode;

    public MessageManager(Path configDir) {
        messagesConfig = Paths.get(new File(configDir.toFile(), "messages.conf").toURI());
        loader = HoconConfigurationLoader.builder().setPath(messagesConfig).build();
        try {
            rootNode = loader.load();
            loadConfig();
        } catch (IOException e) {
        } catch (ObjectMappingException e) {
        }
    }

    private void loadConfig() throws IOException, ObjectMappingException {
        if (rootNode.getNode("messages", "PARKOUR").isVirtual()) {
            GameParkour.getInstance().logger.info("Creating messages configuration");
            rootNode.getNode("messages", "PARKOUR").setValue(TypeToken.of(TextTemplate.class), of(Text.of(TextColors.GRAY, "["), Text.of(TextColors.RED, "PARKOUR"), Text.of(TextColors.GRAY, "] ")));
            rootNode.getNode("messages", "PARKOUR_HELP_TITLE").setValue(TypeToken.of(TextTemplate.class), of(Text.of(TextColors.GREEN, "Game Parkour commands")));
            rootNode.getNode("messages", "NEED_TO_BE_PLAYER").setValue(TypeToken.of(TextTemplate.class), of(Text.of(TextColors.RED, "You need to be a player to do this.")));
            rootNode.getNode("messages", "GAME_NAME_NOT_FOUND").setValue(TypeToken.of(TextTemplate.class), of(Text.of(TextColors.RED, "No game found for name "), arg("gamename"), "."));
            rootNode.getNode("messages", "COOLDOWN").setValue(TypeToken.of(TextTemplate.class), of(Text.of(TextColors.RED, "You have a cooldown of "), arg("cooldown"), "."));

            loader.save(rootNode);
            loadConfig();
        } else {
            PARKOUR = rootNode.getNode("messages", "PARKOUR").getValue(TypeToken.of(TextTemplate.class));
            PARKOUR_HELP_TITLE = rootNode.getNode("messages", "PARKOUR_HELP_TITLE").getValue(TypeToken.of(TextTemplate.class));
            NEED_TO_BE_PLAYER = rootNode.getNode("messages", "NEED_TO_BE_PLAYER").getValue(TypeToken.of(TextTemplate.class));
            GAME_NAME_NOT_FOUND = rootNode.getNode("messages", "GAME_NAME_NOT_FOUND").getValue(TypeToken.of(TextTemplate.class));
            COOLDOWN = rootNode.getNode("messages", "COOLDOWN").getValue(TypeToken.of(TextTemplate.class));

            GameParkour.getInstance().logger.info("Loaded messages");
        }
    }
}
