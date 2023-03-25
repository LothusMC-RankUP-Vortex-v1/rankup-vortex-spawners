package br.net.rankup.spawners.listener;

import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.model.spawner.SpawnerModel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatListener implements Listener {

    private static HashMap<String, SpawnerModel> players;

    public ChatListener() {
        players = new HashMap<>();
    }


    @EventHandler
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final String message = event.getMessage();
        if (players.containsKey(player.getName())) {
            if (message.equalsIgnoreCase("cancelar")) {
                player.sendMessage("§aAção cancelada com sucesso.");
                players.remove(player.getName());
                event.setCancelled(true);
            }
            else {
                final Player target = Bukkit.getPlayer(message);
                if (target == null) {
                    player.sendMessage("§cEste jogador não está online.");
                    event.setCancelled(true);
                    return;
                }
                final SpawnerModel spawnerModel = players.get(player.getName());
                if (spawnerModel == null) {
                    return;
                }
                if (spawnerModel.getFriends().contains(target.getName())) {
                    player.sendMessage("§cEste jogador já está adicionado á esse gerador.");
                    event.setCancelled(true);
                    return;
                }
                player.sendMessage("§aAmigo adicionado com sucesso ao gerador.");
                spawnerModel.getFriends().add(target.getName());
                SpawnerPlugin.getInstance().getSpawnerRepository().update(spawnerModel);
                players.remove(player.getName());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(players.containsKey(event.getPlayer().getName())) {
            players.remove(event.getPlayer().getName());
        }
     }

    public static HashMap<String, SpawnerModel> getPlayers() {
        return players;
    }
}
