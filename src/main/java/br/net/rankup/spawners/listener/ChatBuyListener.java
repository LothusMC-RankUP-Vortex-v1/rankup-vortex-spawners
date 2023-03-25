package br.net.rankup.spawners.listener;

import br.net.rankup.logger.LogPlugin;
import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.inventory.SpawnerShopInventory;
import br.net.rankup.spawners.misc.BukkitUtils;
import br.net.rankup.spawners.model.shop.SpawnerShop;
import br.net.rankup.spawners.model.spawner.SpawnerModel;
import br.net.rankup.spawners.model.spawner.UpgradeModel;
import br.net.rankup.spawners.model.upgrade.CapacityUpgrade;
import br.net.rankup.spawners.model.upgrade.TimeUpgrade;
import br.net.rankup.spawners.model.upgrade.XpUpgrade;
import br.net.rankup.spawners.model.user.UserModel;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public class ChatBuyListener implements Listener {

    private static HashMap<String, SpawnerShop> players;

    public ChatBuyListener() {
        players = new HashMap<>();
    }


    public static HashMap<String, SpawnerShop> getPlayers() {
        return players;
    }

    @EventHandler
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final String message = event.getMessage();
        if (players.containsKey(player.getName())) {
            UserModel userModel = SpawnerPlugin.getInstance().getUserManager().get(player.getName());
            SpawnerShop spawnerShop = players.get(player.getName());
            if (message.equalsIgnoreCase("cancelar")) {
                player.sendMessage("§aAção cancelada com sucesso.");
                players.remove(player.getName());
                event.setCancelled(true);
            }
            else {
                double amount;
                try {
                    amount = Double.parseDouble(message);
                } catch (Exception var10) {
                    player.sendMessage("§cInsira um número válido.");
                    event.setCancelled(true);
                    return;
                }
                if (amount < 1.0D) {
                    player.sendMessage("§cInsira um número válido.");
                    event.setCancelled(true);
                    return;
                } else if (userModel.getSpawnerLimite() < amount) {
                    player.sendMessage("§cVocê não possui Limite de Compa suficiente para concluir sua compra!");
                    event.setCancelled(true);
                    return;
                }
                double price = SpawnerPlugin.getInstance().getBonusController().applyGroupDescont(player, spawnerShop.getPrice()*amount);
                if(SpawnerPlugin.getEconomy().getBalance(player.getName()) < price) {
                    player.closeInventory();
                    BukkitUtils.sendMessage(player, "&cVocê não tem dinheiro suficiente para adquirir esse gerador!");
                    return;
                }

                EntityType entityType = EntityType.valueOf(spawnerShop.getEntity());

                TimeUpgrade timeUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getTime(0);
                XpUpgrade xpUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getXP(0);
                CapacityUpgrade capacityUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getCapacity(0);
                UpgradeModel upgradeModel = new UpgradeModel(timeUpgrade, xpUpgrade, capacityUpgrade);
                ItemStack itemStack = SpawnerPlugin.getInstance().getSpawnerManager().getItemStack(entityType, amount, upgradeModel);

                RyseInventory ryseInventory = new SpawnerShopInventory().build();
                ryseInventory.open(player);

                BukkitUtils.sendMessage(player, "&aGerador(es) adquirido(s) com sucesso!");
                SpawnerPlugin.getEconomy().withdrawPlayer(player.getName(), price);
                LogPlugin.getInstance().getLogManager().registerEconomy(player, "SPAWNER_BUY", price);
                LogPlugin.getInstance().getLogManager().registerSpawner(player, "SPAWNER_BUY", spawnerShop.getEntity(), price);

                if (player.getInventory().firstEmpty() == -1) {
                    player.getLocation().getWorld().dropItemNaturally(player.getLocation(), itemStack);
                } else {
                    player.getInventory().addItem(itemStack);
                }

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

}
