package br.net.rankup.spawners.manager;

import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.adpter.UpgradeAdpter;
import br.net.rankup.spawners.misc.BukkitUtils;
import br.net.rankup.spawners.misc.TimeFormat;
import br.net.rankup.spawners.model.shop.SpawnerShop;
import br.net.rankup.spawners.model.spawner.SpawnerModel;
import br.net.rankup.spawners.model.upgrade.CapacityUpgrade;
import br.net.rankup.spawners.model.upgrade.TimeUpgrade;
import br.net.rankup.spawners.model.upgrade.XpUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ShopManager {

    private List<SpawnerShop> spawners;

    public List<SpawnerShop> getSpawners() {
        return spawners;
    }

    public void loadAll() {
        spawners = new ArrayList<>();
        for (final String name : SpawnerPlugin.getConfiguration().getConfigurationSection("spawnershop").getKeys(false)) {
            final ConfigurationSection section = SpawnerPlugin.getConfiguration().getConfigurationSection("spawnershop." + name);

            String entity = section.getString("entity");
            double price = section.getDouble("price");
            String time = section.getString("time");
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
            long timeLong = 0;
            try {
                timeLong = simpleDateFormat.parse(time).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SpawnerShop spawnerShop = new SpawnerShop(entity, price, timeLong);
            this.getSpawners().add(spawnerShop);
        }
    }

}
