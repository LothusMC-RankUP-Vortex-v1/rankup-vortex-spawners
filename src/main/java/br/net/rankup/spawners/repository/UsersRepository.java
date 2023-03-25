package br.net.rankup.spawners.repository;

import br.net.rankup.spawners.database.HikariDataBase;
import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.model.user.UserModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersRepository {

    private final HikariDataBase hikariDataBase = SpawnerPlugin.getInstance().getHikariDataBase();
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS spawner_user (" +
            "id INTEGER NOT NULL AUTO_INCREMENT, " +
            "name CHAR(36) NOT NULL UNIQUE, " +
            "spawnerLimite DOUBLE NOT NULL, " +
            "stackLimite DOUBLE NOT NULL, " +
            "PRIMARY KEY (id));";
    public static final String CLEAR_TABLE = "DELETE FROM spawner_user;";
    public static final String SELECT_QUERY = "SELECT * FROM spawner_user WHERE name = ?;";
    public static final String SELECTALL_QUERY = "SELECT * FROM spawner_user;";
    public static final String UPDATE_QUERY = "INSERT INTO spawner_user " +
            "(name, spawnerLimite, stackLimite) VALUES (?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE spawnerLimite = ?, stackLimite = ?;";

    public void createTable() {
        try (final Connection connection = hikariDataBase.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
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
                        double spawnerLimite = resultSet.getDouble("spawnerLimite");
                        double stackLimite = resultSet.getDouble("stackLimite");
                        String name = resultSet.getString("name");
                        UserModel userModel = new UserModel(name, spawnerLimite, stackLimite);
                        SpawnerPlugin.getInstance().getUserManager().add(userModel);

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

    public void update(UserModel userModel) {
        Runnable runnable = () -> {
            try (Connection connection = hikariDataBase.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
                    statement.setString(1, userModel.getName());

                    statement.setDouble(2, userModel.getSpawnerLimite());
                    statement.setDouble(3, userModel.getStackLimite());

                    statement.setDouble(4, userModel.getSpawnerLimite());
                    statement.setDouble(5, userModel.getStackLimite());

                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
        SpawnerPlugin.getInstance().getHikariDataBase().executeAsync(runnable);
    }

}
