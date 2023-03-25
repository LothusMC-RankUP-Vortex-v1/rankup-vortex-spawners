package br.net.rankup.spawners.inventory;

import br.net.rankup.spawners.Constants;
import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.misc.*;
import br.net.rankup.spawners.model.spawner.SettingsModel;
import br.net.rankup.spawners.model.spawner.SpawnerModel;
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

public class SettingsInventory implements InventoryProvider {

    private SpawnerModel spawnerModel;

    public SettingsInventory(SpawnerModel spawnerModel) {
        this.spawnerModel = spawnerModel;
    }

    public RyseInventory build() {
        return RyseInventory.builder()
                .title("Gerador - Configurações".replace("&", "§"))
                .rows(4)
                .provider(this)
                .disableUpdateTask()
                .build(SpawnerPlugin.getInstance());
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        ItemBuilder itemBuilderHologramInfo = new ItemBuilder(Material.ARMOR_STAND)
                .setName("§eHolograma")
                .addLoreLine("§7Holograma acima do gerador ")
                .addLoreLine("§7com suas inforamações.");
            contents.set(12, itemBuilderHologramInfo.build());


        String hologramColor = spawnerModel.getSettingsModel().isHologramActived() ? "§a" : "§c";
        String hologramStatus = spawnerModel.getSettingsModel().isHologramActived() ? "§aHabilitado" : "§cDesabilitado";
        int hologramDate = (spawnerModel.getSettingsModel().isHologramActived() ? 10 : 8);

        ItemBuilder itemBuilderHologram = new ItemBuilder(Material.getMaterial(351), 1, hologramDate)
                .setName(hologramColor+"Holograma")
                .addLoreLine("§fEstado: "+hologramStatus)
                .addLoreLine("")
                .addLoreLine("§aClique parar mudar o estado.");

        IntelligentItem intelligentToggle = IntelligentItem.of(itemBuilderHologram.build(), event -> {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);

            SettingsModel settingsModel = spawnerModel.getSettingsModel();

            if(settingsModel.isHologramActived()) {
                settingsModel.setHologramActived(false);
                spawnerModel.getHologram().delete();
                spawnerModel.setHologram(null);
            } else {
                settingsModel.setHologramActived(true);
                Hologram hologram = HologramsAPI.createHologram(SpawnerPlugin.getInstance(),
                        spawnerModel.getLocation().clone().add(0.5, Constants.hologramHeight, 0.5));
                for (final String line : Constants.lines) {
                    hologram.appendTextLine(line
                            .replace("{status}", "§eCarregando...")
                            .replace("{type}", TranslateMob.traslateName(spawnerModel.getType().toString()))
                            .replace("{bar}", ProgressBar.progressBar(spawnerModel.getTime(), spawnerModel.getUpgradeModel().getTimeUpgrade().getValue(), "▎"))
                            .replace("{tempo}", TimeFormat.formatTime((int) (spawnerModel.getUpgradeModel().getTimeUpgrade().getValue() - spawnerModel.getTime())))
                            .replace('&', '§'));
                    hologram.setAllowPlaceholders(true);
                }
                spawnerModel.setHologram(hologram);
            }
            SpawnerPlugin.getInstance().getSpawnerRepository().update(spawnerModel);
            reOpenInventory(player, spawnerModel);

            event.setCancelled(true);
        });
        contents.set(21, intelligentToggle);



        ItemBuilder itemBuilderEnable = new ItemBuilder(Material.MOB_SPAWNER)
                .setName("§eGerar mobs")
                .addLoreLine("§7Gerencie se seu gerador")
                .addLoreLine("§7irá funcionar ou não.");
        contents.set(14, itemBuilderEnable.build());


        String activedColor = spawnerModel.getSettingsModel().isActived() ? "§a" : "§c";
        String activedStatus = spawnerModel.getSettingsModel().isActived() ? "§aHabilitado" : "§cDesabilitado";
        int activedDate = (spawnerModel.getSettingsModel().isActived() ? 10 : 8);

        ItemBuilder itemBuilderActived = new ItemBuilder(Material.getMaterial(351), 1, activedDate)
                .setName(activedColor+"Gerar mobs")
                .addLoreLine("§fEstado: "+activedStatus)
                .addLoreLine("")
                .addLoreLine("§aClique parar mudar o estado.");

        IntelligentItem intelligentActived = IntelligentItem.of(itemBuilderActived.build(), event -> {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);

            spawnerModel.getSettingsModel().setActived(!spawnerModel.getSettingsModel().isActived());
            SpawnerPlugin.getInstance().getSpawnerRepository().update(spawnerModel);
            reOpenInventory(player, spawnerModel);
        });
        contents.set(23, intelligentActived);
    }

    public void reOpenInventory(Player player, SpawnerModel spawnerModel) {
            if(!InventoryUtils.getList().contains(player.getName())) {
                RyseInventory inventory = new SettingsInventory(spawnerModel).build();
                inventory.open(player);
                InventoryUtils.addDelay(player);
            }
    }

}
