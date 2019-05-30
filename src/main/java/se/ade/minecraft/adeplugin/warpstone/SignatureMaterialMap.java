package se.ade.minecraft.adeplugin.warpstone;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;

public class SignatureMaterialMap {
    private enum MappedMaterial {
        WHITE_WOOL(                     Material.WHITE_WOOL,                    0),
        ORANGE_WOOL(                    Material.ORANGE_WOOL,                   1),
        MAGENTA_WOOL(                   Material.MAGENTA_WOOL,                  2),
        LIGHT_BLUE_WOOL(                Material.LIGHT_BLUE_WOOL,               3),
        YELLOW_WOOL(                    Material.YELLOW_WOOL,                   4),
        LIME_WOOL(                      Material.LIME_WOOL,                     5),
        PINK_WOOL(                      Material.PINK_WOOL,                     6),
        GRAY_WOOL(                      Material.GRAY_WOOL,                     7),
        LIGHT_GRAY_WOOL(                Material.LIGHT_GRAY_WOOL,               8),
        CYAN_WOOL(                      Material.CYAN_WOOL,                     9),
        PURPLE_WOOL(                    Material.PURPLE_WOOL,                   10),
        BLUE_WOOL(                      Material.BLUE_WOOL,                     11),
        BROWN_WOOL(                     Material.BROWN_WOOL,                    12),
        GREEN_WOOL(                     Material.GREEN_WOOL,                    13),
        RED_WOOL(                       Material.RED_WOOL,                      14),
        BLACK_WOOL(                     Material.BLACK_WOOL,                    15),
        WHITE_TERRACOTTA(               Material.WHITE_TERRACOTTA,              16),
        ORANGE_TERRACOTTA(              Material.ORANGE_TERRACOTTA,             17),
        MAGENTA_TERRACOTTA(             Material.MAGENTA_TERRACOTTA,            18),
        LIGHT_BLUE_TERRACOTTA(          Material.LIGHT_BLUE_TERRACOTTA,         19),
        YELLOW_TERRACOTTA(              Material.YELLOW_TERRACOTTA,             20),
        LIME_TERRACOTTA(                Material.LIME_TERRACOTTA,               21),
        PINK_TERRACOTTA(                Material.PINK_TERRACOTTA,               22),
        GRAY_TERRACOTTA(                Material.GRAY_TERRACOTTA,               23),
        LIGHT_GRAY_TERRACOTTA(          Material.LIGHT_GRAY_TERRACOTTA,         24),
        CYAN_TERRACOTTA(                Material.CYAN_TERRACOTTA,               25),
        PURPLE_TERRACOTTA(              Material.PURPLE_TERRACOTTA,             26),
        BLUE_TERRACOTTA(                Material.BLUE_TERRACOTTA,               27),
        BROWN_TERRACOTTA(               Material.BROWN_TERRACOTTA,              28),
        GREEN_TERRACOTTA(               Material.GREEN_TERRACOTTA,              29),
        RED_TERRACOTTA(                 Material.RED_TERRACOTTA,                30),
        BLACK_TERRACOTTA(               Material.BLACK_TERRACOTTA,              31);
        /*
        WHITE_GLAZED_TERRACOTTA(        Material.WHITE_GLAZED_TERRACOTTA,       32),
        ORANGE_GLAZED_TERRACOTTA(       Material.ORANGE_GLAZED_TERRACOTTA,      33),
        MAGENTA_GLAZED_TERRACOTTA(      Material.MAGENTA_GLAZED_TERRACOTTA,     34),
        LIGHT_BLUE_GLAZED_TERRACOTTA(   Material.LIGHT_BLUE_GLAZED_TERRACOTTA,  35),
        YELLOW_GLAZED_TERRACOTTA(       Material.YELLOW_GLAZED_TERRACOTTA,      36),
        LIME_GLAZED_TERRACOTTA(         Material.LIME_GLAZED_TERRACOTTA,        37),
        PINK_GLAZED_TERRACOTTA(         Material.PINK_GLAZED_TERRACOTTA,        38),
        GRAY_GLAZED_TERRACOTTA(         Material.GRAY_GLAZED_TERRACOTTA,        39),
        LIGHT_GRAY_GLAZED_TERRACOTTA(   Material.LIGHT_GRAY_GLAZED_TERRACOTTA,  40),
        CYAN_GLAZED_TERRACOTTA(         Material.CYAN_GLAZED_TERRACOTTA,        41),
        PURPLE_GLAZED_TERRACOTTA(       Material.PURPLE_GLAZED_TERRACOTTA,      42),
        BLUE_GLAZED_TERRACOTTA(         Material.BLUE_GLAZED_TERRACOTTA,        43),
        BROWN_GLAZED_TERRACOTTA(        Material.BROWN_GLAZED_TERRACOTTA,       44),
        GREEN_GLAZED_TERRACOTTA(        Material.GREEN_GLAZED_TERRACOTTA,       45),
        RED_GLAZED_TERRACOTTA(          Material.RED_GLAZED_TERRACOTTA,         46),
        BLACK_GLAZED_TERRACOTTA(        Material.BLACK_GLAZED_TERRACOTTA,       47);
        */

        private Material blockType;
        private int databaseId;
        MappedMaterial(Material type, int databaseId) {
            this.blockType = type;
            this.databaseId = databaseId;
        }
    }

    private HashMap<Integer, MappedMaterial> stringIdLookUp = new HashMap<>();
    private HashMap<Material, MappedMaterial> materialLookUp = new HashMap<>();

    public SignatureMaterialMap() {
        for(MappedMaterial m : MappedMaterial.values()) {
            stringIdLookUp.put(m.databaseId, m);
            materialLookUp.put(m.blockType, m);
        }
    }

    public Material fromStringId(int in) {
        if(!stringIdLookUp.containsKey(in)) return null;

        return stringIdLookUp.get(in).blockType;
    }

    public int getDataBaseId(Material m) {
        if(!isSignatureMaterial(m)) return -1;

        return materialLookUp.get(m).databaseId;
    }

    public boolean isSignatureMaterial(Material m) {
        return materialLookUp.containsKey(m);
    }

    public boolean verifyAllBlocksAreSignatureMaterial(Block[] blocks) {
        for(int i = 0; i < blocks.length; i++) {
            if(!isSignatureMaterial(blocks[i].getType()))
                return false;
        }
        return true;
    }
}
