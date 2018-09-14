package cloud.zeroprox.gameparkour.game;

import cloud.zeroprox.gameparkour.GameParkour;
import cloud.zeroprox.gameparkour.utils.CooldownValue;
import cloud.zeroprox.gameparkour.utils.GameSerialize;
import cloud.zeroprox.gameparkour.utils.ParkourItemWeight;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.weighted.VariableAmount;
import org.spongepowered.api.util.weighted.WeightedObject;
import org.spongepowered.api.util.weighted.WeightedTable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class GameClassic implements IGame {

    private String name;
    private Transform<World> box, spawn;
    private int cooldown;
    private VariableAmount boxItemAmount;
    private List<String> winningCommands;
    private WeightedTable<ParkourItemWeight> rewards = new WeightedTable<>();
    private ArrayList<CooldownValue> cooldowns;
    private GameParkour.Mode mode;
    private static Random random = new Random();

    public GameClassic(GameSerialize gameSerialize, ArrayList<CooldownValue> cooldowns) {
        this.name = gameSerialize.name;
        this.box = gameSerialize.box;
        this.spawn = gameSerialize.spawn;
        this.boxItemAmount = VariableAmount.range(gameSerialize.boxmin, gameSerialize.boxmax);
        this.cooldown = gameSerialize.cooldown;
        this.winningCommands = gameSerialize.winningCommand;
        this.mode = GameParkour.Mode.READY;
        this.cooldowns = cooldowns;
        for (ParkourItemWeight entry : gameSerialize.items) {
            rewards.add(new WeightedObject<>(entry, entry.weight));
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isBox(Location<World> worldLocation) {
        if (this.box.getExtent() != worldLocation.getExtent()) return false;
        return !(this.box.getLocation().getPosition().toInt().distanceSquared(worldLocation.getPosition().toInt()) > 0.5f);
    }

    @Override
    public GameParkour.Mode getMode() {
        return this.mode;
    }

    @Override
    public void addPlayer(Player player) {
        player.setTransform(this.spawn);
    }

    @Override
    public void playerInteract(Player player, BlockSnapshot targetBlock) {
        if (isBox(targetBlock.getLocation().get())) {
            if (!hasCooldown(player.getUniqueId())) {
                for (String cmd : this.winningCommands) {
                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd.replace("%player%", player.getName()).replace("%game%", this.name));
                }
                for (int i = (int)boxItemAmount.getAmount(random); i >= 0; i--) {
                    ParkourItemWeight item = this.rewards.get(random).get(0);
                    ItemStack is = item.itemtype.copy();
                    is.setQuantity((item.amount.fixed ? item.amount.min : (int)VariableAmount.range(item.amount.min, item.amount.max).getAmount(random)));
                    player.getInventory().offer(is);
                }
                setCooldown(player.getUniqueId());
            } else {
                player.sendMessage(GameParkour.mM().COOLDOWN.apply(ImmutableMap.of("cooldown", cooldown(player.getUniqueId()))).build());
            }
        }
    }

    @Override
    public void toggleStatus() {
        this.mode = (this.mode == GameParkour.Mode.READY ? GameParkour.Mode.DISABLED : GameParkour.Mode.READY);
    }

    @Override
    public void stop() {
        GameParkour.getCooldownManager().saveCooldownsForKey(cooldowns, name);
    }

    public boolean hasCooldown(UUID uniqueId) {
        Optional<CooldownValue> cooldownValue = this.cooldowns.stream().filter(cooldownValueF -> cooldownValueF.player == uniqueId).findFirst();
        return cooldownValue.isPresent() && cooldownValue.get().time + TimeUnit.MINUTES.toMillis(cooldown) >= System.currentTimeMillis();
    }

    public void setCooldown(UUID uniqueId) {
        Optional<CooldownValue> cooldownValue = this.cooldowns.stream().filter(cooldownValueF -> cooldownValueF.player == uniqueId).findFirst();
        if (cooldownValue.isPresent()) {
            this.cooldowns.remove(cooldownValue.get());
        }
        this.cooldowns.add(new CooldownValue(uniqueId, System.currentTimeMillis()));
    }

    public String cooldown(UUID uniqueId) {
        Optional<CooldownValue> cooldownValue = this.cooldowns.stream().filter(cooldownValueF -> cooldownValueF.player == uniqueId).findFirst();
        if (!cooldownValue.isPresent()) {
            return "unknown";
        }
        long l = cooldownValue.get().time + TimeUnit.MINUTES.toMillis(cooldown);
        long now = System.currentTimeMillis();

        long soon = l - now;
        if (soon <= 0) {
            return "now";
        } else {
            StringBuilder sb = new StringBuilder();
            long h = TimeUnit.HOURS.convert(soon, TimeUnit.MILLISECONDS);
            long m = TimeUnit.MINUTES.convert(soon, TimeUnit.MILLISECONDS) - h * 60;
            long sec = TimeUnit.SECONDS.convert(soon, TimeUnit.MILLISECONDS) - (m * 60);
            if (h != 0 && m != 0 && sec != 0) {
                sb.append(h).append("h").append(m).append("m");
            } else if (h == 0 && m != 0 && sec != 0) {
                sb.append(m).append("m").append(sec).append("s");
            } else {
                sb.append(sec).append("s");
            }
            return sb.toString();
        }
    }
}
