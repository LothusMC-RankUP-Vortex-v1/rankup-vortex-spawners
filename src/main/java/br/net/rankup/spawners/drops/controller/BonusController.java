package br.net.rankup.spawners.drops.controller;

import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.drops.models.bonus.BonusModel;
import br.net.rankup.spawners.misc.Toolchain;
import org.bukkit.entity.*;

public class BonusController
{
    private final SpawnerPlugin plugin;
    
    public BonusController(final SpawnerPlugin plugin) {
        this.plugin = plugin;
    }
    
    public double applyGroupBonus(final Player player, final double toApply) {
        for (final BonusModel element : this.plugin.getBonusCache().getElements()) {
            if (player.hasPermission(element.getPermission())) {
                return toApply + toApply / 100.0 * element.getBonus();
            }
        }
        return toApply;
    }

    public double applyGroupDescont(final Player player, final double toApply) {
        for (final BonusModel element : this.plugin.getBonusCache().getElements()) {
            if (player.hasPermission(element.getPermission())) {
                return toApply - toApply / 100.0 * element.getBonus();
            }
        }
        return toApply;
    }

    public String getBonusMessage(final Player player) {
        for (final BonusModel element : this.plugin.getBonusCache().getElements()) {
            if (player.hasPermission(element.getPermission())) {
                return " §f§l| " + element.getFriendlyName().replace("&", "§") + " §7(" + Toolchain.formatPercentage(element.getBonus()) + "%)";
            }
        }
        return "";
    }

    public String getDescountMessage(final Player player) {
        for (final BonusModel element : this.plugin.getBonusCache().getElements()) {
            if (player.hasPermission(element.getPermission())) {
                return element.getFriendlyName().replace("&", "§") + " §7(" + Toolchain.formatPercentage(element.getBonus()) + "%)";
            }
        }
        return "";
    }
    public String getRank(final Player player) {
        for (final BonusModel element : this.plugin.getBonusCache().getElements()) {
            if (player.hasPermission(element.getPermission())) {
                return element.getFriendlyName().replace("&", "§");
            }
        }
        return "";
    }
    
    public SpawnerPlugin getPlugin() {
        return this.plugin;
    }
}
