package br.net.rankup.spawners.drops.models.bonus;

public class BonusModel
{
    private final String friendlyName;
    private final String permission;
    private final double bonus;
    private double descount;
    
    BonusModel(final String friendlyName, final String permission, final double bonus, final double descount) {
        this.friendlyName = friendlyName;
        this.permission = permission;
        this.bonus = bonus;
        this.descount = descount;
    }
    
    public static BonusModelBuilder builder() {
        return new BonusModelBuilder();
    }
    
    public String getFriendlyName() {
        return this.friendlyName;
    }
    
    public String getPermission() {
        return this.permission;
    }
    
    public double getBonus() {
        return this.bonus;
    }

    public double getDescount() {
        return this.descount;
    }
    
    public static class BonusModelBuilder
    {
        private String friendlyName;
        private String permission;
        private double bonus;
        private double descount;
        
        BonusModelBuilder() {
        }
        
        public BonusModelBuilder friendlyName(final String friendlyName) {
            this.friendlyName = friendlyName;
            return this;
        }
        
        public BonusModelBuilder permission(final String permission) {
            this.permission = permission;
            return this;
        }
        
        public BonusModelBuilder bonus(final double bonus) {
            this.bonus = bonus;
            return this;
        }

        public BonusModelBuilder descount(final double descount) {
            this.descount = descount;
            return this;
        }
        
        public BonusModel build() {
            return new BonusModel(this.friendlyName, this.permission, this.bonus, this.descount);
        }

    }
}
