package br.net.rankup.spawners.model.reward;

public class RewardModel
{
    private final String friendlyName;
    private final String command;
    private final double chance;
    
    public RewardModel(final String friendlyName, final String command, final double chance) {
        this.friendlyName = friendlyName;
        this.command = command;
        this.chance = chance;
    }
    
    public String getFriendlyName() {
        return this.friendlyName;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public double getChance() {
        return this.chance;
    }
}
