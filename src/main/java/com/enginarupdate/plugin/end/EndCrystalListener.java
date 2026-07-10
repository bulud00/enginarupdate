package com.enginarupdate.plugin.end;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * While the Ender Dragon is alive, an End Crystal can only be destroyed by melee
 * attacks and requires 3 hits.
 */
public class EndCrystalListener implements Listener {

    private static final int REQUIRED_HITS = 3;
    private static final int COOLDOWN_TICKS = 200; // 10 seconds
    private static final double CLOUD_RADIUS = 5.0;
    private static final int CLOUD_DURATION_TICKS = 160; // 8 seconds
    private static final int CLOUD_REAPPLY_TICKS = 20; // re-damages roughly once per second
    private static final int LIGHTNING_STRIKES = 5;

    private final Plugin plugin;
    private final NamespacedKey hitsKey;
    private final Map<UUID, Long> cooldownUntilTick = new HashMap<>();
    private final Random random = new Random();

    public EndCrystalListener(Plugin plugin) {
        this.plugin = plugin;
        this.hitsKey = new NamespacedKey(plugin, "crystal_hits");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof EnderCrystal crystal)) {
            return;
        }

        World world = crystal.getWorld();
        if (!isDragonAlive(world)) {
            // Dragon is not alive: revert to vanilla behavior (pass through without acting).
            return;
        }

        // While this mechanic is active, all damage is cancelled up front; only the
        // defined player-melee flow affects the crystal.
        event.setCancelled(true);

        Entity damager = event.getDamager();
        if (damager instanceof Projectile) {
            return; // Rule: cannot be destroyed by projectiles.
        }
        if (!(damager instanceof Player player)) {
            return; // Only a player melee hit has defined behavior.
        }

        UUID crystalId = crystal.getUniqueId();
        long now = world.getFullTime();
        Long cooldownUntil = cooldownUntilTick.get(crystalId);
        if (cooldownUntil != null && now < cooldownUntil) {
            return; // No player can hit it while on cooldown.
        }

        registerHit(crystal, player);
    }

    private void registerHit(EnderCrystal crystal, Player player) {
        int hits = crystal.getPersistentDataContainer().getOrDefault(hitsKey, PersistentDataType.INTEGER, 0) + 1;
        crystal.getPersistentDataContainer().set(hitsKey, PersistentDataType.INTEGER, hits);

        Location crystalLoc = crystal.getLocation();

        spawnHarmCloud(crystal);

        UUID crystalId = crystal.getUniqueId();
        cooldownUntilTick.put(crystalId, crystal.getWorld().getFullTime() + COOLDOWN_TICKS);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            cooldownUntilTick.remove(crystalId);
            if (crystal.isValid() && !crystal.isDead()) {
                playCooldownEndedSound(crystalLoc);
            }
        }, COOLDOWN_TICKS);

        if (hits >= REQUIRED_HITS) {
            explodeCrystal(crystal);
        }
    }

    private void spawnHarmCloud(EnderCrystal crystal) {
        Location cloudLoc = crystal.getLocation().subtract(0, 1, 0);
        crystal.getWorld().spawn(cloudLoc, org.bukkit.entity.AreaEffectCloud.class, cloud -> {
            cloud.setRadius((float) CLOUD_RADIUS);
            cloud.setDuration(CLOUD_DURATION_TICKS);
            cloud.setReapplicationDelay(CLOUD_REAPPLY_TICKS);
            cloud.setRadiusPerTick(0f);
            cloud.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 0), true);
        });
    }

    private void playCooldownEndedSound(Location crystalLoc) {
        World world = crystalLoc.getWorld();
        if (world == null) {
            return;
        }
        for (Player nearby : world.getPlayers()) {
            if (nearby.getLocation().distanceSquared(crystalLoc) <= CLOUD_RADIUS * CLOUD_RADIUS) {
                nearby.playSound(crystalLoc, Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.2f);
            }
        }
    }

    private void explodeCrystal(EnderCrystal crystal) {
        Location loc = crystal.getLocation();
        World world = crystal.getWorld();
        crystal.remove();
        world.createExplosion(loc, 6.0F, false, true);
        for (int i = 0; i < LIGHTNING_STRIKES; i++) {
            double jitterX = (random.nextDouble() * 2 - 1) * 1.5;
            double jitterZ = (random.nextDouble() * 2 - 1) * 1.5;
            world.strikeLightning(loc.clone().add(jitterX, 0, jitterZ));
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
