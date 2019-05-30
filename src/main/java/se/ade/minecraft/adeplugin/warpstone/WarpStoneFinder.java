package se.ade.minecraft.adeplugin.warpstone;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class WarpStoneFinder {
    public static List<Block> findPotentialStonesFromSignatureComponent(Block block, SignatureMaterialMap map) {
        Block[] blocks;

        if(block.getLocation().getBlockY() == 255) {
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
            Block above = block.getRelative(BlockFace.UP);

            blocks = new Block[] {
                    block.getRelative(BlockFace.WEST),
                    block.getRelative(BlockFace.EAST),
                    block.getRelative(BlockFace.NORTH),
                    block.getRelative(BlockFace.SOUTH),
                    block.getRelative(BlockFace.NORTH_WEST),
                    block.getRelative(BlockFace.NORTH_EAST),
                    block.getRelative(BlockFace.SOUTH_WEST),
                    block.getRelative(BlockFace.SOUTH_EAST),
                    above,
                    above.getRelative(BlockFace.WEST),
                    above.getRelative(BlockFace.EAST),
                    above.getRelative(BlockFace.SOUTH),
                    above.getRelative(BlockFace.NORTH),
                    above.getRelative(BlockFace.NORTH_EAST),
                    above.getRelative(BlockFace.NORTH_WEST),
                    above.getRelative(BlockFace.SOUTH_WEST),
                    above.getRelative(BlockFace.SOUTH_EAST)
            };
        }

        ArrayList<Block> result = new ArrayList<>();
        for(Block b : blocks) {
            if(b.getType() == WarpStoneModule.DESTINATION_BLOCK_MATERIAL || b.getType() == WarpStoneModule.SOURCE_BLOCK_MATERIAL) {
                result.add(b);
            }
        }

        return result;
    }
}
