package br.net.rankup.spawners.drops.manager;

import br.net.rankup.booster.misc.BukkitUtils;
import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.drops.models.drop.DropModel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class DropManager {

    private static HashMap<String, DropModel> drops;

    public void loadAll() {
        int amount = 0;
        drops = new HashMap<>();
        for (final String name : SpawnerPlugin.getConfiguration().getConfigurationSection("drops").getKeys(false)) {
            final ConfigurationSection section = SpawnerPlugin.getConfiguration().getConfigurationSection("drops." + name);
            Double priceCoins = section.getDouble("price");
            String indentifier = section.getString("indentifier");

            DropModel dropModel = new DropModel(priceCoins);
            drops.put(indentifier, dropModel);
            amount++;
        }
        BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&fCarregadas {int} drops em {time} ms."
                .replace("{time}",""+(System.currentTimeMillis() - SpawnerPlugin.getStart()))
                .replace("{int}", amount+""));
    }

    public static HashMap<String, DropModel> getDrops() {
        return drops;
    }
}
