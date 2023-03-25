package br.net.rankup.spawners.listener;

import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.model.user.UserModel;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class UserListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UserModel userModel = SpawnerPlugin.getInstance().getUserManager().get(player.getName());
        if (userModel != null) {
            SpawnerPlugin.getInstance().getUsersRepository().update(userModel);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!SpawnerPlugin.getInstance().getUserManager().getUsers().containsKey(player.getName())) {
                UserModel userModel = new UserModel(player.getName(), 10, 10);
                SpawnerPlugin.getInstance().getUserManager().add(userModel);
        }
    }

}
