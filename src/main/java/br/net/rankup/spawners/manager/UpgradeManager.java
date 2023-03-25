package br.net.rankup.spawners.manager;

import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.adpter.UpgradeAdpter;
import br.net.rankup.spawners.model.upgrade.CapacityUpgrade;
import br.net.rankup.spawners.model.upgrade.TimeUpgrade;
import br.net.rankup.spawners.model.upgrade.XpUpgrade;
import org.bukkit.configuration.ConfigurationSection;
import java.util.HashMap;

public class UpgradeManager {

    //time
    private HashMap<Integer, TimeUpgrade> upgradeTime;

    public HashMap<Integer, TimeUpgrade> getTime() {
        return upgradeTime;
    }
    //xp
    private HashMap<Integer, XpUpgrade> upgradeXP;

    public HashMap<Integer, XpUpgrade> getXP() {
        return upgradeXP;
    }
    //capacity
    private HashMap<Integer, CapacityUpgrade> upgradeCapacity;

    public HashMap<Integer, CapacityUpgrade> getCapacity() {
        return upgradeCapacity;
    }

    public void load() {
        upgradeTime = new HashMap<>();
        upgradeXP = new HashMap<>();
        upgradeCapacity = new HashMap<>();
        for (final String name : SpawnerPlugin.getConfiguration().getConfigurationSection("upgrades.times").getKeys(false)) {
            final ConfigurationSection section = SpawnerPlugin.getConfiguration().getConfigurationSection("upgrades.times." + name);
            TimeUpgrade timeUpgrade = UpgradeAdpter.writeTime(section);
            this.addTime(timeUpgrade.getIndentifier(), timeUpgrade);
        }

        for (final String name : SpawnerPlugin.getConfiguration().getConfigurationSection("upgrades.xp").getKeys(false)) {
            final ConfigurationSection section = SpawnerPlugin.getConfiguration().getConfigurationSection("upgrades.xp." + name);
            XpUpgrade xpUpgrade = UpgradeAdpter.writeXP(section);
            this.addXP(xpUpgrade.getIndentifier(), xpUpgrade);
        }

        for (final String name : SpawnerPlugin.getConfiguration().getConfigurationSection("upgrades.capacity").getKeys(false)) {
            final ConfigurationSection section = SpawnerPlugin.getConfiguration().getConfigurationSection("upgrades.capacity." + name);
            CapacityUpgrade capacityUpgrade = UpgradeAdpter.writeCapacity(section);
            this.addCapacity(capacityUpgrade.getIndentifier(), capacityUpgrade);
        }
    }

    //capacity
    public void addCapacity(int indentifer, CapacityUpgrade capacityUpgrade) {
        if(!upgradeCapacity.containsKey(indentifer)) {
            this.upgradeCapacity.put(indentifer, capacityUpgrade);
        }
    }

    public CapacityUpgrade getCapacity(int indentifier) {
        if(upgradeCapacity.containsKey(indentifier)) {
            return upgradeCapacity.get(indentifier);
        }
        return null;
    }

    public CapacityUpgrade getNextCapacity(int indentifier) {
        if(upgradeCapacity.containsKey(indentifier+1)) {
            return upgradeCapacity.get(indentifier+1);
        }
        return null;
    }

    public boolean hasNextCapacity(int indentifier) {
        return this.upgradeCapacity.containsKey(indentifier+1);
    }
    //xp
    public void addXP(int indentifer, XpUpgrade xpUpgrade) {
        if(!upgradeXP.containsKey(indentifer)) {
            this.upgradeXP.put(indentifer, xpUpgrade);
        }
    }

    public XpUpgrade getXP(int indentifier) {
        if(upgradeXP.containsKey(indentifier)) {
            return upgradeXP.get(indentifier);
        }
        return null;
    }

    public XpUpgrade getNextXP(int indentifier) {
        if(upgradeXP.containsKey(indentifier+1)) {
            return upgradeXP.get(indentifier+1);
        }
        return null;
    }

    public boolean hasNextXP(int indentifier) {
        return this.upgradeXP.containsKey(indentifier+1);
    }

    //time
    public void addTime(int indentifer, TimeUpgrade time) {
        if(!upgradeTime.containsKey(indentifer)) {
            this.upgradeTime.put(indentifer, time);
        }
    }

    public TimeUpgrade getTime(int indentifier) {
        if(upgradeTime.containsKey(indentifier)) {
            return upgradeTime.get(indentifier);
        }
        return null;
    }

    public TimeUpgrade getNext(int indentifier) {
        if(upgradeTime.containsKey(indentifier+1)) {
            return upgradeTime.get(indentifier+1);
        }
        return null;
    }

    public boolean hasNextTime(int indentifier) {
        return this.upgradeTime.containsKey(indentifier+1);
    }

}
