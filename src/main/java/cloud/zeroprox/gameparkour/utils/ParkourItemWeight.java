package cloud.zeroprox.gameparkour.utils;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.inventory.ItemStack;

@ConfigSerializable
public class ParkourItemWeight {

    @Setting(value = "weight", comment = "Higher numbers give more change of getting the reward")
    public int weight;

    @Setting(value = "itemstack", comment = "Item to give")
    public ItemStack itemtype;

    @Setting(value = "amount", comment = "Amount to give")
    public ParkourAmount amount;

    public ParkourItemWeight() {}

    public ParkourItemWeight(ItemStack item, int weight, ParkourAmount range) {
        this.itemtype = item;
        this.weight = weight;
        this.amount = range;
    }

}
