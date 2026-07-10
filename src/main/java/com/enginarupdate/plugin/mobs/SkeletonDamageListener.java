package com.enginarupdate.plugin.mobs;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * The classic Skeleton's damage output is reduced by 25%.
 * Stray and WitherSkeleton are excluded.
 */
public class SkeletonDamageListener implements Listener {

    private static final double DAMAGE_MULTIPLIER = 0.75;

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile projectile)) {
            return;
        }
        if (!(projectile.getShooter() instanceof Skeleton skeleton)) {
            return;
        }
        if (skeleton.getType() != EntityType.SKELETON) {
            return;
        }
        event.setDamage(event.getDamage() * DAMAGE_MULTIPLIER);
    }
}
