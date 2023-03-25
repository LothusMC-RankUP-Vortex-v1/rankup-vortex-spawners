package br.net.rankup.spawners.listener;

import br.net.rankup.logger.LogPlugin;
import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.misc.BukkitUtils;
import br.net.rankup.spawners.model.user.UserModel;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class UsableItemListener implements Listener {

    @EventHandler
    public void oninteractLimite(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        if (itemStack != null && itemStack.getType() != Material.AIR && itemStack.getItemMeta().hasDisplayName()) {
            net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
            NBTTagCompound itemCompound = nmsItem.getTag();
            if (!event.getAction().toString().contains("RIGHT")) return;
            if (!BukkitUtils.hasNBT(itemStack, "limite_amount")) return;
            if(!itemStack.getItemMeta().getDisplayName().contains("Limite de ")) return;
            event.setCancelled(true);
            UserModel userModel = SpawnerPlugin.getInstance().getUserManager().get(player.getName());

            if(userModel == null) {
                BukkitUtils.sendMessage(player, "&cSeu usuário está sendo carregado.");
                return;
            }
            double inHandAmount = player.getItemInHand().getAmount();

            double amount = itemCompound.getDouble("limite_amount")*inHandAmount;
            player.setItemInHand(null);
            userModel.setSpawnerLimite(userModel.getSpawnerLimite()+amount);
            LogPlugin.getInstance().getLogManager().registerEconomy(player, "SPAWNER_LIMITE_USED", amount);
            BukkitUtils.sendMessage(player, "&aYAY! Você ativou um limite de compra!");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 1.0f);
        }
    }


    @EventHandler
    public void oninteractStack(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        if (itemStack != null && itemStack.getType() != Material.AIR && itemStack.getItemMeta().hasDisplayName()) {
            net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
            NBTTagCompound itemCompound = nmsItem.getTag();
            if (!event.getAction().toString().contains("RIGHT")) return;
            if (!BukkitUtils.hasNBT(itemStack, "stackmob_amount")) return;
            if(!itemStack.getItemMeta().getDisplayName().contains("Stack ")) return;
            event.setCancelled(true);
            UserModel userModel = SpawnerPlugin.getInstance().getUserManager().get(player.getName());

            if(userModel == null) {
                BukkitUtils.sendMessage(player, "&cSeu usuário está sendo carregado.");
                return;
            }

            double inHandAmount = player.getItemInHand().getAmount();
            double amount = itemCompound.getDouble("stackmob_amount")*inHandAmount;
            player.setItemInHand(null);
            userModel.setStackLimite(userModel.getStackLimite()+amount);
            LogPlugin.getInstance().getLogManager().registerEconomy(player, "SPAWNER_STACKMOB_USED", amount);
            BukkitUtils.sendMessage(player, "&aYAY! Você ativou um limite de stackmob!");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 1.0f);
        }
    }

}
