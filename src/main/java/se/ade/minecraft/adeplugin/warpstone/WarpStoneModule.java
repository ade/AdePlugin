package se.ade.minecraft.adeplugin.warpstone;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import se.ade.minecraft.adeplugin.AdePlugin;
import se.ade.minecraft.adeplugin.infrastructure.SubModule;
import se.ade.minecraft.adeplugin.util.Coords;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adrian Nilsson
 * Created 2013-12-28 19:10
 */
public class WarpStoneModule implements Listener, SubModule {
    private static final Material SOURCE_BLOCK_MATERIAL = Material.REDSTONE_BLOCK;
    private static final Material DESTINATION_BLOCK_MATERIAL = Material.COAL_BLOCK;
    private static final String SOURCE_ITEM_NAME = ChatColor.GOLD + "Warp Stone";
    private static final String DESTINATION_ITEM_NAME = ChatColor.GOLD + "Warp Destination";

    WarpStoneRepository repository;
    ShapedRecipe sourceRecipe;
    ShapedRecipe destinationRecipe;
    AdePlugin plugin;

    @Override
    public void onEnable(AdePlugin plugin) {
        this.plugin = plugin;
        repository = new WarpStoneRepository(plugin.getDbConnection());
        addWarpStoneRecipe();
        addDestinationStoneRecipe();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onDisable() {
        plugin.removeRecipe(sourceRecipe);
        plugin.removeRecipe(destinationRecipe);
    }

    private ItemStack getSourceItem() {
        ItemStack item = new ItemStack(SOURCE_BLOCK_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SOURCE_ITEM_NAME);
        List<String> lore = new ArrayList<String>();
        lore.add("Place inside of a plus-sign shape of four");
        lore.add("wool blocks with a stone pressure plate on top.");
        lore.add("The color of the wool sets the warp destination.");
        lore.add("To create a destination warp, use a Warp Destination block.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getDestinationItem() {
        ItemStack item = new ItemStack(DESTINATION_BLOCK_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(DESTINATION_ITEM_NAME);
        List<String> lore = new ArrayList<String>();
        lore.add("Place inside of a plus-sign shape of four wool blocks.");
        lore.add("The color of the wool determines the warp source.");
        lore.add("To create a source warp, use a Warp Stone block.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void addWarpStoneRecipe() {
        sourceRecipe = new ShapedRecipe(getSourceItem());
        sourceRecipe.shape("ABA", "BCB", "ABA");
        sourceRecipe.setIngredient('A', Material.REDSTONE);
        sourceRecipe.setIngredient('B', Material.GOLD_INGOT);
        sourceRecipe.setIngredient('C', Material.ENDER_PEARL);
        plugin.getServer().addRecipe(sourceRecipe);
    }

    private void addDestinationStoneRecipe() {
        destinationRecipe = new ShapedRecipe(getDestinationItem());
        destinationRecipe.shape("ABA", "BCB", "ABA");
        destinationRecipe.setIngredient('A', Material.REDSTONE);
        destinationRecipe.setIngredient('B', Material.COAL);
        destinationRecipe.setIngredient('C', Material.ENDER_PEARL);
        plugin.getServer().addRecipe(destinationRecipe);
    }

    @EventHandler
    public void onEvent(PlayerInteractEvent ev) {
        if(ev.getAction().equals(Action.PHYSICAL)){
            Block block = ev.getClickedBlock();
            if(block == null) return;

            if(block.getType() == Material.STONE_PRESSURE_PLATE && block.getRelative(BlockFace.DOWN).getType() == SOURCE_BLOCK_MATERIAL){
                //Outbound warp stone possibly detected. Check if is a warp stone, and validate adjacent blocks.
                WarpStoneSignature signature = getWarpSignature(block.getRelative(BlockFace.DOWN));
                if(signature != null) {
                    WarpStone source = repository.findByCoords(block.getRelative(BlockFace.DOWN));
                    if(source != null && source.isSource()) {
                        WarpStone target = repository.findBySignature(signature, new Coords(block.getRelative(BlockFace.DOWN).getLocation()), false);
                        if(target != null) {
                            teleport(ev, target);
                        }
                    }
                }
            }
        }
    }

    private void teleport(PlayerEvent event, final WarpStone target) {
        float yaw = event.getPlayer().getLocation().getYaw();
        if(target.getYaw() != null) {
            yaw = target.getYaw();
        }

        Block above = target.getBlock().getRelative(BlockFace.UP);
        double teleportHeight = target.getCoords().y + 1.1;
        while (above != null && (!above.isEmpty() && !above.isLiquid())) {
            above = above.getRelative(BlockFace.UP);
            teleportHeight++;
        }

        final Location destinationLocation = new Location(target.getWorld(), target.getCoords().x + 0.5, teleportHeight, target.getCoords().z + 0.5, yaw, event.getPlayer().getLocation().getPitch());
        final Player player = event.getPlayer();

        event.getPlayer().getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
        event.getPlayer().getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1.2f);

        target.getWorld().loadChunk((int)(target.getCoords().x >> 4), (int)(target.getCoords().z >> 4), true);

        Chunk chunk = target.getBlock().getChunk();
        plugin.debugLog("Chunk loaded: " + chunk.isLoaded());
        if(!chunk.isLoaded()) {
            boolean loaded = chunk.load();
            plugin.debugLog("Chunk load returned " + loaded);
        }

        player.sendBlockChange(target.getBlock().getLocation(), target.getBlock().getType(), target.getBlock().getData());
        player.teleport(destinationLocation);
        player.sendBlockChange(target.getBlock().getLocation(), target.getBlock().getType(), target.getBlock().getData());

        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                target.getWorld().playEffect(destinationLocation, Effect.MOBSPAWNER_FLAMES, 0);
                target.getWorld().playSound(destinationLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1.3f);

                //Re-teleport to avoid falling(experimental)
                player.teleport(destinationLocation);
                player.sendBlockChange(target.getBlock().getLocation(), target.getBlock().getType(), target.getBlock().getData());
            }
        }, 2);
    }

    private boolean isSourceWarpStoneItem(ItemStack item) {
        if(item.getType() == SOURCE_BLOCK_MATERIAL) {
            if(item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals(SOURCE_ITEM_NAME)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDestinationWarpStoneItem(ItemStack item) {
        if(item.getType() == DESTINATION_BLOCK_MATERIAL) {
            if(item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals(DESTINATION_ITEM_NAME)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onEvent(BlockPlaceEvent event) {
        if(isWarpStoneBlockType(event.getBlockPlaced())) {
            ItemStack item = event.getItemInHand();

            if(isSourceWarpStoneItem(item) || isDestinationWarpStoneItem(item)) {
                boolean isSource = isSourceWarpStoneItem(item);

                //Placing a warp stone or destination.
                repository.saveStone(new WarpStone(new Coords(event.getBlockPlaced().getLocation()), event.getPlayer().getLocation().getYaw(), event.getBlockPlaced().getWorld(), getWarpSignature(event.getBlockPlaced()), isSource));

                //Play a linked effect if there is a corresponding warp arrangement.
                playEnableEffectIfLinked(event.getBlockPlaced(), isSource);
            }
        } else if(WarpStoneSignature.isSignatureMaterial(event.getBlockPlaced().getType())) {
            //Search for adjacent warp stones that may have been affected and update them
            updateWarpStoneIfExists(event.getBlockPlaced().getRelative(BlockFace.NORTH), null);
            updateWarpStoneIfExists(event.getBlockPlaced().getRelative(BlockFace.SOUTH), null);
            updateWarpStoneIfExists(event.getBlockPlaced().getRelative(BlockFace.WEST), null);
            updateWarpStoneIfExists(event.getBlockPlaced().getRelative(BlockFace.EAST), null);
        }
    }

    private void playEnableEffectIfLinked(Block warpBlock, boolean isSource) {
        WarpStoneSignature signature = getWarpSignature(warpBlock);
        if(signature != null) {
            WarpStone target = repository.findBySignature(signature, new Coords(warpBlock.getLocation()), !isSource);
            if(target != null) {
                playEnableEffect(warpBlock);
                playEnableEffect(target.getBlock());
            }
        }
    }

    private void playEnableEffect(Block warpBlock) {
        final Location location = warpBlock.getRelative(BlockFace.UP).getLocation();
        warpBlock.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 0);

        final World world = warpBlock.getWorld();
        world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.7f);
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                world.playSound(location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.3f);
                world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.1f, 0.5f);
                world.playSound(location, Sound.ENTITY_TNT_PRIMED, 1f, 1.1f);
            }
        }, 22);
    }

    private void playDisableEffect(Block warpBlock) {
        final Location location = warpBlock.getRelative(BlockFace.UP).getLocation();
        warpBlock.getWorld().playEffect(location, Effect.SMOKE, 4);
        warpBlock.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.2f, 0.7f);
    }

    private Set<Player> getPlayersInRange(int range, Location origin) {
        Set<Player> back = new HashSet<Player>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(origin.getWorld())) {
                if (p.getLocation().distanceSquared(origin) <= range) {
                    back.add(p);
                }
            }
        }
        return back;
    }

    @EventHandler
    public void onEvent(BlockBreakEvent event) {
        if(isWarpStoneBlockType(event.getBlock())) {
            WarpStone stone = repository.findByCoords(event.getBlock());

            if(stone != null) {
                ItemStack drop = null;
                repository.deleteStone(stone);

                if(event.getBlock().getType() == SOURCE_BLOCK_MATERIAL) {
                    drop = getSourceItem();
                }

                if(event.getBlock().getType() == DESTINATION_BLOCK_MATERIAL) {
                    drop = getDestinationItem();
                }

                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
            }
        } else if(WarpStoneSignature.isSignatureMaterial(event.getBlock().getType())) {
            //Check if a warpstone has been broken in the adjacent blocks
            updateWarpStoneIfExists(event.getBlock().getRelative(BlockFace.NORTH), event.getBlock());
            updateWarpStoneIfExists(event.getBlock().getRelative(BlockFace.EAST), event.getBlock());
            updateWarpStoneIfExists(event.getBlock().getRelative(BlockFace.SOUTH), event.getBlock());
            updateWarpStoneIfExists(event.getBlock().getRelative(BlockFace.WEST), event.getBlock());
        }

    }

    /**
     * Makes sure any warp stone potentially existing at <block> (source or destination) has the correct signature stored
     * */
    private void updateWarpStoneIfExists(Block block, Block blockPendingRemoval) {
        if(isWarpStoneBlockType(block)) {
            WarpStone warpStone = repository.findByCoords(block);

            if(warpStone != null) {
                WarpStoneSignature worldSignature = getWarpSignature(block, blockPendingRemoval);
                WarpStoneSignature oldSignature = warpStone.getSignature();

                if(oldSignature != null && oldSignature.equals(worldSignature)) {
                    //Nothing has been changed, no update needed.
                    return;
                }

                if(worldSignature != null) {
                    //New signature exists
                    warpStone.setSignature(worldSignature);
                    repository.saveStone(warpStone);
                    playEnableEffectIfLinked(block, warpStone.isSource());
                } else if(oldSignature != null) {
                    //Had signature before, but does not have a valid signature any more
                    warpStone.setSignature(null);
                    repository.saveStone(warpStone);

                    plugin.debugLog("Warp stone signature removed at " + block.getLocation().toString());

                    //Play fizzle effect on stone
                    playDisableEffect(block);

                    //Look for other warp stone of same type //TODO: Get all, not just one
                    WarpStone otherCopy = repository.findBySignature(oldSignature, new Coords(block.getLocation()), warpStone.isSource());
                    if(otherCopy != null) {
                        playDisableEffect(otherCopy.getBlock());
                    }

                    //Look for a linked warp stone //TODO: Get all, not just one
                    WarpStone destinationStone = repository.findBySignature(oldSignature, new Coords(block.getLocation()), !warpStone.isSource());
                    if(destinationStone != null) {
                        playDisableEffect(destinationStone.getBlock());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEvent(final AsyncPlayerChatEvent event) {
        if(plugin.isDevMode()) {
            if (event.getMessage().contains("-warp-items")) {
                ItemStack[] kit = new ItemStack[27];
                for (int i = 0; i < DyeColor.values().length; i++) {
                    DyeColor color = DyeColor.values()[i];
                    kit[i] = new Wool(color).toItemStack(64);
                }

                kit[16] = new ItemStack(Material.COAL, 64);
                kit[17] = new ItemStack(Material.GOLD_INGOT, 64);
                kit[18] = new ItemStack(Material.ENDER_PEARL, 64);
                kit[19] = new ItemStack(Material.REDSTONE, 64);
                kit[20] = new ItemStack(Material.DIAMOND_PICKAXE, 64);
                kit[21] = new ItemStack(Material.TORCH, 64);
                kit[22] = new ItemStack(Material.IRON_INGOT, 64);
                kit[23] = new ItemStack(Material.STONE_PRESSURE_PLATE, 64);
                kit[24] = new ItemStack(Material.OAK_WOOD, 64);
                kit[25] = getSourceItem();
                kit[26] = getDestinationItem();


                event.getPlayer().getInventory().clear();
                event.getPlayer().getInventory().addItem(kit);
            }
        }
    }

    private WarpStoneSignature getWarpSignature(Block block) {
        return getWarpSignature(block, null);
    }

    /**
     * Return a warp stone signature if a valid one was found bounding the specified block, otherwise null.
     */
    private WarpStoneSignature getWarpSignature(Block origin, Block excludeBlock) {
        Block[] adjacent = getAdjacentBlocks(origin);

        if(excludeBlock != null) {
            int rx = excludeBlock.getLocation().getBlockX();
            int ry = excludeBlock.getLocation().getBlockY();
            int rz = excludeBlock.getLocation().getBlockZ();
            for (Block adjacentBlock : adjacent) {
                int ax = adjacentBlock.getLocation().getBlockX();
                int ay = adjacentBlock.getLocation().getBlockY();
                int az = adjacentBlock.getLocation().getBlockZ();
                if (ax == rx && ay == ry && az == rz) {
                    //This block is excluded (e.g. pending removal by an event)
                    return null;
                }
            }
        }

        if(WarpStoneSignature.isSignatureMaterial(adjacent)) {
            return new WarpStoneSignature(adjacent);
        } else {
            return null;
        }
    }

    private Block[] getAdjacentBlocks(Block center) {
        Block[] adjacent = new Block[4];
        adjacent[0] = center.getRelative(BlockFace.WEST);
        adjacent[1] = center.getRelative(BlockFace.EAST);
        adjacent[2] = center.getRelative(BlockFace.SOUTH);
        adjacent[3] = center.getRelative(BlockFace.NORTH);
        return adjacent;
    }

    /**
     * Check if a block is possibly a warp stone by block type (doesn't check actual DB!)
     */
    public boolean isWarpStoneBlockType(Block block) {
        return block.getType() == SOURCE_BLOCK_MATERIAL || block.getType() == DESTINATION_BLOCK_MATERIAL;
    }

}
