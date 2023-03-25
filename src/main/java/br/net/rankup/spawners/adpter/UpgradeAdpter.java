package br.net.rankup.spawners.adpter;

import br.net.rankup.spawners.model.upgrade.CapacityUpgrade;
import br.net.rankup.spawners.model.upgrade.TimeUpgrade;
import br.net.rankup.spawners.model.upgrade.XpUpgrade;
import org.bukkit.configuration.ConfigurationSection;

public class UpgradeAdpter {

    public static TimeUpgrade writeTime(ConfigurationSection section) {
        int indentifier = section.getInt("indentifier");
        double price = section.getDouble("price");
        int value = section.getInt("value");
        return  new TimeUpgrade(indentifier, price, value);
    }

    public static XpUpgrade writeXP(ConfigurationSection section) {
        int indentifier = section.getInt("indentifier");
        double price = section.getDouble("price");
        int value = section.getInt("value");
        return new XpUpgrade(indentifier, price, value);
    }

    public static CapacityUpgrade writeCapacity(ConfigurationSection section) {
        int indentifier = section.getInt("indentifier");
        double price = section.getDouble("price");
        double value = section.getDouble("value");
        return new CapacityUpgrade(indentifier, price, value);
    }

}
