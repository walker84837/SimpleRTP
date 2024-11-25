package org.winlogon.Rtp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

public class RTPPlugin extends JavaPlugin implements CommandExecutor {

    private static final Set<Material> UNSAFE_BLOCKS = EnumSet.of(
            Material.LAVA, Material.WATER, Material.FIRE, Material.CACTUS, Material.MAGMA_BLOCK);

    private static final int DEFAULT_RANGE = 50000;
    private static final int MIN_RANGE = 1000;
    private static final int MAX_RANGE = 1000000;
    private static final int MAX_ATTEMPTS = 50;

    private int maxAttempts;
    private int defaultRange;
    private int minRange;
    private int maxRange;

    @Override
    public void onEnable() {
        loadConfig();
        this.getCommand("rtp").setExecutor(this);
        getLogger().info("RTPPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("RTPPlugin has been disabled!");
    }

    private void loadConfig() {
        // Save the default config if it doesn't exist
        saveDefaultConfig();

        // Load the configuration
        FileConfiguration config = getConfig();

        // Set variables using defaults if not specified in config.yml
        maxAttempts = config.getInt("max-attempts", MAX_ATTEMPTS);
        defaultRange = config.getInt("default-range", DEFAULT_RANGE);
        minRange = config.getInt("min-range", MIN_RANGE);
        maxRange = config.getInt("max-range", MAX_RANGE);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        int range = defaultRange;

        if (args.length > 0) {
            try {
                range = Integer.parseInt(args[0]);
                if (range < minRange || range > maxRange) {
                    player.sendMessage("Range must be between " + minRange + " and " + maxRange + ".");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid range. Please provide a number between " + minRange + " and " + maxRange + ".");
                return true;
            }
        }

        int finalRange = range;

        // Run location finding asynchronously to avoid blocking the main thread
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            Location safeLocation = findSafeLocation(player.getWorld(), finalRange);

            if (safeLocation != null) {
                // Teleport the player back on the main thread
                Bukkit.getScheduler().runTask(this, () -> {
                    player.teleport(safeLocation);
                    player.sendMessage("You have been teleported to a random safe location!");
                });
            } else {
                player.sendMessage("Failed to find a safe location. Please try again.");
            }
        });

        return true;
    }

    private Location findSafeLocation(World world, int range) {
        Random random = new Random();

        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            int x = random.nextInt(range * 2) - range;
            int z = random.nextInt(range * 2) - range;

            int highestY = world.getHighestBlockYAt(x, z);
            Location potentialLocation = new Location(world, x + 0.5, highestY + 1, z + 0.5);

            Material blockBelow = world.getBlockAt(x, highestY, z).getType();
            Material blockAtFeet = world.getBlockAt(x, highestY + 1, z).getType();
            Material blockAtHead = world.getBlockAt(x, highestY + 2, z).getType();

            if (isSafeBlock(blockBelow) && blockAtFeet == Material.AIR && blockAtHead == Material.AIR) {
                return potentialLocation;
            }
        }

        return null; // No safe location found within maxAttempts
    }

    private boolean isSafeBlock(Material material) {
        return material.isSolid() && !UNSAFE_BLOCKS.contains(material);
    }
}
