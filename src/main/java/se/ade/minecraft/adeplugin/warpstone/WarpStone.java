package se.ade.minecraft.adeplugin.warpstone;

import org.bukkit.World;
import org.bukkit.block.Block;
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
    private Float yaw;

    public WarpStone(Coords coords, Float yaw, World world, WarpStoneSignature signature, boolean isSource) {
        this.coords = coords;
        this.world = world;
        this.yaw = yaw;
        this.signature = signature;
        this.isSource = isSource;
    }

    /**
     * Return the block containing the warp stone (Note, not the block above it)
     */
    public Block getBlock() {
        return world.getBlockAt((int)coords.x, (int)coords.y, (int)coords.z);
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

    public Float getYaw() {
        return yaw;
    }

    public void setYaw(Float yaw) {
        this.yaw = yaw;
    }
}
