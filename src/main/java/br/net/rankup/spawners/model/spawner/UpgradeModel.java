package br.net.rankup.spawners.model.spawner;

import br.net.rankup.spawners.model.upgrade.CapacityUpgrade;
import br.net.rankup.spawners.model.upgrade.TimeUpgrade;
import br.net.rankup.spawners.model.upgrade.XpUpgrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpgradeModel {

    private TimeUpgrade timeUpgrade;
    private XpUpgrade xpUpgrade;
    private CapacityUpgrade capacityUpgrade;

}
