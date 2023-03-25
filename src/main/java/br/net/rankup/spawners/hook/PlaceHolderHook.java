package br.net.rankup.spawners.hook;

import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.misc.Toolchain;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceHolderHook extends PlaceholderExpansion {

    private final SpawnerPlugin plugin;

    public PlaceHolderHook(SpawnerPlugin plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return this.plugin.getName();
    }

    public String getIdentifier() {
        return "spawners";
    }

    public String getAuthor() {
        return "zRomaGod_";
    }

    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.equalsIgnoreCase("limite")) {
        if(SpawnerPlugin.getInstance().getUserManager().getUsers().containsKey(player.getName())) {
            return Toolchain.format(SpawnerPlugin.getInstance().getUserManager().get(player.getName()).getSpawnerLimite());
        }
            return "0";
        }

        if (params.equalsIgnoreCase("stackmob")) {
            if(SpawnerPlugin.getInstance().getUserManager().getUsers().containsKey(player.getName())) {
                return Toolchain.format(SpawnerPlugin.getInstance().getUserManager().get(player.getName()).getStackLimite());
            }
            return "0";
        }
        
        return "Placeholder inválida";
    }

}
