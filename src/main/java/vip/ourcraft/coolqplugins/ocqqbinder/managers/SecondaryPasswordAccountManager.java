package vip.ourcraft.coolqplugins.ocqqbinder.managers;


import vip.ourcraft.coolqplugins.ocqqbinder.MySQLSettings;
import vip.ourcraft.coolqplugins.ocqqbinder.utils.Util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SecondaryPasswordAccountManager {
    private MySQLSettings settings;
    private MySQLManager mySQLManager;

    public SecondaryPasswordAccountManager(MySQLSettings settings) {
        this.mySQLManager = new MySQLManager(settings);
        this.settings = settings;
    }

    public boolean registerAccount(String playerName, String password) {
        if (isRegistered(playerName)) {
            throw new IllegalArgumentException("already registered");
        }

        return setAccountPassword(playerName, password);
    }

    public boolean isRegistered(String playerName) {
        if (playerName == null) {
            throw new IllegalArgumentException("playername can't be null");
        }

        try {
            java.sql.PreparedStatement preparedStatement = mySQLManager.getCon().prepareStatement("SELECT * FROM " + settings.getMySQLTableName() + " WHERE player_name = '" + playerName.toLowerCase() + "'");
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isRightPassword(String playerName, String password) {
        if (playerName == null) {
            throw new IllegalArgumentException("playername can't be null");
        }

        if (password == null) {
            throw new IllegalArgumentException("password can't be null");
        }

        if (!isRegistered(playerName)) {
            throw new IllegalArgumentException("not registered");
        }

        try {
            java.sql.PreparedStatement preparedStatement = mySQLManager.getCon().prepareStatement("SELECT * FROM " + settings.getMySQLTableName() + " WHERE player_name = '" + playerName.toLowerCase() + "'");
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() && resultSet.getString("pwd").equals(Util.md5(Util.md5(password)));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setAccountPassword(String playerName, String password) {
        if (playerName == null) {
            throw new IllegalArgumentException("playername can't be null");
        }

        if (password == null) {
            throw new IllegalArgumentException("password can't be null");
        }

        try {
            java.sql.PreparedStatement preparedStatement = mySQLManager.getCon().prepareStatement("REPLACE INTO " + settings.getMySQLTableName() + " (player_name, pwd) VALUES(?,?)");

            preparedStatement.setString(1, playerName.toLowerCase());
            preparedStatement.setString(2, Util.md5(Util.md5(password)));

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean unregisterAccount(String playerName) {
        if (playerName == null) {
            throw new IllegalArgumentException("playername can't be null");
        }

        return mySQLManager.executeStatement("DELETE FROM " + settings.getMySQLTableName() + " WHERE player_name = '" + playerName + "'");
    }
}
