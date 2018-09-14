package cloud.zeroprox.gameparkour.utils;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CooldownManager {

    private Path messagesConfig;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode rootNode;

    public CooldownManager(Path configDir) {
        messagesConfig = Paths.get(new File(configDir.toFile(), "cooldowns.conf").toURI());
        loader = HoconConfigurationLoader.builder().setPath(messagesConfig).build();
        try {
            rootNode = loader.load();
        } catch (IOException e) {
        }
    }

    public ArrayList<CooldownValue> loadCooldownsForKey(String key) {
        try {
            if (rootNode.getNode("cooldowns", key).isVirtual()) {
                return new ArrayList<>();
            } else {
                return new ArrayList<>(rootNode.getNode("cooldowns", key).getList(TypeToken.of(CooldownValue.class)));
            }
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public void saveCooldownsForKey(ArrayList<CooldownValue> cooldowns, String key) {
        try {
            rootNode.getNode("cooldowns", key).setValue(new TypeToken<List<CooldownValue>>(){}, cooldowns);
            loader.save(rootNode);
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
        }
    }
}
