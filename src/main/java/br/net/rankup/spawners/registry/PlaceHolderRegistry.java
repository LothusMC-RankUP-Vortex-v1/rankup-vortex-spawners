package br.net.rankup.spawners.registry;

import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.hook.PlaceHolderHook;
import br.net.rankup.spawners.misc.BukkitUtils;
import org.bukkit.Bukkit;

public final class PlaceHolderRegistry {

    public static void init() {
        if (!SpawnerPlugin.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
        	BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&cPlaceholderAPI não foi encontrado no servidor.");
            return;
        }
        BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&aPalaceholder registrado com sucesso.");
        new PlaceHolderHook(SpawnerPlugin.getInstance()).register();
    }

}
