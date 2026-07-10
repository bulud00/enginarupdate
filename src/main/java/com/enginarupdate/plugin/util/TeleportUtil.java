package com.enginarupdate.plugin.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.random.RandomGenerator;

/**
 * Shared helper for random, short-range, safe teleportation in the style of Chorus Fruit.
 */
public final class TeleportUtil {

    private TeleportUtil() {
    }

    private static final int ATTEMPTS = 16;
    private static final int RANGE = 8;

    /**
     * Attempts to teleport the given entity to a random safe location around its current position.
     *
     * @param withinRadiusOfOrigin if non-negative, the destination cannot fall outside this radius
     *                             (measured from the world origin on the X/Z plane)
     * @return whether the teleport succeeded
     */
    public static boolean tryChorusFruitTeleport(Entity entity, RandomGenerator random, double withinRadiusOfOrigin) {
        Location origin = entity.getLocation();
        World world = entity.getWorld();

        for (int i = 0; i < ATTEMPTS; i++) {
            double dx = (random.nextDouble() * 2 - 1) * RANGE;
            double dy = (random.nextDouble() * 2 - 1) * RANGE;
            double dz = (random.nextDouble() * 2 - 1) * RANGE;

            Location target = origin.clone().add(dx, dy, dz);

            if (withinRadiusOfOrigin >= 0) {
                double distFromWorldOrigin = Math.sqrt(target.getX() * target.getX() + target.getZ() * target.getZ());
                if (distFromWorldOrigin > withinRadiusOfOrigin) {
                    continue;
                }
            }

            if (isSafe(world, target)) {
                target.setYaw(origin.getYaw());
                target.setPitch(origin.getPitch());
                entity.teleport(target);
                return true;
            }
        }
        return false;
    }

    private static boolean isSafe(World world, Location loc) {
        Block feet = world.getBlockAt(loc);
        Block head = feet.getRelative(0, 1, 0);
        Block ground = feet.getRelative(0, -1, 0);

        if (feet.isLiquid() || head.isLiquid() || ground.isLiquid()) {
            return false;
        }
        if (feet.getType().isSolid() || head.getType().isSolid()) {
            return false;
        }
        if (!ground.getType().isSolid()) {
            return false; // There must be a solid block underfoot — prevents teleporting into mid-air.
        }
        return loc.getY() >= world.getMinHeight() && loc.getY() <= world.getMaxHeight();
    }
}
