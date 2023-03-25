package br.net.rankup.spawners.inventory;

import br.net.rankup.booster.api.BoosterAPI;
import br.net.rankup.booster.models.Account;
import br.net.rankup.booster.type.BoosterType;
import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.misc.BukkitUtils;
import br.net.rankup.spawners.misc.InventoryUtils;
import br.net.rankup.spawners.misc.ItemBuilder;
import br.net.rankup.spawners.misc.Toolchain;
import br.net.rankup.spawners.model.spawner.SpawnerModel;
import br.net.rankup.spawners.model.spawner.UpgradeModel;
import br.net.rankup.spawners.model.user.UserModel;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class DefaultInventory implements InventoryProvider {

    private SpawnerModel spawnerModel;

    public DefaultInventory(SpawnerModel spawnerModel) {
        this.spawnerModel = spawnerModel;
    }

    public RyseInventory build() {
        return RyseInventory.builder()
                .title("Gerador".replace("&", "§"))
                .rows(3)
                .provider(this)
                .disableUpdateTask()
                .build(SpawnerPlugin.getInstance());
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        String friends = (spawnerModel.getFriends().size() == 0 ? "§eNenhum" : ""+ Toolchain.format(spawnerModel.getFriends().size()));
        UserModel userModel = SpawnerPlugin.getInstance().getUserManager().get(player.getName());

        double stackLimite = userModel.getStackLimite();
        String bonus = "";

        Account account = BoosterAPI.getAccount(player);
        if(account != null) {
            if (account.getType().equals(BoosterType.STACK)) {
                bonus = (bonus + " §f➟ §6Booster de StackMobs §7(" + account.getBonus() + "x)");
                stackLimite *= account.getBonus();
            }
        }

        UpgradeModel upgradeModel = spawnerModel.getUpgradeModel();

        ItemBuilder itemBuilderInfo = new ItemBuilder(Material.MOB_SPAWNER)
                .setName("§aInformações")
                .addLoreLine("§7Veja algumas inforamações")
                .addLoreLine("§7desse gerador abaixo:")
                .addLoreLine("§7")
                .addLoreLine("§8• §fDono: §e"+spawnerModel.getOwner())
                .addLoreLine("§8• §fStack Limite: §e"+Toolchain.format(stackLimite) + bonus)
                .addLoreLine("§8• §fAmigos adicionados: §e"+friends)
                .addLoreLine("§8• §fQuantidade de geradores: §e"+Toolchain.format(spawnerModel.getAmount()))
                .addLoreLine("")
                .addLoreLine("§eUpgrades:")
                .addLoreLine(" §fVelocidade: §7"+upgradeModel.getTimeUpgrade().getValue()+"s")
                .addLoreLine(" §fXP p/ mob: §7"+upgradeModel.getXpUpgrade().getValue()+"x")
                .addLoreLine(" §fCapacidade: §7"+Toolchain.format(upgradeModel.getCapacityUpgrade().getValue())+" geradores");
            contents.set(10, itemBuilderInfo.build());

        ItemBuilder itemBuilderFriends = new ItemBuilder(Material.NETHER_STAR)
                .setName("§aAmigos")
                .addLoreLine("§7Gerencie seus amigos adicionados")
                .addLoreLine("§7a este gerador e suas permissões.")
                .addLoreLine("§7")
                .addLoreLine("§aClique para abrir.");

        IntelligentItem intelligentFriends = IntelligentItem.of(itemBuilderFriends.build(), event -> {
            if(!spawnerModel.getOwner().equalsIgnoreCase(player.getName())) {
                BukkitUtils.sendMessage(player, "&cVocê não pode gerenciar os amigos desse gerador.");
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5.0F, 1.0f);
                player.closeInventory();
                return;
            }

            if(!InventoryUtils.getList().contains(player.getName())) {
                RyseInventory inventory = new FriendsInventory(spawnerModel).build();
                inventory.open(player);
                InventoryUtils.addDelay(player);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 5.0f);
            }
            event.setCancelled(true);
        });
        contents.set(12, intelligentFriends);

        ItemBuilder itemBuilderUpgrade = new ItemBuilder(Material.HOPPER)
                .setName("§aUpgrades")
                .addLoreLine("§7Evolua seu gerador e")
                .addLoreLine("§7multiplique seus ganhos.")
                .addLoreLine("§7")
                .addLoreLine("§aClique para abrir.");

        IntelligentItem intelligentItemUpgrade = IntelligentItem.of(itemBuilderUpgrade.build(), event -> {
            if(!InventoryUtils.getList().contains(player.getName())) {
                RyseInventory inventory = new UpgradeInventory(spawnerModel).build();
                inventory.open(player);
                InventoryUtils.addDelay(player);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 5.0f);
            }
            event.setCancelled(true);
        });
        contents.set(14, intelligentItemUpgrade);

        ItemBuilder itemBuilderSettings = new ItemBuilder(Material.REDSTONE)
                .setName("§aConfigurações")
                .addLoreLine("§7Gerencie seu gerador da forma")
                .addLoreLine("§7que lhe agradar")
                .addLoreLine("§7")
                .addLoreLine("§aClique para abrir.");

        IntelligentItem intelligentItemSettings = IntelligentItem.of(itemBuilderSettings.build(), event -> {
            if(!InventoryUtils.getList().contains(player.getName())) {
                RyseInventory inventory = new SettingsInventory(spawnerModel).build();
                inventory.open(player);
                InventoryUtils.addDelay(player);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 5.0f);
            }
            event.setCancelled(true);
        });
        contents.set(16, intelligentItemSettings);
    }

}
