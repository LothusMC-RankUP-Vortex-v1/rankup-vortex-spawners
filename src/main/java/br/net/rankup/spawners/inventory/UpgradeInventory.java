package br.net.rankup.spawners.inventory;

import br.net.rankup.logger.LogPlugin;
import br.net.rankup.spawners.Constants;
import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.misc.*;
import br.net.rankup.spawners.model.spawner.SettingsModel;
import br.net.rankup.spawners.model.spawner.SpawnerModel;
import br.net.rankup.spawners.model.upgrade.CapacityUpgrade;
import br.net.rankup.spawners.model.upgrade.TimeUpgrade;
import br.net.rankup.spawners.model.upgrade.XpUpgrade;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Time;

public class UpgradeInventory implements InventoryProvider {

    private SpawnerModel spawnerModel;

    public UpgradeInventory(SpawnerModel spawnerModel) {
        this.spawnerModel = spawnerModel;
    }

    public RyseInventory build() {
        return RyseInventory.builder()
                .title("Gerador - Upgrades".replace("&", "§"))
                .rows(3)
                .provider(this)
                .disableUpdateTask()
                .build(SpawnerPlugin.getInstance());
    }

    @Override
    public void init(Player player, InventoryContents contents) {


        //time upgrade
        TimeUpgrade timeUpgrade = spawnerModel.getUpgradeModel().getTimeUpgrade();
        if (timeUpgrade != null) {

            ItemBuilder itemBuilderTimeNoHas = new ItemBuilder(Material.getMaterial(381))
                    .setName("§eEvoluir Velocidade")
                    .addLoreLine("§7Nível máximo atingido.");

            ItemStack itemStack = itemBuilderTimeNoHas.build();

            if (SpawnerPlugin.getInstance().getUpgradeManager().hasNextTime(timeUpgrade.getIndentifier())) {

                TimeUpgrade timeUpgradeNext = SpawnerPlugin.getInstance().getUpgradeManager().getNext(timeUpgrade.getIndentifier());

                String hasMoney = SpawnerPlugin.getEconomy().has(player.getName(), timeUpgradeNext.getPrice()) ? "§eClique para evoluir" : "§cSem dinheiro para evoluir.";

                ItemBuilder itemBuilderTimeHas = new ItemBuilder(Material.getMaterial(381))
                        .setName("§eEvoluir Velocidade")
                        .addLoreLine("§7Esta evolução faz com que")
                        .addLoreLine("§7os mobs geradores sejam")
                        .addLoreLine("§7gerados mais rapidamente.")
                        .addLoreLine("")
                        .addLoreLine(" §fNível: §7{indentifier} §8({time}s) §f➟ §7{next-indentifier} §8({next-time}s)"
                                .replace("{next-indentifier}", "" + timeUpgradeNext.getIndentifier())
                                .replace("{next-time}", "" + timeUpgradeNext.getValue())
                                .replace("{indentifier}", "" + timeUpgrade.getIndentifier())
                                .replace("{time}", "" + timeUpgrade.getValue()))
                        .addLoreLine(" §fCusto: §2$§a{coins}".replace("{coins}", Toolchain.format(timeUpgradeNext.getPrice())))
                        .addLoreLine("")
                        .addLoreLine(hasMoney);
                itemStack = itemBuilderTimeHas.build();
            }

            IntelligentItem intelligentActived = IntelligentItem.of(itemStack, event -> {
                player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);

                if (!SpawnerPlugin.getInstance().getUpgradeManager().hasNextTime(timeUpgrade.getIndentifier())) {
                    BukkitUtils.sendMessage(player, "&cVocê já está no ultima evolução.");
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5.0f, 1.0f);
                    player.closeInventory();
                    return;
                }

                TimeUpgrade timeUpgradeNext = SpawnerPlugin.getInstance().getUpgradeManager().getNext(timeUpgrade.getIndentifier());

                if (!SpawnerPlugin.getEconomy().has(player.getName(), timeUpgradeNext.getPrice())) {
                    BukkitUtils.sendMessage(player, "&cVocê não tem dinheiro suficiente para evoluir.");
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5.0f, 1.0f);
                    player.closeInventory();
                    return;
                }

                spawnerModel.getUpgradeModel().setTimeUpgrade(timeUpgradeNext);
                spawnerModel.setTime(0);
                SpawnerPlugin.getEconomy().withdrawPlayer(player.getName(), timeUpgradeNext.getPrice());
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 1.0f);
                BukkitUtils.sendMessage(player, "&aYAY! Evolução adquirida com sucesso.");
                LogPlugin.getInstance().getLogManager().registerSpawner(player, "SPAWNER_UPGRADE", "TIME", timeUpgradeNext.getPrice());
                SpawnerPlugin.getInstance().getSpawnerRepository().update(spawnerModel);

                reOpenInventory(player, spawnerModel);
            });
            contents.set(11, intelligentActived);
        }

        //xp upgrade
        XpUpgrade xpUpgrade = spawnerModel.getUpgradeModel().getXpUpgrade();
        if(xpUpgrade != null) {

            ItemBuilder itemBuilderXpNoHas = new ItemBuilder(Material.EXP_BOTTLE)
                    .setName("§eEvoluir XP")
                    .addLoreLine("§7Nível máximo atingido.");

            ItemStack itemStackXp = itemBuilderXpNoHas.build();

            if (SpawnerPlugin.getInstance().getUpgradeManager().hasNextXP(xpUpgrade.getIndentifier())) {

                XpUpgrade xpUpgradeNext = SpawnerPlugin.getInstance().getUpgradeManager().getNextXP(xpUpgrade.getIndentifier());

                String hasMoneyXp = SpawnerPlugin.getEconomy().has(player.getName(), xpUpgradeNext.getPrice()) ? "§eClique para evoluir" : "§cSem dinheiro para evoluir.";

                ItemBuilder itemBuilderXPHas = new ItemBuilder(Material.EXP_BOTTLE)
                        .setName("§eEvoluir XP")
                        .addLoreLine("§7Esta evolução faz com que")
                        .addLoreLine("§7o xp ao matar um mob seja")
                        .addLoreLine("§7multiplicado ganhando mais.")
                        .addLoreLine("")
                        .addLoreLine(" §fNível: §7{indentifier} §8({multiplier}x) §f➟ §7{next-indentifier} §8({next-multiplier}x)"
                                .replace("{next-indentifier}", "" + xpUpgradeNext.getIndentifier())
                                .replace("{next-multiplier}", "" + xpUpgradeNext.getValue())
                                .replace("{indentifier}", "" + xpUpgrade.getIndentifier())
                                .replace("{multiplier}", "" + xpUpgrade.getValue()))
                        .addLoreLine(" §fCusto: §2$§a{coins}".replace("{coins}", Toolchain.format(xpUpgradeNext.getPrice())))
                        .addLoreLine("")
                        .addLoreLine(hasMoneyXp);
                itemStackXp = itemBuilderXPHas.build();
            }

            IntelligentItem intelligent = IntelligentItem.of(itemStackXp, event -> {
                player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);

                if (!SpawnerPlugin.getInstance().getUpgradeManager().hasNextXP(xpUpgrade.getIndentifier())) {
                    BukkitUtils.sendMessage(player, "&cVocê já está no ultima evolução.");
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5.0f, 1.0f);
                    player.closeInventory();
                    return;
                }

                XpUpgrade xpUpgradeNext = SpawnerPlugin.getInstance().getUpgradeManager().getNextXP(xpUpgrade.getIndentifier());

                if (!SpawnerPlugin.getEconomy().has(player.getName(), xpUpgradeNext.getPrice())) {
                    BukkitUtils.sendMessage(player, "&cVocê não tem dinheiro suficiente para evoluir.");
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5.0f, 1.0f);
                    player.closeInventory();
                    return;
                }

                spawnerModel.getUpgradeModel().setXpUpgrade(xpUpgradeNext);
                SpawnerPlugin.getEconomy().withdrawPlayer(player.getName(), xpUpgradeNext.getPrice());
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 1.0f);
                LogPlugin.getInstance().getLogManager().registerSpawner(player, "SPAWNER_UPGRADE", "XP", xpUpgrade.getPrice());
                BukkitUtils.sendMessage(player, "&aYAY! Evolução adquirida com sucesso.");
                SpawnerPlugin.getInstance().getSpawnerRepository().update(spawnerModel);

                reOpenInventory(player, spawnerModel);
            });
            contents.set(13, intelligent);
        }

        //capacity upgrade
        CapacityUpgrade capacityUpgrade = spawnerModel.getUpgradeModel().getCapacityUpgrade();
        if(capacityUpgrade != null) {

            ItemBuilder itemBuilderNoHas = new ItemBuilder(Material.HOPPER)
                    .setName("§eEvoluir Capacidade")
                    .addLoreLine("§7Nível máximo atingido.");

            ItemStack itemStack = itemBuilderNoHas.build();

            if (SpawnerPlugin.getInstance().getUpgradeManager().hasNextCapacity(capacityUpgrade.getIndentifier())) {

                CapacityUpgrade capacityUpgradeNext = SpawnerPlugin.getInstance().getUpgradeManager().getNextCapacity(capacityUpgrade.getIndentifier());

                String hasMoney = SpawnerPlugin.getEconomy().has(player.getName(), capacityUpgradeNext.getPrice()) ? "§eClique para evoluir" : "§cSem dinheiro para evoluir.";

                ItemBuilder itemBuilderHas = new ItemBuilder(Material.HOPPER)
                        .setName("§eEvoluir Capacidade")
                        .addLoreLine("§7Esta evolução faz com que")
                        .addLoreLine("§7você poda armazenar mais")
                        .addLoreLine("§7geradores no mesmo gerador.")
                        .addLoreLine("")
                        .addLoreLine(" §fNível: §7{indentifier} §8({capacity}x) §f➟ §7{next-indentifier} §8({next-capacity}x)"
                                .replace("{next-indentifier}", "" + capacityUpgradeNext.getIndentifier())
                                .replace("{next-capacity}", Toolchain.format(capacityUpgradeNext.getValue()))
                                .replace("{indentifier}", "" + capacityUpgrade.getIndentifier())
                                .replace("{capacity}", Toolchain.format(capacityUpgrade.getValue())))
                        .addLoreLine(" §fCusto: §2$§a{coins}".replace("{coins}", Toolchain.format(capacityUpgradeNext.getPrice())))
                        .addLoreLine("")
                        .addLoreLine(hasMoney);
                itemStack = itemBuilderHas.build();
            }

            IntelligentItem intelligentXP = IntelligentItem.of(itemStack, event -> {
                player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);

                if (!SpawnerPlugin.getInstance().getUpgradeManager().hasNextCapacity(capacityUpgrade.getIndentifier())) {
                    BukkitUtils.sendMessage(player, "&cVocê já está no ultima evolução.");
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5.0f, 1.0f);
                    player.closeInventory();
                    return;
                }

                CapacityUpgrade capacityUpgradeNext = SpawnerPlugin.getInstance().getUpgradeManager().getNextCapacity(capacityUpgrade.getIndentifier());

                if (!SpawnerPlugin.getEconomy().has(player.getName(), capacityUpgradeNext.getPrice())) {
                    BukkitUtils.sendMessage(player, "&cVocê não tem dinheiro suficiente para evoluir.");
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5.0f, 1.0f);
                    player.closeInventory();
                    return;
                }

                spawnerModel.getUpgradeModel().setCapacityUpgrade(capacityUpgradeNext);
                SpawnerPlugin.getEconomy().withdrawPlayer(player.getName(), capacityUpgradeNext.getPrice());
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 1.0f);
                BukkitUtils.sendMessage(player, "&aYAY! Evolução adquirida com sucesso.");
                LogPlugin.getInstance().getLogManager().registerSpawner(player, "SPAWNER_UPGRADE", "CAPACITY", capacityUpgrade.getPrice());
                SpawnerPlugin.getInstance().getSpawnerRepository().update(spawnerModel);

                reOpenInventory(player, spawnerModel);
            });
            contents.set(15, intelligentXP);
        }
    }

    public void reOpenInventory(Player player, SpawnerModel spawnerModel) {
            if(!InventoryUtils.getList().contains(player.getName())) {
                RyseInventory inventory = new UpgradeInventory(spawnerModel).build();
                inventory.open(player);
                InventoryUtils.addDelay(player);
            }
    }

}
