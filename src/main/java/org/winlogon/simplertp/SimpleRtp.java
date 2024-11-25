package org.winlogon.simplertp;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * SimpleRTP is a plugin for Bukkit that allows players to teleport
 * to random safe locations within a specified range.
 */
public class SimpleRtp extends JavaPlugin {

    private static final Set<Material> UNSAFE_BLOCKS = EnumSet.of(
            Material.LAVA, Material.WATER, Material.FIRE, Material.CACTUS, Material.MAGMA_BLOCK);

    private int minRange;
    private int maxAttempts;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        this.getCommand("rtp").setExecutor(this);
        getLogger().info("SimpleRtp has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SimpleRtp has been disabled!");
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        minRange = config.getInt("min-range", 3000);
        maxAttempts = config.getInt("max-attempts", 50);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        World world = player.getWorld();

        if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
            player.sendMessage(ChatColor.GREEN + "Usage: /rtp");
            player.sendMessage(ChatColor.AQUA + "Randomly teleports you to a safe location between "
                + minRange + " and " + world.getWorldBorder().getSize() / 2 + " blocks.");
            return true;
        }

        Location safeLocation = findSafeLocation(world);

        if (safeLocation != null) {
            player.teleport(safeLocation);
            player.sendMessage(ChatColor.GOLD + "You have been teleported to a random location");
        } else {
            player.sendMessage(ChatColor.RED + "Failed to find a safe location. Please try again.");
        }

        return true;
    }

    private Location findSafeLocation(World world) {
        Random random = new Random();
        WorldBorder border = world.getWorldBorder();
        double borderSize = border.getSize() / 2; // Half-size to get the radius

        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            int x = random.nextInt((int) borderSize * 2) - (int) borderSize;
            int z = random.nextInt((int) borderSize * 2) - (int) borderSize;

            // Ensure the location is within the border
            if (!border.isInside(new Location(world, x, world.getHighestBlockYAt(x, z), z))) {
                continue;
            }

            // Check if within the minimum range from spawn
            Location spawn = world.getSpawnLocation();
            Location randomLocation = new Location(world, x, spawn.getY(), z);

            if (spawn.distanceSquared(randomLocation) < minRange * minRange) {
                continue;
            }

            // Ensure chunk is loaded synchronously
            int chunkX = x >> 4;
            int chunkZ = z >> 4;

            if (!world.isChunkLoaded(chunkX, chunkZ)) {
                world.loadChunk(chunkX, chunkZ);
            }

            int highestY = world.getHighestBlockYAt(x, z);
            Location potentialLocation = new Location(world, x + 0.5, highestY + 1, z + 0.5);

            Material blockBelow = world.getBlockAt(x, highestY, z).getType();
            Material blockAtFeet = world.getBlockAt(x, highestY + 1, z).getType();
            Material blockAtHead = world.getBlockAt(x, highestY + 2, z).getType();

            if (isSafeBlock(blockBelow) && blockAtFeet == Material.AIR
                && blockAtHead == Material.AIR) {
                return potentialLocation;
            }
        }
        return null; // No safe location found within max attempts
    }


    private boolean isSafeBlock(Material material) {
        return material.isSolid() && !UNSAFE_BLOCKS.contains(material);
    }
}
