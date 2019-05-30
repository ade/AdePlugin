package se.ade.minecraft.adeplugin.warpstone;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Arrays;

/**
 * Adrian Nilsson
 * Created 2013-12-28 18:39
 */
public class WarpStoneSignature {
    private String data;

    private WarpStoneSignature(String data) {
        this.data = data;
    }

    public static WarpStoneSignature fromWarpStoneBlock(Block block, Block excludedBlock, SignatureMaterialMap map) {
        Block[] blocks;

        if(block.getLocation().getBlockY() == 0) {
            blocks = new Block[] {
                block.getRelative(BlockFace.WEST),
                block.getRelative(BlockFace.EAST),
                block.getRelative(BlockFace.NORTH),
                block.getRelative(BlockFace.SOUTH),
                block.getRelative(BlockFace.NORTH_WEST),
                block.getRelative(BlockFace.NORTH_EAST),
                block.getRelative(BlockFace.SOUTH_WEST),
                block.getRelative(BlockFace.SOUTH_EAST),
            };
        } else {
            Block below = block.getRelative(BlockFace.DOWN);

            blocks = new Block[] {
                block.getRelative(BlockFace.WEST),
                block.getRelative(BlockFace.EAST),
                block.getRelative(BlockFace.NORTH),
                block.getRelative(BlockFace.SOUTH),
                block.getRelative(BlockFace.NORTH_WEST),
                block.getRelative(BlockFace.NORTH_EAST),
                block.getRelative(BlockFace.SOUTH_WEST),
                block.getRelative(BlockFace.SOUTH_EAST),
                below,
                below.getRelative(BlockFace.WEST),
                below.getRelative(BlockFace.EAST),
                below.getRelative(BlockFace.SOUTH),
                below.getRelative(BlockFace.NORTH),
                below.getRelative(BlockFace.NORTH_EAST),
                below.getRelative(BlockFace.NORTH_WEST),
                below.getRelative(BlockFace.SOUTH_WEST),
                below.getRelative(BlockFace.SOUTH_EAST)
            };
        }

        String data = toSignatureString(blocks, excludedBlock, map);
        if(data == null) {
            return null;
        }

        return new WarpStoneSignature(data);
    }

    public static WarpStoneSignature fromData(String s) {
        if(s == null || s.length() == 0) {
            return null;
        } else {
            return new WarpStoneSignature(s);
        }
    }

    private static String toSignatureString(Block[] blocks, Block excludedBlock, SignatureMaterialMap map) {
        int[] colors = new int[blocks.length];
        boolean containsKey = false;
        for(int i = 0; i < blocks.length; i++) {
            boolean excluded = false;

            if(excludedBlock != null && excludedBlock.getLocation().equals(blocks[i].getLocation())) {
                //This block is excluded (e.g. pending removal by an event), so don't include it
                excluded = true;
            }

            if(!excluded && map.isSignatureMaterial(blocks[i].getType())) {
                colors[i] = map.getDataBaseId(blocks[i].getType());
                containsKey = true;
            } else {
                colors[i] = -1;
            }
        }

        if(!containsKey) {
            return null;
        }

        Arrays.sort(colors);

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < blocks.length; i++) {
            if(colors[i] >= 0) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(colors[i]);
            }
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(o != null && o instanceof WarpStoneSignature) {
            WarpStoneSignature other = (WarpStoneSignature)o;
            if(this.data == null) { //Check if both null.
                return other.data == null;
            } else {
                return data.equals(other.data);
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return data;
    }

    public String getData() {
        return data;
    }
}
