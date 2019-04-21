package se.ade.minecraft.adeplugin.warpstone;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import java.util.Arrays;

/**
 * Adrian Nilsson
 * Created 2013-12-28 18:39
 */
public class WarpStoneSignature {
    private String data;

    public WarpStoneSignature(String data) {
        this.data = data;
    }

    public WarpStoneSignature(Block[] materials) {
        data = materialsToData(materials);
    }

    public static WarpStoneSignature fromData(String s) {
        if(s == null) {
            return null;
        } else {
            return new WarpStoneSignature(s);
        }
    }

    public static boolean isSignatureMaterial(Block[] materials) {
        for(int i = 0; i < materials.length; i++) {
            if(!isSignatureMaterial(materials[i].getType()))
                return false;
        }
        return true;
    }

    /**
     * Returns if a Material matches the type of material used to encode targets/destinations (colored wool)
     * @param m
     * @return
     */
    public static boolean isSignatureMaterial(Material m) {
        return Tag.WOOL.isTagged(m);
    }

    private String materialsToData(Block[] materials) {
        if(materials.length != 4)
            throw new IllegalArgumentException("Only the four adjacent materials should be passed as argument");

        int[] colors = new int[4];
        for(int i = 0; i < 4; i++) {
            colors[i] = materials[i].getData();
        }

        Arrays.sort(colors);

        return colors[0] + "," + colors[1] + "," + colors[2] + "," + colors[3];
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
