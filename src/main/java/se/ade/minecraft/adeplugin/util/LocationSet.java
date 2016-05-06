package se.ade.minecraft.adeplugin.util;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;

/**
 * Adrian Nilsson
 * Created 2013-12-28 01:41
 */
public class LocationSet {
    private ArrayList<Location> locations = new ArrayList<Location>();

    public LocationSet(Location start) {
        locations.add(start);
    }

    public void up(int length) {
        draw(BlockFace.UP, length);
    }

    public void down(int length) {
        draw(BlockFace.DOWN, length);
    }

    public void north(int length) {
        draw(BlockFace.NORTH, length);
    }

    public void east(int length) {
        draw(BlockFace.EAST, length);
    }

    public void south(int length) {
        draw(BlockFace.SOUTH, length);
    }

    public void west(int length) {
        draw(BlockFace.WEST, length);
    }

    public void draw(BlockFace face, int length) {
        for(int i = 0; i < length; i++) {
            locations.add(locations.get(locations.size()-1).getBlock().getRelative(face, 1).getLocation().clone());
        }
    }
}
