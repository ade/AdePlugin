package se.ade.minecraft.adeplugin.warpstone;

import org.bukkit.World;
import se.ade.minecraft.adeplugin.util.Coords;

/**
 * Adrian Nilsson
 * Created 2013-12-28 18:21
 */
public class WarpStone {
    private Coords coords;
    private World world;
    private WarpStoneSignature signature;
    private boolean isSource;

    public WarpStone(Coords coords, World world, WarpStoneSignature signature, boolean isSource) {
        this.coords = coords;
        this.world = world;
        this.signature = signature;
        this.isSource = isSource;
    }

    public Coords getCoords() {
        return coords;
    }

    public WarpStoneSignature getSignature() {
        return signature;
    }

    public World getWorld() {
        return world;
    }

    public boolean isSource() {
        return isSource;
    }

    public void setSource(boolean source) {
        isSource = source;
    }

    public void setSignature(WarpStoneSignature signature) {
        this.signature = signature;
    }
}
