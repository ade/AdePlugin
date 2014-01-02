package se.ade.minecraft.adeplugin.db;


import org.bukkit.plugin.Plugin;
import se.ade.minecraft.adeplugin.AdePlugin;

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

    public ResultSet query(String query, Object... arguments) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for(int i = 0; i < arguments.length; i++) {
                if(arguments[i] == null) {
                    statement.setString(i+1, null);
                } else {
                    statement.setString(i+1, arguments[i].toString());
                }
            }
            return statement.executeQuery();
        } catch (SQLException e) {
            plugin.getLogger().info("Problem executing query '" + query + "': " + e.toString());
            e.printStackTrace();
        }

        return null;
    }

    public int update(String query, Object... arguments) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for(int i = 0; i < arguments.length; i++) {
                if(arguments[i] == null) {
                    statement.setString(i+1, null);
                } else {
                    statement.setString(i+1, arguments[i].toString());
                }
            }

            return statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().info("Problem executing statement '" + query + "': " + e.toString());
            e.printStackTrace();
        }

        return 0;
    }
}
