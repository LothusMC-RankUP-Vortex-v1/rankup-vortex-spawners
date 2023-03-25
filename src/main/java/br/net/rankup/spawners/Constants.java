package br.net.rankup.spawners;

import br.net.rankup.spawners.model.reward.RewardModel;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static List<String> lines;
    public static double hologramHeight;
    public static List<String> statusLore;
    public static List<RewardModel> spawnersRewards;
    public static int radius = 5;

    public static void loadRewards(final SpawnerPlugin plugin) {
        spawnersRewards = new ArrayList<>();
        for (final String key : plugin.getConfig().getConfigurationSection("spawners-rewards").getKeys(false)) {
            final String prefix = "spawners-rewards." + key + ".";
            final String friendlyName = plugin.getConfig().getString(prefix + "friendly-name");
            final String command = plugin.getConfig().getString(prefix + "command");
            final double chance = plugin.getConfig().getDouble(prefix + "chance");
            Constants.spawnersRewards.add(new RewardModel(friendlyName, command, chance));
        }
    }

    static {
        Constants.lines = SpawnerPlugin.getConfiguration().getStringList("Hologram.line");
        Constants.statusLore = SpawnerPlugin.getConfiguration().getStringList("Hologram.status");
        Constants.hologramHeight = SpawnerPlugin.getConfiguration().getDouble("Hologram.hologram-height");
    }
}
