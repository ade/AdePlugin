package se.ade.minecraft.adeplugin.db;


import com.mysql.jdbc.CommunicationsException;
import org.bukkit.plugin.Plugin;

import java.sql.*;

/**
 * Adrian Nilsson
 * Created 2013-12-28 17:50
 */
public class DbConnection {
    private Connection connection;
    private Plugin plugin;

    public DbConnection(Plugin plugin) {
        this.plugin = plugin;
    }

    private void onSqlError(SQLException e, String query) {
        plugin.getLogger().info("Problem executing query '" + query + "': " + e.toString());
        e.printStackTrace();
    }

    public void connect() {
        String url = plugin.getConfig().getString("database.url");
        String user = plugin.getConfig().getString("database.user");
        String password = plugin.getConfig().getString("database.password");

        try {
            this.connection = DriverManager.getConnection(url, user, password);
            plugin.getLogger().info("Database connection OK");
        } catch (SQLException ex) {
            plugin.getLogger().info("Database connection failed");
            ex.printStackTrace();
        }
    }

    private void validateConnection() {
        try {
            if(connection == null || connection.isClosed()) {
                plugin.getLogger().info("Reconnecting to database...");
                connect();
            }
        } catch (SQLException e) {
            plugin.getLogger().info("Database re-connection failed");
            e.printStackTrace();
        }
    }

    private ResultSet executeAsQuery(String query, Object... arguments) throws SQLException {
        validateConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        for(int i = 0; i < arguments.length; i++) {
            if(arguments[i] == null) {
                statement.setString(i+1, null);
            } else {
                statement.setString(i+1, arguments[i].toString());
            }
        }
        return statement.executeQuery();
    }

    public ResultSet query(String query, Object... arguments) {
        try {
            return executeAsQuery(query, arguments);
        } catch (SQLException ce) {
            //Retry once since the connection may be restored.
            try {
                plugin.getLogger().info("Retrying query...");
                return executeAsQuery(query, arguments);
            } catch (SQLException e2) {
                onSqlError(e2, query);
            }
        }
        return null;
    }

    private int executeAsUpdate(String query, Object... arguments) throws SQLException {
        validateConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        for(int i = 0; i < arguments.length; i++) {
            if(arguments[i] == null) {
                statement.setString(i+1, null);
            } else {
                statement.setString(i+1, arguments[i].toString());
            }
        }

        return statement.executeUpdate();
    }

    public int update(String query, Object... arguments) {
        try {
            return executeAsUpdate(query, arguments);
        } catch (SQLException e) {
            //Retry once since the connection may be restored.
            try {
                plugin.getLogger().info("Retrying update...");
                return executeAsUpdate(query, arguments);
            } catch (SQLException e2) {
                onSqlError(e2, query);
            }
        }
        return 0;
    }
}
