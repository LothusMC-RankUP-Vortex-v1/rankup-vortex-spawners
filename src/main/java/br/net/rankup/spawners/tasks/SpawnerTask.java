package br.net.rankup.spawners.tasks;

import br.net.rankup.booster.api.BoosterAPI;
import br.net.rankup.booster.models.Account;
import br.net.rankup.booster.type.BoosterType;
import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.misc.BukkitUtils;
import br.net.rankup.spawners.misc.Toolchain;
import br.net.rankup.spawners.misc.TranslateMob;
import br.net.rankup.spawners.model.spawner.UpgradeModel;
import br.net.rankup.spawners.model.user.UserModel;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class SpawnerTask extends BukkitRunnable {
    @Override
    public void run() {
        SpawnerPlugin.getInstance().getSpawnerManager().getSpawners().values().forEach(spawnerModel -> {

            if(spawnerModel.hologram()) {
                UpgradeModel upgradeModel = spawnerModel.getUpgradeModel();
                if(!spawnerModel.getSettingsModel().isActived()) {
                    return;
                }
                if (spawnerModel.getTime() >= upgradeModel.getTimeUpgrade().getValue()) {
                    final Entity findedEntity = spawnerModel.getLocation().getWorld().getNearbyEntities(spawnerModel.getLocation(), 5D, 5D, 5D)
                            .stream()
                            .filter(entity -> entity.getType() == spawnerModel.getType() && entity.hasMetadata("spawner_amount") && entity.hasMetadata("spawner_location"))
                            .findFirst()
                            .orElse(null);

                        if(findedEntity != null) {
                            final double mobAmount = findedEntity.getMetadata("spawner_amount").get(0).asDouble();
                            double newAmount = mobAmount + spawnerModel.getAmount();
                            UserModel userModel = SpawnerPlugin.getInstance().getUserManager().get(spawnerModel.getOwner());
                            double maxStack = userModel.getStackLimite();

                            Account account = BoosterAPI.getAccount(Bukkit.getPlayer(spawnerModel.getOwner()));
                            if(account != null) {
                                if (account.getType().equals(BoosterType.STACK)) {
                                    maxStack *= account.getBonus();
                                }
                            }

                            if (newAmount >= maxStack) newAmount = maxStack;
                            findedEntity.removeMetadata("spawner_location", SpawnerPlugin.getInstance());
                            findedEntity.removeMetadata("spawner_amount", SpawnerPlugin.getInstance());
                            findedEntity.setMetadata("spawner_location", new FixedMetadataValue(SpawnerPlugin.getInstance(), BukkitUtils.serializeLocation(spawnerModel.getLocation())));
                            findedEntity.setMetadata("spawner_amount", new FixedMetadataValue(SpawnerPlugin.getInstance(), newAmount));

                            findedEntity.setCustomName("§e" + Toolchain.format(newAmount)
                                    + "x §7" + (newAmount > 1
                                    ? Objects.requireNonNull(TranslateMob.traslateName(findedEntity.getType().toString())) + "s"
                                    : Objects.requireNonNull(TranslateMob.traslateName(findedEntity.getType().toString()))));
                        } else {
                            UserModel userModel = SpawnerPlugin.getInstance().getUserManager().get(spawnerModel.getOwner());
                            double amount = spawnerModel.getAmount();
                            double stackLimite = userModel.getStackLimite();

                            Account account = BoosterAPI.getAccount(Bukkit.getPlayer(spawnerModel.getOwner()));
                            if(account != null) {
                                if (account.getType().equals(BoosterType.STACK)) {
                                    stackLimite *= account.getBonus();
                                }
                            }
                            if (amount >= stackLimite) amount = stackLimite;

                            final Entity entity = spawnerModel.getLocation().getWorld().spawnEntity(spawnerModel.getLocation().clone().add(2, 0, 2), spawnerModel.getType());
                            entity.setMetadata("spawner_location", new FixedMetadataValue(SpawnerPlugin.getInstance(), BukkitUtils.serializeLocation(spawnerModel.getLocation())));
                            entity.setMetadata("spawner_amount", new FixedMetadataValue(SpawnerPlugin.getInstance(), amount));
                            entity.setCustomNameVisible(true);
                            entity.setCustomName("§e" + Toolchain.format(amount)
                                    + "x §7" + (amount > 1
                                    ? Objects.requireNonNull(TranslateMob.traslateName(entity.getType().toString())) + "s"
                                    : Objects.requireNonNull(TranslateMob.traslateName(entity.getType().toString()))));

                            this.renameEntity(entity);
                        }
                    spawnerModel.setTime(0);
                     return;
                }
                spawnerModel.setTime(spawnerModel.getTime() + 1);
            }

        });
    }

    private void renameEntity(Entity entity) {
        final net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        final NBTTagCompound nbtTag = nmsEntity.getNBTTag() == null ? new NBTTagCompound() : nmsEntity.getNBTTag();
        nmsEntity.c(nbtTag);
        nbtTag.setInt("NoAI", 1);
        nmsEntity.f(nbtTag);
    }

}
