package br.net.rankup.spawners.model.spawner;

import br.net.rankup.booster.api.BoosterAPI;
import br.net.rankup.booster.models.Account;
import br.net.rankup.booster.type.BoosterType;
import br.net.rankup.spawners.Constants;
import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.misc.ProgressBar;
import br.net.rankup.spawners.misc.TimeFormat;
import br.net.rankup.spawners.misc.Toolchain;
import br.net.rankup.spawners.misc.TranslateMob;
import br.net.rankup.spawners.model.user.UserModel;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SpawnerModel {

    private Hologram hologram;
    private Location location;
    private String owner;
    private List<String> friends;
    private EntityType type;
    private double amount;
    private UpgradeModel upgradeModel;
    private SettingsModel settingsModel;
    private double time;

    public boolean hologram() {

        if(!settingsModel.isHologramActived()) {
            if(!settingsModel.isActived()) return false;
            if(getNearbyEntities(5)) {
                return true;
            }
        }

        boolean canContinue = false;

        String status = Constants.statusLore.get(0);

        if(getNearbyEntities(Constants.radius)) {
            status = Constants.statusLore.get(1);
            canContinue = true;
        }
        if(!settingsModel.isActived()) {
            status = Constants.statusLore.get(2);
        }
        final Entity findedEntity = this.getLocation().getWorld().getNearbyEntities(this.getLocation(), 5D, 5D, 5D)
                .stream()
                .filter(entity -> entity.getType() == this.getType() && entity.hasMetadata("spawner_amount") && entity.hasMetadata("spawner_location"))
                .findFirst()
                .orElse(null);

        if(findedEntity != null) {
            UserModel userModel = SpawnerPlugin.getInstance().getUserManager().get(this.owner);
            if(userModel != null) {
                if(!findedEntity.isDead()) {
                    double stackMobs = findedEntity.getMetadata("spawner_amount").get(0).asDouble();

                    double stackLimite = userModel.getStackLimite();

                    Account account = BoosterAPI.getAccount(Bukkit.getPlayer(owner));
                    if(account != null) {
                        if (account.getType().equals(BoosterType.STACK)) {
                            stackLimite *= account.getBonus();
                        }
                    }

                    if(stackMobs >= stackLimite) {
                        status = Constants.statusLore.get(3);
                        canContinue = false;
                    }
                }
            }
        }

        if(hologram != null) {
            for (int i = 0; i < Constants.lines.size(); i++) {
                TextLine line = (TextLine) hologram.getLine(i);
                line.setText(Constants.lines.get(i)
                        .replace("{status}", status)
                        .replace("{type}", TranslateMob.traslateName(type.toString()))
                        .replace("{amount}", Toolchain.format(this.amount))
                        .replace("{bar}", ProgressBar.progressBar(this.getTime(), upgradeModel.getTimeUpgrade().getValue(), "▎"))
                        .replace("{tempo}", TimeFormat.formatTime((int) (upgradeModel.getTimeUpgrade().getValue() - this.getTime())))
                        .replace('&', '§'));
            }
        }
              return canContinue;
    }
    public boolean getNearbyEntities(double radius) {
        int amountPlayer = 0;
        for (final Entity entity : location.getWorld().getNearbyEntities(location, radius,  radius, radius)) {
            if (entity instanceof Player) {
                ++amountPlayer;
            }
        }
        return amountPlayer >= 1;
    }

    public void remove() {
        this.hologram.delete();
        SpawnerPlugin.getInstance().getSpawnerManager().remove(this);
        SpawnerPlugin.getInstance().getSpawnerRepository().deleteTable(location);
    }

    public void addAmount(double amountInHand) {
        amount += amountInHand;
    }
}
