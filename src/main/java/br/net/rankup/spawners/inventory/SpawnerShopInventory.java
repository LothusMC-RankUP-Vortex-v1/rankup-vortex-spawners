package br.net.rankup.spawners.inventory;

import br.net.rankup.logger.LogPlugin;
import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.listener.ChatBuyListener;
import br.net.rankup.spawners.misc.*;
import br.net.rankup.spawners.model.spawner.UpgradeModel;
import br.net.rankup.spawners.model.upgrade.CapacityUpgrade;
import br.net.rankup.spawners.model.upgrade.TimeUpgrade;
import br.net.rankup.spawners.model.upgrade.XpUpgrade;
import br.net.rankup.spawners.model.user.UserModel;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator;
import io.github.rysefoxx.inventory.plugin.pattern.SlotIteratorPattern;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class SpawnerShopInventory implements InventoryProvider {



    public RyseInventory build() {
        return RyseInventory.builder()
                .title("Comprar Geradores".replace("&", "§"))
                .rows(5)
                .provider(this)
                .disableUpdateTask()
                .build(SpawnerPlugin.getInstance());
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();
        pagination.iterator(SlotIterator.builder().withPattern(SlotIteratorPattern.builder().define(
                                "XXXXXXXXX",
                                "XXXXXXXXX",
                                "XXOOOOOXX",
                                "XXOOOOOXX",
                                "XXXXXXXXX",
                                "XXXXXXXXX")
                        .attach('O')
                        .buildPattern())
                .build());
        pagination.setItemsPerPage(10);

        UserModel userModel = SpawnerPlugin.getInstance().getUserManager().get(player.getName());

        ItemBuilder itemBuilderHead =
                new ItemBuilder(SkullCreatorUtils.itemFromName(player.getName()))
                        .setName("§eInformações")
                        .lore(
                                "§7Veja abaixo suas informações:"
                                , ""
                                ,"§fDesconto: §7" + SpawnerPlugin.getInstance().getBonusController().getDescountMessage(player)
                                ,"§fStackMob: §7"+Toolchain.format(userModel.getStackLimite())
                                ,"§fLimite: §7"+Toolchain.format(userModel.getSpawnerLimite())
                                , "");

        contents.set(4, itemBuilderHead.build());

        SpawnerPlugin.getInstance().getShopManager().getSpawners().forEach(spawnerShop -> {
            AtomicReference<Double> price = new AtomicReference<>(SpawnerPlugin.getInstance().getBonusController().applyGroupDescont(player, spawnerShop.getPrice()));
            String custo = (price.get() != spawnerShop.getPrice())
                    ? "§fCusto: §7§m{old-coins}§2 §a{coins} coins"
                    .replace("{coins}", Toolchain.format(price.get()))
                    .replace("{old-coins}", Toolchain.format(spawnerShop.getPrice()))
                    : "§fCusto: §2$§a{custo} coins".replace("{custo}", Toolchain.format(spawnerShop.getPrice()));
            ItemBuilder itemBuilder = new ItemBuilder(
                    SkullCreatorUtils.itemFromName("MHF_" + spawnerShop.getEntity()
                            .replace("_", "").replace("IRONGOLEM", "GOLEM")
            )).setName("§a"+TranslateMob.traslateName(spawnerShop.getEntity()))
                    .addLoreLine("")
                    .addLoreLine("§fTipo: §7"+TranslateMob.traslateName(spawnerShop.getEntity()))
                    .addLoreLine(custo)
                    .addLoreLine("")
                    .addLoreLine("§e§l▎ §fVocê consegue comprar com seu")
                    .addLoreLine("§e§l▎ §fdinheiro §e{spawner} §fdesse gerador.".replace("{spawner}", Toolchain.format(SpawnerPlugin.getEconomy().getBalance(player.getName())/spawnerShop.getPrice())))
                    .addLoreLine("")
                    .addLoreLine("§fBotão esquerdo: §7Adquirir 1x")
                    .addLoreLine("§fBotão direito: §7Escolher quantidade");

            if(System.currentTimeMillis() < spawnerShop.getTime()) {

                final SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                final Date dateSpawner = new Date(spawnerShop.getTime());
                final String date = dateformat.format(dateSpawner).substring(0, 10);
                final String hour = dateformat.format(dateSpawner).substring(11, 16);

                itemBuilder = new ItemBuilder(Material.BARRIER)
                        .setName("§c§k"+TranslateMob.traslateName(spawnerShop.getEntity()))
                        .addLoreLine("")
                        .addLoreLine("§cEsse gerador não está liberado ainda!")
                        .addLoreLine("§cSerá liberado em {date} ás {hour}".replace("{date}", date).replace("{hour}", hour))
                        .addLoreLine("");
            }

            IntelligentItem intelligentItem = IntelligentItem.of(itemBuilder.build(), event -> {
                if(System.currentTimeMillis() < spawnerShop.getTime()) {
                    player.closeInventory();
                    BukkitUtils.sendMessage(player, "&cEsse gerador não está liberado para comprar!");
                    return;
                }

                if(event.isLeftClick()) {
                    price.updateAndGet(v -> new Double((double) (v * 1)));
                    if(SpawnerPlugin.getEconomy().getBalance(player.getName()) < price.get()) {
                        player.closeInventory();
                        BukkitUtils.sendMessage(player, "&cVocê não tem dinheiro suficiente para adquirir esse gerador!");
                        return;
                    }

                    EntityType entityType = EntityType.valueOf(spawnerShop.getEntity());
                    TimeUpgrade timeUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getTime(0);
                    XpUpgrade xpUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getXP(0);
                    CapacityUpgrade capacityUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getCapacity(0);
                    UpgradeModel upgradeModel = new UpgradeModel(timeUpgrade, xpUpgrade, capacityUpgrade);
                    ItemStack itemStack = SpawnerPlugin.getInstance().getSpawnerManager().getItemStack(entityType, 1, upgradeModel);

                    if (player.getInventory().firstEmpty() == -1) {
                        player.getLocation().getWorld().dropItemNaturally(player.getLocation(), itemStack);
                    } else {
                        player.getInventory().addItem(itemStack);
                    }

                    SpawnerPlugin.getEconomy().withdrawPlayer(player.getName(), price.get());
                    BukkitUtils.sendMessage(player, "&aGerador(es) adquirido(s) com sucesso!".replace("{coins}", Toolchain.format(price.get())));
                    LogPlugin.getInstance().getLogManager().registerEconomy(player, "SPAWNER_BUY", price.get());
                    LogPlugin.getInstance().getLogManager().registerSpawner(player, "SPAWNER_BUY", entityType.getName(), price.get());
                }

                if(event.isRightClick()) {
                    player.sendMessage(new String[]{
                            "",
                            "§e Quantos geradores você deseja comprar?",
                            "§7 Digite 'cancelar' caso queira cancelar o processo.",
                            ""});
                    ChatBuyListener.getPlayers().put(player.getName(), spawnerShop);
                    player.closeInventory();
                }

            });
            pagination.addItem(intelligentItem);

        });


    }

}
