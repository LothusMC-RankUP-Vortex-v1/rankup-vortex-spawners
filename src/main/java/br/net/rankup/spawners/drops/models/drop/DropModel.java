package br.net.rankup.spawners.drops.models.drop;

import br.net.rankup.spawners.SpawnerPlugin;

public class DropModel
{
    private final SpawnerPlugin plugin;
    private double price;

    
    public DropModel(Double price) {
        this.plugin = SpawnerPlugin.getInstance();
        this.price = price;
    }
    
    public SpawnerPlugin getPlugin() {
        return this.plugin;
    }
    
    public double getPrice() {
        return this.price;
    }

    public void setPrice(final double amount) {
        this.price = amount;
    }
}
