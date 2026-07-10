package com.enginarupdate.plugin.end;

import com.enginarupdate.plugin.util.TeleportUtil;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.random.RandomGenerator;

/**
 * While the Ender Dragon is alive, players in the End are teleported every 10 seconds
 * to a random safe location, Chorus-Fruit-style, restricted to the Main Island.
 */
public class EndTeleportTask extends BukkitRunnable {

    private static final long PERIOD_TICKS = 200L; // 10 seconds
    private static final double MAIN_ISLAND_RADIUS = 100.0;

    private final RandomGenerator random = RandomGenerator.getDefault();

    public static void start(Plugin plugin) {
        new EndTeleportTask().runTaskTimer(plugin, PERIOD_TICKS, PERIOD_TICKS);
    }

    @Override
    public void run() {
        for (World world : org.bukkit.Bukkit.getWorlds()) {
            if (world.getEnvironment() != World.Environment.THE_END) {
                continue;
            }
            if (!isDragonAlive(world)) {
                continue;
            }
            for (Player player : world.getPlayers()) {
                if (player.getGameMode() == GameMode.SPECTATOR) {
                    continue;
                }
                TeleportUtil.tryChorusFruitTeleport(player, random, MAIN_ISLAND_RADIUS);
            }
        }
    }

    private boolean isDragonAlive(World world) {
        for (EnderDragon dragon : world.getEntitiesByClass(EnderDragon.class)) {
            if (!dragon.isDead()) {
                return true;
            }
        }
        return false;
    }
}
