package se.ade.minecraft.adeplugin.warpstone;

import org.bukkit.block.Block;
import se.ade.minecraft.adeplugin.AdePlugin;
import se.ade.minecraft.adeplugin.db.DbConnection;
import se.ade.minecraft.adeplugin.util.Coords;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Adrian Nilsson
 * Created 2013-12-28 18:23
 */
public class WarpStoneRepository {
    private DbConnection db; //Convenience pointer to database

    public WarpStoneRepository(DbConnection db) {
        this.db = db;
    }

    public WarpStone findByCoords(Block baseBlock) {
        return findByCoords(baseBlock.getX(), baseBlock.getY(), baseBlock.getZ(), baseBlock.getWorld().getName());
    }

    public WarpStone findByCoords(int x, int y, int z, String worldName) {
        ResultSet resultSet = db.query("SELECT * FROM warpstones WHERE x=? AND y=? AND z=? AND world=?",x,y,z,worldName);
        WarpStone stone = null;

        try {
            if (resultSet != null && resultSet.next()) {
                stone = mapWarpStoneResult(resultSet);
            }
        } catch (SQLException e) {
            sqlError(e);
        }

        return stone;
    }

    /**
     * Find a warp stone with the specified signature. If multiple matches are found, a random one is selected.
     */
    public WarpStone findBySignature(WarpStoneSignature signature, Coords exclude, boolean source) {
        ResultSet resultSet = db.query("SELECT * FROM warpstones WHERE signature = ? AND (x != ? OR y != ? OR z != ?) AND is_source=? ORDER BY RAND()", signature.getData(), exclude.x, exclude.y, exclude.z, source ? "1" : "0");
        WarpStone stone = null;

        try {
            if (resultSet != null && resultSet.next()) {
                stone = mapWarpStoneResult(resultSet);
            }
        } catch (SQLException e) {
            sqlError(e);
        }

        return stone;
    }

    private WarpStone mapWarpStoneResult(ResultSet resultSet) {
        try {
            Coords coords = new Coords(
                resultSet.getInt("x"),
                resultSet.getInt("y"),
                resultSet.getInt("z")
            );

            return new WarpStone(
                coords,
                AdePlugin.get().getServer().getWorld(resultSet.getString("world")),
                WarpStoneSignature.fromData(resultSet.getString("signature")),
                resultSet.getBoolean("is_source")
            );
        } catch(SQLException e) {
            sqlError(e);
        }
        return null;
    }

    private void sqlError(SQLException e) {
        AdePlugin.get().getLogger().info("Sql error");
        e.printStackTrace();
    }

    public void saveStone(WarpStone warpStone) {
        deleteStone(warpStone);
        String signatureData = warpStone.getSignature() != null ? warpStone.getSignature().getData() : null;
        db.update("INSERT INTO warpstones (x,y,z,world,is_source,signature) VALUES (?,?,?,?,?,?)", warpStone.getCoords().x, warpStone.getCoords().y, warpStone.getCoords().z, warpStone.getWorld().getName(), warpStone.isSource() ? "1" : "0", signatureData);
    }

    public boolean deleteStone(WarpStone warpStone) {
        return db.update("DELETE FROM warpstones WHERE x=? AND y=? AND z=? AND world=?", warpStone.getCoords().x, warpStone.getCoords().y, warpStone.getCoords().z, warpStone.getWorld().getName()) > 0;
    }
}
