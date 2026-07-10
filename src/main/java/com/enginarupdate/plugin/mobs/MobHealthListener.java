package com.enginarupdate.plugin.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.Blaze;

/**
 * Applies all custom max-health values when a creature is added to the world (spawn).
 */
public class MobHealthListener implements Listener {

    private static final double BLAZE_MELEE_DAMAGE = 12.0; // vanilla base 6.0, +100%

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        Double newMax = resolveMaxHealth(living);
        if (newMax != null) {
            applyMaxHealth(living, newMax);
        }

        if (living instanceof Blaze) {
            applyAttackDamage(living, BLAZE_MELEE_DAMAGE);
        }
    }

    private Double resolveMaxHealth(LivingEntity living) {
        // Zombie variants (Zombie, Husk, Drowned, Zombie Villager, Zombified Piglin all implement Zombie)
        if (living instanceof Zombie zombie) {
            return zombie.isBaby() ? 12.0 : 24.0;
        }
        // Piglin (PiglinBrute does not implement Piglin, handled separately)
        if (living instanceof Piglin) {
            return 24.0;
        }
        // Piglin Brute
        if (living instanceof PiglinBrute) {
            return 100.0;
        }
        // Enderman
        if (living instanceof Enderman) {
            return 100.0;
        }
        // Blaze
        if (living instanceof Blaze) {
            return 200.0;
        }
        // Witch
        if (living instanceof Witch) {
            return 120.0;
        }
        // Illager group (except Witch), +25% health
        if (living instanceof Vindicator) {
            return 30.0;
        }
        if (living instanceof Pillager) {
            return 30.0;
        }
        if (living instanceof Evoker) {
            return 30.0;
        }
        if (living instanceof Vex) {
            return 17.5;
        }
        if (living instanceof Ravager) {
            return 125.0;
        }
        if (living instanceof Illusioner) {
            return 40.0;
        }
        // Ender Dragon
        if (living instanceof EnderDragon) {
            return 1000.0;
        }
        // Wither
        if (living instanceof Wither) {
            return 600.0;
        }
        return null;
    }

    private void applyMaxHealth(LivingEntity living, double newMax) {
        AttributeInstance attribute = living.getAttribute(Attribute.MAX_HEALTH);
        if (attribute == null) {
            return;
        }
        attribute.setBaseValue(newMax);
        living.setHealth(Math.min(newMax, attribute.getValue()));
    }

    private void applyAttackDamage(LivingEntity living, double newDamage) {
        AttributeInstance attribute = living.getAttribute(Attribute.ATTACK_DAMAGE);
        if (attribute == null) {
            return;
        }
        attribute.setBaseValue(newDamage);
    }
}
