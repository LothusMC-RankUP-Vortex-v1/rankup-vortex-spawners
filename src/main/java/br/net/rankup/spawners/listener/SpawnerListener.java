package br.net.rankup.spawners.listener;

import br.net.rankup.booster.api.BoosterAPI;
import br.net.rankup.booster.models.Account;
import br.net.rankup.booster.type.BoosterType;
import br.net.rankup.logger.LogPlugin;
import br.net.rankup.spawners.Constants;
import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.drops.models.drop.DropModel;
import br.net.rankup.spawners.inventory.DefaultInventory;
import br.net.rankup.spawners.manager.SpawnerManager;
import br.net.rankup.spawners.misc.*;
import br.net.rankup.spawners.model.reward.RewardModel;
import br.net.rankup.spawners.model.spawner.SettingsModel;
import br.net.rankup.spawners.model.spawner.SpawnerModel;
import br.net.rankup.spawners.model.spawner.UpgradeModel;
import br.net.rankup.spawners.model.upgrade.CapacityUpgrade;
import br.net.rankup.spawners.model.upgrade.TimeUpgrade;
import br.net.rankup.spawners.model.upgrade.XpUpgrade;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class SpawnerListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!event.getAction().toString().contains("RIGHT")) return;
           Block block = event.getClickedBlock();
           if(block != null && block.getType() != Material.AIR) {
               SpawnerModel spawnerModel = SpawnerPlugin.getInstance().getSpawnerManager().getSpawners().get(block.getLocation());
               if(spawnerModel != null) {
                   if(!spawnerModel.getOwner().contains(player.getName()) && !spawnerModel.getFriends().contains(player.getName())) {
                       BukkitUtils.sendMessage(player, "&cVocê não tem permissão para interagir com esse gerador.");
                       player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5.0f, 1.0f);
                       return;
                   }

                       if(!InventoryUtils.getList().contains(player.getName())) {
                           RyseInventory inventory = new DefaultInventory(spawnerModel).build();
                           inventory.open(player);
                           InventoryUtils.addDelay(player);
                           player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 5.0f);
                       }
               }
       }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        SpawnerModel spawnerModel = SpawnerPlugin.getInstance().getSpawnerManager().getSpawners().get(block.getLocation());
        if (spawnerModel != null) {
            event.setCancelled(true);
            if(spawnerModel == null) {
                BukkitUtils.sendMessage(player, "&cEsse gerador não está funcionando.");
                return;
            }
            if(!spawnerModel.getOwner().equalsIgnoreCase(player.getName()) && !spawnerModel.getFriends().contains(player.getName())
                    && !player.hasPermission("spawners.admin")) {
                BukkitUtils.sendMessage(player, "&cVocê não tem permissão para gerenciar esse gerador.");
                event.setCancelled(true);
                return;
            }

            ItemStack itemStack = SpawnerPlugin.getInstance().getSpawnerManager().getItemStack(spawnerModel.getType(), spawnerModel.getAmount(), spawnerModel.getUpgradeModel());
            spawnerModel.remove();

            spawnerModel.getLocation().getWorld().getNearbyEntities(spawnerModel.getLocation(), 2, 2, 2)
                    .stream()
                    .filter(entity -> entity.getType() == spawnerModel.getType() && entity.hasMetadata("spawner_amount") && entity.hasMetadata("spawner_location"))
                    .forEach(entity -> { entity.remove(); });

            block.setType(Material.AIR);
            player.getInventory().addItem(itemStack);
            LogPlugin.getInstance().getLogManager().registerSpawner(player, "SPAWNER_REMOVED", spawnerModel.getType().toString(), spawnerModel.getAmount());
            BukkitUtils.sendMessage(player, "&aYAY! Você removeu os geradores com êxito.");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = event.getItemInHand();
        Block block = event.getBlock();
        if(event.isCancelled()) return;
        if(itemInHand != null && itemInHand.getType() != Material.AIR) {
            if(BukkitUtils.hasNBT(itemInHand, "spawner_type")) {

                if(!player.getWorld().getName().equalsIgnoreCase("plotworld")) {
                    BukkitUtils.sendMessage(player, "&cVocê só pode colocar um gerador no mundo dos tereenos.");
                    event.setCancelled(true);
                    return;
                }

                net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemInHand);
                NBTTagCompound itemCompound = nmsItem.getTag();

                EntityType entityType = EntityType.fromName(itemCompound.getString("spawner_type"));
                Double amount = itemCompound.getDouble("spawner_amount");
                int timeIndentifier = itemCompound.getInt("upgrade_time");
                int xpIndentifier = itemCompound.getInt("upgrade_xp");
                int capacityIndentifier = itemCompound.getInt("upgrade_capacity");
                double amountInHand = itemInHand.getAmount()*amount;

                TimeUpgrade timeUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getTime(timeIndentifier);
                XpUpgrade xpUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getXP(xpIndentifier);
                CapacityUpgrade capacityUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getCapacity(capacityIndentifier);

                if(getNearSpawners(block.getLocation(), 5, entityType).size() >= 1) {
                    for(Block block1 : getNearSpawners(block.getLocation(), 5, entityType)) {
                        SpawnerModel spawnerModel = SpawnerPlugin.getInstance().getSpawnerManager().getSpawners().get(block1.getLocation());
                        if(!spawnerModel.getOwner().equalsIgnoreCase(player.getName()) && !spawnerModel.getFriends().contains(player.getName())
                        && !player.hasPermission("spawners.admin")) {
                            BukkitUtils.sendMessage(player, "&cVocê não tem permissão para gerenciar esse gerador.");
                            return;
                        }

                        double capacity = spawnerModel.getUpgradeModel().getCapacityUpgrade().getValue();
                        double leftAmount = 0;

                        if((amountInHand+spawnerModel.getAmount()) > capacity) {
                            leftAmount = amountInHand-capacity;
                            amountInHand = capacity;
                            BukkitUtils.sendMessage(player, "&cA capacidade desse gerador foi atingida, evolua para comportar mais.");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5.0f, 1.0f);
                            event.setCancelled(true);
                            return;
                        }

                        spawnerModel.addAmount(amountInHand);
                        player.setItemInHand(null);
                        BukkitUtils.sendMessage(player, "&eVocê adicionou &fx{amount} &egeradores de {type}&e."
                                .replace("{type}", TranslateMob.traslateName(entityType.toString()))
                                .replace("{amount}", Toolchain.format(amountInHand)));
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 1.0f);
                        SpawnerPlugin.getInstance().getSpawnerRepository().update(spawnerModel);
                        LogPlugin.getInstance().getLogManager().registerSpawner(player, "SPAWNER_ADDED_WITH_SPAWNER", spawnerModel.getType().toString(), spawnerModel.getAmount());
                        event.setCancelled(true);
                    }
                } else {
                    SettingsModel settingsModel = new SettingsModel(true, true);
                    Hologram hologram = HologramsAPI.createHologram(SpawnerPlugin.getInstance(),
                            block.getLocation().clone().add(0.5, Constants.hologramHeight, 0.5));
                    for (final String line : Constants.lines) {
                        hologram.appendTextLine(line
                                .replace("{type}", TranslateMob.traslateName(entityType.toString()))
                                .replace("{status}", "§eCarregando...")
                                .replace("{amount}", Toolchain.format(amount))
                                .replace("{bar}", ProgressBar.progressBar(0, timeUpgrade.getValue(), "▎"))
                                .replace("{tempo}", TimeFormat.formatTime((int) (timeUpgrade.getValue() - 0)))
                                .replace('&', '§'));
                    }
                    hologram.setAllowPlaceholders(true);

                    UpgradeModel upgradeModel = new UpgradeModel(timeUpgrade, xpUpgrade, capacityUpgrade);

                    double capacity = upgradeModel.getCapacityUpgrade().getValue();
                    double leftAmount = 0;

                    if(amountInHand > capacity) {
                        leftAmount = amountInHand-capacity;
                        amountInHand = capacity;
                        BukkitUtils.sendMessage(player, "&cEsse gerador está agora com sua capacidade máxima.");
                        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5.0f, 1.0f);
                    }

                    SpawnerModel spawnerModel = new SpawnerModel(hologram, block.getLocation(), player.getName(), new ArrayList<>(), entityType, amountInHand, upgradeModel, settingsModel, 0);
                    SpawnerPlugin.getInstance().getSpawnerManager().add(spawnerModel);
                    SpawnerPlugin.getInstance().getSpawnerRepository().update(spawnerModel);

                    LogPlugin.getInstance().getLogManager().registerSpawner(player, "SPAWNER_ADD", spawnerModel.getType().toString(), spawnerModel.getAmount());
                    BukkitUtils.sendMessage(player, "&aYAY! Você colocou seu gerador!");
                    SpawnerPlugin.getInstance().getSpawnerRepository().update(spawnerModel);

                    //set block with metadate
                    block.setMetadata("spawner_type", new FixedMetadataValue(SpawnerPlugin.getInstance(), entityType.getName()));

                    //set block with mob
                    BlockState blockState = block.getState();
                    CreatureSpawner spawner = ((CreatureSpawner) blockState);
                    spawner.setSpawnedType(entityType);
                    spawner.update();
                    blockState.update();

                    player.setItemInHand(null);
                    if(leftAmount > 0) {
                        TimeUpgrade timeUpgradeNew = SpawnerPlugin.getInstance().getUpgradeManager().getTime(0);
                        XpUpgrade xpUpgradeNew = SpawnerPlugin.getInstance().getUpgradeManager().getXP(0);
                        CapacityUpgrade capacityUpgradeNew = SpawnerPlugin.getInstance().getUpgradeManager().getCapacity(0);
                        UpgradeModel upgradeModelNew = new UpgradeModel(timeUpgradeNew, xpUpgradeNew, capacityUpgradeNew);
                        player.getInventory().addItem(SpawnerPlugin.getInstance().getSpawnerManager().getItemStack(spawnerModel.getType(), leftAmount, upgradeModelNew));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSpawn(SpawnerSpawnEvent event) {
        CreatureSpawner creatureSpawner = event.getSpawner();
        if(creatureSpawner.getBlock().hasMetadata("spawner_type")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) return;
        Entity entity = event.getEntity();
        Player player = (Player) event.getDamager();

        if(entity.hasMetadata("spawner_location")) {
            double stackMobs = entity.getMetadata("spawner_amount").get(0).asDouble();
            Location location = BukkitUtils.deserializeLocation(entity.getMetadata("spawner_location").get(0).asString());

            if(location == null) return;

            SpawnerModel spawnerModel = SpawnerPlugin.getInstance().getSpawnerManager().getSpawners().get(location);
            if(spawnerModel == null) {
                BukkitUtils.sendMessage(player, "&cNão foi possivel encontrar o gerador desse mob.");
                return;
            }

            if(!spawnerModel.getOwner().equalsIgnoreCase(player.getName()) && !spawnerModel.getFriends().contains(player.getName())
                    && !player.hasPermission("spawners.admin")) {
                BukkitUtils.sendMessage(player, "&cVocê não tem permissão para matar esse mob.");
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(!(event.getEntity().getKiller() instanceof Player)) return;

        Entity entity = event.getEntity();

        if(entity.hasMetadata("spawner_location")) {
            event.getDrops().clear();
            Player player = (Player) event.getEntity().getKiller();
            double stackMobs = entity.getMetadata("spawner_amount").get(0).asDouble();
            Location location = BukkitUtils.deserializeLocation(entity.getMetadata("spawner_location").get(0).asString());

            if(location == null) return;

            SpawnerModel spawnerModel = SpawnerPlugin.getInstance().getSpawnerManager().getSpawners().get(location);
            if(spawnerModel == null) {
                BukkitUtils.sendMessage(player, "&cNão foi possivel encontrar o gerador desse mob.");
                return;
            }

            DropModel dropModel = SpawnerPlugin.getInstance().getDropManager().getDrops().get(entity.getType().toString());

            if(dropModel == null) {
                BukkitUtils.sendMessage(player, "&cO drop desse gerador não está configurado.");
                return;
            }

            String actionBar = "§aVocê recebeu §2$§a{coins} coins";

            double fortune = 1;
            if(player.getItemInHand().getEnchantments().containsKey(Enchantment.LOOT_BONUS_MOBS)) {
                fortune = player.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
            }

            double xp = event.getDroppedExp()*5;
            double drops = stackMobs * fortune;
            double coins = dropModel.getPrice();

            Account account = BoosterAPI.getAccount(player);
            if(account != null) {
                if(account.getType().equals(BoosterType.COINS)) {
                    actionBar = (actionBar+" §f§l| §aBooster de Coins §7("+account.getBonus()+"x)");
                    coins *= account.getBonus();
                }
                if(account.getType().equals(BoosterType.DROPS)) {
                    actionBar = (actionBar+" §f§l| §9Booster de Drops §7("+account.getBonus()+"x)");
                    drops = account.getBonus();
                }
            }

            coins *= drops;

            if(!SpawnerPlugin.getInstance().getBonusController().getBonusMessage(player).equalsIgnoreCase("")) {
                coins = SpawnerPlugin.getInstance().getBonusController().applyGroupBonus(player, coins);
                actionBar = (actionBar + SpawnerPlugin.getInstance().getBonusController().getBonusMessage(player));
            }

            for (final RewardModel reward : Constants.spawnersRewards) {
                final double random2 = Math.random() * 100.0;
                final double chance2 = reward.getChance();
                if (random2 < chance2) {
                    Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), reward.getCommand().replace("<player>", player.getName()));
                    BukkitUtils.sendActionBar("§b§lRECOMPENSA §eVocê recebeu " + reward.getFriendlyName().replace("&", "§"), player);
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 5.0f);
                }
            }


            BukkitUtils.sendActionBar(actionBar.replace("{coins}", Toolchain.format(coins)), player);
            SpawnerPlugin.getEconomy().depositPlayer(player.getName(), coins);
            event.setDroppedExp((int) ((xp) * spawnerModel.getUpgradeModel().getXpUpgrade().getValue()));
        }
    }

    public static List<Block> getNearSpawners(Location location, int radius, EntityType entityType) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = location.clone().add(x, y, z).getBlock();
                    if(SpawnerPlugin.getInstance().getSpawnerManager().getSpawners().containsKey(block.getLocation())) {
                            EntityType entityTypeBlock = EntityType.fromName(block.getMetadata("spawner_type").get(0).asString());
                            if (entityType != null) {
                                if (entityType.equals(entityTypeBlock)) {
                                    blocks.add(block);
                                }
                            }
                    }
                }
            }
        }
        return blocks;
    }
}
