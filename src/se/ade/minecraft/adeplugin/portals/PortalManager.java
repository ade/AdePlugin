package se.ade.minecraft.adeplugin.portals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

/**
 * Adrian Nilsson
 * Created 2013-12-28 01:13
 */
public class PortalManager {
    private PortalRepository portalRepository = new PortalRepository();
    private Plugin plugin;

    public void create(Location loc, boolean xy) {
        for(int sideOffset = 0; sideOffset < 2; sideOffset++) {
            for(int y = 0; y < 2; y++) {
                Block block;
                if(xy) {
                    block = loc.getWorld().getBlockAt(loc.getBlockX() + sideOffset, loc.getBlockY() + 1 + y, loc.getBlockZ());
                } else {
                    block = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1 + y, loc.getBlockZ() + sideOffset);
                }
                block.setType(Material.PORTAL);
            }
        }
    }
}
