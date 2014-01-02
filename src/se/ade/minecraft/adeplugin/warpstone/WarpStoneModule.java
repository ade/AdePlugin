package se.ade.minecraft.adeplugin.warpstone;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import se.ade.minecraft.adeplugin.AdePlugin;
import se.ade.minecraft.adeplugin.infrastructure.SubModule;
import se.ade.minecraft.adeplugin.util.Coords;

import java.util.ArrayList;
import java.util.List;

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
            if(block.getType() == Material.STONE_PLATE && block.getRelative(BlockFace.DOWN).getType() == SOURCE_BLOCK_MATERIAL){
                //Outbound warp stone possibly detected. Check if is a warp stone, and validate adjacent blocks.
                WarpStoneSignature signature = getWarpSignature(block.getRelative(BlockFace.DOWN));
                if(signature != null) {
                    WarpStone source = repository.findByCoords(block.getRelative(BlockFace.DOWN));
                    if(source != null && source.isSource()) {
                        WarpStone target = repository.findBySignature(signature, new Coords(block.getRelative(BlockFace.DOWN).getLocation()), false);
                        if(target != null) {
                            Location location = new Location(target.getWorld(), target.getCoords().x + 0.5, target.getCoords().y+1, target.getCoords().z + 0.5, ev.getPlayer().getLocation().getYaw(), ev.getPlayer().getLocation().getPitch());
                            ev.getPlayer().teleport(location);
                        }
                    }
                }
            }
        }
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
                //Placing a warp stone or destination.
                repository.saveStone(new WarpStone(new Coords(event.getBlockPlaced().getLocation()), event.getBlockPlaced().getWorld(), getWarpSignature(event.getBlockPlaced()), isSourceWarpStoneItem(item)));
            }
        } else if(event.getBlockPlaced().getType() == Material.WOOL) {
            updateWarpStoneIfExists(event.getBlockPlaced().getRelative(BlockFace.NORTH), false);
            updateWarpStoneIfExists(event.getBlockPlaced().getRelative(BlockFace.SOUTH), false);
            updateWarpStoneIfExists(event.getBlockPlaced().getRelative(BlockFace.WEST), false);
            updateWarpStoneIfExists(event.getBlockPlaced().getRelative(BlockFace.EAST), false);
        }
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
        } else if(event.getBlock().getType() == Material.WOOL) {
            //Check if a warpstone has been broken in the adjacent blocks
            updateWarpStoneIfExists(event.getBlock().getRelative(BlockFace.NORTH), true);
            updateWarpStoneIfExists(event.getBlock().getRelative(BlockFace.EAST), true);
            updateWarpStoneIfExists(event.getBlock().getRelative(BlockFace.SOUTH), true);
            updateWarpStoneIfExists(event.getBlock().getRelative(BlockFace.WEST), true);
        }

    }

    private void updateWarpStoneIfExists(Block block, boolean removedBlock) {
        if(isWarpStoneBlockType(block)) {
            WarpStone warpStone = repository.findByCoords(block);

            if(warpStone != null) {
                boolean modified = false;
                WarpStoneSignature newSignature = null;

                Block[] adjacent = getAdjacentBlocks(block);
                if(!removedBlock && WarpStoneSignature.validateMaterials(adjacent)) {
                    newSignature = new WarpStoneSignature(adjacent);

                    if(warpStone.getSignature() == null) {
                        modified = true;
                    } else if(!warpStone.getSignature().equals(newSignature)) {
                        modified = true;
                    }
                } else if(warpStone.getSignature() != null) {
                    newSignature = null;
                    modified = true;
                }

                if(modified) {
                    warpStone.setSignature(newSignature);
                    repository.saveStone(warpStone);
                }
            }
        }
    }

    @EventHandler
    public void onEvent(AsyncPlayerChatEvent event) {

        if(event.getMessage().contains("-warp-items") && plugin.isDevMode()) {
            ItemStack[] kit = new ItemStack[27];
            for(int i = 0; i < DyeColor.values().length; i++) {
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
            kit[23] = new ItemStack(Material.STONE_PLATE, 64);
            kit[24] = new ItemStack(Material.WOOD, 64);
            kit[25] = getSourceItem();
            kit[26] = getDestinationItem();


            event.getPlayer().getInventory().clear();
            event.getPlayer().getInventory().addItem(kit);
        }

    }

    /**
     * Return a warp stone signature if a valid one was found bounding the specified block, otherwise null.
     */
    private WarpStoneSignature getWarpSignature(Block block) {
        Block[] adjacent = getAdjacentBlocks(block);

        if(WarpStoneSignature.validateMaterials(adjacent)) {
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
