package se.ade.minecraft.adeplugin.portals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.ade.minecraft.adeplugin.AdePlugin;

/**
 * Adrian Nilsson
 * Created 2013-12-27 23:24
 */
public class PortalListener implements Listener {
    private AdePlugin plugin;
    private PortalManager portalManager;

    public PortalListener(AdePlugin plugin) {
        this.plugin = plugin;
        this.portalManager = new PortalManager();
    }

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getBlockFace() == BlockFace.UP) {
            World world = event.getPlayer().getWorld();
            ItemStack stack = event.getItem();

            if(stack != null && stack.getType() == Material.DIRT) {
                final Vector direction = event.getPlayer().getEyeLocation().getDirection();
                direction.setZ(0);

                Location loc = event.getPlayer().getLocation();
                direction.multiply(2);
                loc.add(direction);



                boolean xy;
                int axis = (int)loc.getYaw();
                if(axis < 0) {
                    axis += 360;
                    axis = (axis + 45) / 90;
                }else {
                    axis = (axis + 45) / 90;
                }
                xy = (axis % 2 == 0);

                portalManager.create(loc, xy);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPhysicsEvent(BlockPhysicsEvent e) {
        if (e.getBlock().getType() == Material.PORTAL) {
            e.setCancelled(true);
        }
    }
}
