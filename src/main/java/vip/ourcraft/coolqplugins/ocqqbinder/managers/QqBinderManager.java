package vip.ourcraft.coolqplugins.ocqqbinder.managers;

import vip.ourcraft.coolqplugins.ocqqbinder.QqInfo;
import vip.ourcraft.coolqplugins.ocqqbinder.MySQLSettings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QqBinderManager {
    private MySQLManager mySQLManager;
    private MySQLSettings settings;

    public QqBinderManager(MySQLSettings settings) {
        this.mySQLManager = new MySQLManager(settings);
        this.settings = settings;
    }

    public String getPlayerNameByQq(long qq) {
        if (qq == 0L) {
            throw new IllegalArgumentException("qq can't be 0");
        }

        try {
            java.sql.PreparedStatement preparedStatement = mySQLManager.getCon().prepareStatement("select * from " + settings.getMySQLTableName() + " where qq = '" + qq + "'");
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? resultSet.getString("player_name") : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public long getQqByPlayerName(String playerName) {
        if (playerName == null) {
            throw new IllegalArgumentException("playername can't be null");
        }

        try {
            java.sql.PreparedStatement preparedStatement = mySQLManager.getCon().prepareStatement("select * from " + settings.getMySQLTableName() + " where player_name = '" + playerName.toLowerCase() + "'");
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? resultSet.getLong("qq") : 0L;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0L;
    }

    public boolean isBoundQq(String playerName) {
        return getQqByPlayerName(playerName) != 0L;
    }

    public boolean isBoundQq(long qq) {
        return getPlayerNameByQq(qq) != null;
    }

    public boolean unbindQq(String playerName) {
        if (!isBoundQq(playerName)) {
            throw new IllegalArgumentException("qq not bound");
        }

        return mySQLManager.executeStatement("delete * from " + settings.getMySQLTableName() + " where player_name = '" + playerName + "'");
    }

    public boolean unbindQq(long qq) {
        if (!isBoundQq(qq)) {
            throw new IllegalArgumentException("qq not bound");
        }

        return mySQLManager.executeStatement("delete * from " + settings.getMySQLTableName() + " where qq = '" + qq + "'");
    }

    public boolean bindQq(String playerName, long qq) {
        if (playerName == null) {
            throw new IllegalArgumentException("playername can't be null");
        }

        if (qq == 0L) {
            throw new IllegalArgumentException("qq can't be 0");
        }

        try {
            java.sql.PreparedStatement preparedStatement = mySQLManager.getCon().prepareStatement("insert into " + settings.getMySQLTableName() + " (player_name, qq) values(?,?)");

            preparedStatement.setString(1, playerName.toLowerCase());
            preparedStatement.setLong(2, qq);

            return preparedStatement.execute();
        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }

    public List<QqInfo> getBoundQqs() {
        List<QqInfo> result = new ArrayList<>();

        try {
            java.sql.PreparedStatement preparedStatement = mySQLManager.getCon().prepareStatement("select * from " + settings.getMySQLTableName());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                result.add(new QqInfo(resultSet.getLong("qq"), resultSet.getString("player_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
