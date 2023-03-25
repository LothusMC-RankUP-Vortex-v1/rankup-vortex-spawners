package br.net.rankup.spawners.drops.cache;


import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.drops.Cache;
import br.net.rankup.spawners.drops.adapter.BonusAdapter;
import br.net.rankup.spawners.drops.models.bonus.BonusModel;

public class BonusCache extends Cache<BonusModel>
{
    private final SpawnerPlugin plugin;
    
    public BonusCache(final SpawnerPlugin plugin) {
        this.plugin = plugin;
        final BonusAdapter adapter = new BonusAdapter();
        for (final String key : plugin.getConfig().getConfigurationSection("bonus").getKeys(false)) {
            this.addElement(adapter.read(plugin.getConfig().getConfigurationSection("bonus." + key)));
        }
    }
    
    public BonusModel getByPermission(final String permission) {
        return this.get(bonusModel -> bonusModel.getPermission().equals(permission));
    }
    
    public SpawnerPlugin getPlugin() {
        return this.plugin;
    }
}
