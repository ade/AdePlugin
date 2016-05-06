package se.ade.minecraft.adeplugin.util;

import org.bukkit.Location;

/**
 * Adrian Nilsson
 * Created 2013-12-28 01:15
 */
public class Coords {
    public long x;
    public long y;
    public long z;

    public Coords(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coords(Location location) {
        this.x = (long)location.getX();
        this.y = (long)location.getY();
        this.z = (long)location.getZ();
    }


}
