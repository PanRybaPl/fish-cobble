/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.panryba.mc.cobble;

import static java.util.Arrays.fill;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author PanRyba.pl
 */
public class Plugin extends JavaPlugin implements Listener {
    private final Random random = new Random();
    private Material[] materials;
    
    @Override
    public void onEnable() {
        ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.COBBLESTONE));
        recipe.addIngredient(9, Material.COBBLESTONE);

        materials = new Material[10000];
        int current = 0;
        
        FileConfiguration config = getConfig();
        ConfigurationSection chancesSection = config.getConfigurationSection("chances");
        
        for(String key : chancesSection.getKeys(false)) {
            Material material = Material.getMaterial(key);
            int chance = chancesSection.getInt(key);
            
            Bukkit.getLogger().log(Level.INFO, "Cobble chance - {0}: {1}", new Object[]{material, chance});
            
            fill(materials, current, current + chance, material);
            current += chance;
        }
        
        getServer().addRecipe(recipe);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getServer().resetRecipes();
    }    
    
    @EventHandler
    private void onCraft(PrepareItemCraftEvent event) {
        ItemStack stack = event.getRecipe().getResult();
        
        if(stack.getType() != Material.COBBLESTONE) {
            return;
        }
        
        for(ItemStack contentItem : event.getInventory().getMatrix()) {
            if(contentItem != null && (contentItem.getType() != Material.COBBLESTONE || contentItem.getAmount() != 64)) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }
        }
       
        event.getInventory().setMatrix(new ItemStack[10]);
        
        Player player = (Player)event.getView().getPlayer();
        player.updateInventory();
        
        ItemStack item = getRandomItem();
        Bukkit.getLogger().log(Level.INFO, "COBBLE: {0}", item);
        if(item != null) {
            event.getInventory().setResult(item);
        }
    }

    private ItemStack getRandomItem() {
        int roll = this.random.nextInt(materials.length);
        Material m = this.materials[roll];
        if(m == null) {
            return null;
        }
        
        return new ItemStack(m, 1);
    }
}
