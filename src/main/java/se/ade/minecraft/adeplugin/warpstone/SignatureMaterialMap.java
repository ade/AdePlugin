package se.ade.minecraft.adeplugin.warpstone;

import org.bukkit.Material;

import java.util.HashMap;

public abstract class SignatureMaterialMap {
    protected HashMap<Integer, Material> intIdLookUp = new HashMap<>();
    protected HashMap<Material, Integer> materialLookUp = new HashMap<>();

    protected void addMapping(Material material, int id) {
        materialLookUp.put(material, id);
        intIdLookUp.put(id, material);
    }

    public Material fromId(int in) {
        if(!intIdLookUp.containsKey(in)) return null;

        return intIdLookUp.get(in);
    }

    public int getDataBaseId(Material m) {
        if(!isSignatureMaterial(m)) return -1;

        return materialLookUp.get(m);
    }

    public boolean isSignatureMaterial(Material m) {
        return materialLookUp.containsKey(m);
    }
}
