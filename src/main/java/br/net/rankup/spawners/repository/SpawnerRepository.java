package br.net.rankup.spawners.repository;

import br.net.rankup.spawners.Constants;
import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.adpter.FriendsAdpter;
import br.net.rankup.spawners.database.HikariDataBase;
import br.net.rankup.spawners.misc.BukkitUtils;
import br.net.rankup.spawners.misc.ProgressBar;
import br.net.rankup.spawners.misc.TimeFormat;
import br.net.rankup.spawners.misc.Toolchain;
import br.net.rankup.spawners.model.spawner.SettingsModel;
import br.net.rankup.spawners.model.spawner.SpawnerModel;
import br.net.rankup.spawners.model.spawner.UpgradeModel;
import br.net.rankup.spawners.model.upgrade.CapacityUpgrade;
import br.net.rankup.spawners.model.upgrade.TimeUpgrade;
import br.net.rankup.spawners.model.upgrade.XpUpgrade;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpawnerRepository {

    private final HikariDataBase hikariDataBase = SpawnerPlugin.getInstance().getHikariDataBase();
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS spawner_spawners (" +
            "id INTEGER NOT NULL AUTO_INCREMENT, " +
            "spawner_location CHAR(255) NOT NULL UNIQUE, " +
            "spawner_owner CHAR(36) NOT NULL, " +
            "spawner_friends CHAR(36) NOT NULL, " +
            "spawner_type CHAR(36) NOT NULL, " +
            "spawner_amount DOUBLE NOT NULL, " +
            "upgrade_time DOUBLE NOT NULL, " +
            "upgrade_xp DOUBLE NOT NULL, " +
            "upgrade_capacity DOUBLE NOT NULL, " +
            "settings_actived BOOLEAN NOT NULL, " +
            "settings_hologram BOOLEAN NOT NULL, " +
            "PRIMARY KEY (id));";
    public static final String CLEAR_TABLE = "DELETE FROM spawner_spawners WHERE spawner_location = ?;";
    public static final String SELECT_QUERY = "SELECT * FROM spawner_spawners WHERE spawner_location = ?;";
    public static final String SELECTALL_QUERY = "SELECT * FROM spawner_spawners;";
    public static final String UPDATE_QUERY = "INSERT INTO spawner_spawners " +
            "(spawner_location, spawner_owner, spawner_friends, spawner_type, spawner_amount, upgrade_time, upgrade_xp, upgrade_capacity, settings_actived, settings_hologram) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE spawner_owner = ?,  spawner_friends = ?,  spawner_type = ?" +
            ",  spawner_amount = ?,  upgrade_time = ?, upgrade_xp = ?, upgrade_capacity = ?, settings_actived = ?,  settings_hologram = ?;";

    public void createTable() {
        try (final Connection connection = hikariDataBase.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTable(Location location) {
        try (final Connection connection = hikariDataBase.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CLEAR_TABLE)) {
                statement.setString(1, BukkitUtils.serializeLocation(location));
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadAll() {
        try (Connection connection = hikariDataBase.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECTALL_QUERY)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Location location = BukkitUtils.deserializeLocation(resultSet.getString("spawner_location"));
                        String owner = resultSet.getString("spawner_owner");
                        String friend = resultSet.getString("spawner_friends");
                        List<String> friends = FriendsAdpter.deserialize(friend);

                        EntityType entityType = EntityType.fromName(resultSet.getString("spawner_type"));
                        Double amount = resultSet.getDouble("spawner_amount");

                        int timeIndentifier = resultSet.getInt("upgrade_time");
                        int xpIndentifier = resultSet.getInt("upgrade_xp");
                        int capacityIndentifier = resultSet.getInt("upgrade_capacity");

                        boolean spawnerActived = resultSet.getBoolean("settings_actived");
                        boolean hologramActived = resultSet.getBoolean("settings_hologram");

                        TimeUpgrade timeUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getTime(0);
                        if(SpawnerPlugin.getInstance().getUpgradeManager().getTime().containsKey(timeIndentifier)) {
                            timeUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getTime(timeIndentifier);
                        }

                        XpUpgrade xpUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getXP(0);
                        if(SpawnerPlugin.getInstance().getUpgradeManager().getXP().containsKey(xpIndentifier)) {
                            xpUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getXP(xpIndentifier);
                        }

                        CapacityUpgrade capacityUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getCapacity(0);
                        if(SpawnerPlugin.getInstance().getUpgradeManager().getCapacity().containsKey(capacityIndentifier)) {
                            capacityUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getCapacity(capacityIndentifier);
                        }

                        location.getChunk().load();

                        //set a block with spawner
                        location.getBlock().setMetadata("spawner_type", new FixedMetadataValue(SpawnerPlugin.getInstance(), entityType.getName()));
                        location.getBlock().setType(Material.MOB_SPAWNER);

                        //set block with mob
                        BlockState blockState = location.getBlock().getState();
                        CreatureSpawner spawner = ((CreatureSpawner) blockState);
                        spawner.setSpawnedType(entityType);
                        spawner.update();
                        blockState.update();

                        UpgradeModel upgradeModel = new UpgradeModel(timeUpgrade, xpUpgrade, capacityUpgrade);
                        SettingsModel settingsModel = new SettingsModel(spawnerActived, hologramActived);

                        Hologram hologram = HologramsAPI.createHologram(SpawnerPlugin.getInstance(),
                                location.clone().add(0.5, Constants.hologramHeight, 0.5));
                        for (final String line : Constants.lines) {
                            hologram.appendTextLine(line
                                    .replace("{status}", "§eCarregando...")
                                    .replace("{amount}", Toolchain.format(amount))
                                    .replace("{bar}", ProgressBar.progressBar(0, upgradeModel.getTimeUpgrade().getValue(), "▎"))
                                    .replace("{tempo}", TimeFormat.formatTime(0 - upgradeModel.getTimeUpgrade().getValue()))
                                    .replace('&', '§'));
                            hologram.setAllowPlaceholders(true);
                        }
                        SpawnerModel spawnerModel = new SpawnerModel(hologram, location, owner, friends, entityType, amount, upgradeModel, settingsModel, 0);
                        SpawnerPlugin.getInstance().getSpawnerManager().add(spawnerModel);

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(String name) {
        try (Connection connection = hikariDataBase.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY)) {
                statement.setString(1, name);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void update(SpawnerModel spawnerModel) {
        Runnable runnable = () -> {
            try (Connection connection = hikariDataBase.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
                    statement.setString(1, BukkitUtils.serializeLocation(spawnerModel.getLocation()));

                    statement.setString(2, spawnerModel.getOwner());
                    statement.setString(3, spawnerModel.getFriends().toString());
                    statement.setString(4, spawnerModel.getType().toString());
                    statement.setDouble(5, spawnerModel.getAmount());
                    statement.setDouble(6, spawnerModel.getUpgradeModel().getTimeUpgrade().getIndentifier());
                    statement.setDouble(7, spawnerModel.getUpgradeModel().getXpUpgrade().getIndentifier());
                    statement.setDouble(8, spawnerModel.getUpgradeModel().getCapacityUpgrade().getIndentifier());
                    statement.setBoolean(9, spawnerModel.getSettingsModel().isActived());
                    statement.setBoolean(10, spawnerModel.getSettingsModel().isHologramActived());

                    //set with duplicate key
                    statement.setString(11, spawnerModel.getOwner());
                    statement.setString(12, spawnerModel.getFriends().toString());
                    statement.setString(13, spawnerModel.getType().toString());
                    statement.setDouble(14, spawnerModel.getAmount());
                    statement.setDouble(15, spawnerModel.getUpgradeModel().getTimeUpgrade().getIndentifier());
                    statement.setDouble(16, spawnerModel.getUpgradeModel().getXpUpgrade().getIndentifier());
                    statement.setDouble(17, spawnerModel.getUpgradeModel().getCapacityUpgrade().getIndentifier());
                    statement.setBoolean(18, spawnerModel.getSettingsModel().isActived());
                    statement.setBoolean(19, spawnerModel.getSettingsModel().isHologramActived());

                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
        SpawnerPlugin.getInstance().getHikariDataBase().executeAsync(runnable);
    }

}
