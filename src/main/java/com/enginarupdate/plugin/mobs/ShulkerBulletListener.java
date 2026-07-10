package com.enginarupdate.plugin.mobs;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * When a Shulker bullet hits an entity, it applies 10 seconds of Poison I
 * alongside the vanilla Levitation effect.
 */
public class ShulkerBulletListener implements Listener {

    private static final int POISON_DURATION_TICKS = 200; // 10 seconds

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShulkerBulletHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof ShulkerBullet)) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity victim)) {
            return;
        }
        victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, POISON_DURATION_TICKS, 0));
    }
}
