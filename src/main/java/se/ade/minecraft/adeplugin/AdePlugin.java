package se.ade.minecraft.adeplugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import se.ade.minecraft.adeplugin.db.DbConnection;
import se.ade.minecraft.adeplugin.infrastructure.SubModule;
import se.ade.minecraft.adeplugin.warpstone.WarpStoneModule;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

/**
 * Adrian Nilsson
 * Created 2013-12-27 23:07
 */
public class AdePlugin extends JavaPlugin {
    public static final String CONFIG_FILE = "plugins\\adeplugin.yml";

    private static AdePlugin instance;
    private DbConnection dbConnection;
    private SubModule[] activeModules;

    public AdePlugin() {
        if(instance != null) {
            throw new RuntimeException("Instance already set");
        }

        instance = this;
    }

    public static AdePlugin get() {
        return instance;
    }

    @Override
    public void onEnable() {
        try {
            getConfig().options().copyDefaults(true);
            getConfig().load(CONFIG_FILE);
        } catch (Exception e) {
            getLogger().warning("Couldn't open " + CONFIG_FILE + " config file. Using default values. Looking for the file in " + System.getProperty("user.dir") + "\\" + CONFIG_FILE);
            e.printStackTrace();
        }

        dbConnection = new DbConnection(this);
        dbConnection.connect();

        activeModules = new SubModule[] {
            new WarpStoneModule()
            //new BlueprintModule()

        };

        for(SubModule subModule : activeModules) {
            subModule.onEnable(this);
        }

        if(isDevMode()) {
            getLogger().warning("Development mode is enabled.");
        }
    }

    public boolean isDevMode() {
        return getConfig().getBoolean("development_mode");
    }

    @Override
    public void onDisable() {
        for(SubModule subModule : activeModules) {
            subModule.onDisable();
        }
        activeModules = null;
        instance = null;
    }

    public void removeRecipe(ShapedRecipe itemRecipe){
        Iterator<Recipe> recipeIterator = getServer().recipeIterator();
        StringBuilder iteratedRecipeString = new StringBuilder();
        StringBuilder itemRecipeString = new StringBuilder();
        int matches = 0;

        //Create a recipe string to match with
        Map<Character, ItemStack> soughtIngredients = itemRecipe.getIngredientMap();
        String[] itemIngredients = itemRecipe.getShape();
        for(int i = 0; i <itemIngredients.length; i++) {
            itemRecipeString.append(itemIngredients[i]).append(",");
        }

        while(recipeIterator.hasNext()){
            Recipe currentRecipe = recipeIterator.next();
            if(currentRecipe instanceof ShapedRecipe){
                ShapedRecipe currentShapedRecipe = (ShapedRecipe) currentRecipe;
                Map<Character, ItemStack> currentIngredients = currentShapedRecipe.getIngredientMap();

                if(currentIngredients.values().containsAll(soughtIngredients.values())){
                    if(currentRecipe.getResult().getType() == itemRecipe.getResult().getType()) {
                        //Ingredients and output type matches. Assume this is the recipe.
                        recipeIterator.remove();
                        //getLogger().info("Recipe removed (result item type: " + currentRecipe.getResult().getType().name() + ").");
                        matches++;
                    }
                }
            }
        }
        if(matches == 0) {
            getLogger().warning("Failed to remove recipe (result item type: " + itemRecipe.getResult().getType().name() + ").");
        }
    }


    public DbConnection getDbConnection() {
        return dbConnection;
    }

    public void debugLog(String s) {
        if(isDevMode()) {
            getLogger().log(Level.INFO, s);
        }
    }
}
