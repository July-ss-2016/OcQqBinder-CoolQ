package vip.ourcraft.coolqplugins.ocqqbinder.managers;

import vip.ourcraft.coolqplugins.ocqqbinder.MySQLSettings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLManager {
    private Connection con;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    public MySQLManager(MySQLSettings settings) {
        this.host = settings.getMySQLHost();
        this.port = settings.getMySQLPort();
        this.database = settings.getMySQLDatabase();
        this.username = settings.getMySQLUserName();
        this.password = settings.getMySQLPassword();

        connect();
    }

    public boolean executeStatement(String sql) {
        try {
            PreparedStatement preparedStatement = this.con.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Connection getCon() {
        try {
            if (con.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return con;
    }

    public boolean reconnect() {
        Connection oldCon = con;

        if (connect()) {
            try {
                oldCon.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        return false;
    }

    public boolean isConnected() {
        try {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");

            this.con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return con != null;
    }

    public void disconnect() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
