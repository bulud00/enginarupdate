package com.enginarupdate.plugin.mobs;

import com.enginarupdate.plugin.lang.Lang;
import com.enginarupdate.plugin.util.TeleportUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.random.RandomGenerator;

/**
 * While a Witch has an active target: every 3 seconds it teleports Chorus-Fruit-style
 * (healing itself on each teleport) and applies Blindness to its target. Independently
 * of combat state, a boss bar visible to every online player is shown whenever any
 * player is within {@link #BOSS_BAR_RADIUS} blocks of the Witch.
 */
public class WitchCombatTask extends BukkitRunnable implements Listener {

    private static final long PERIOD_TICKS = 60L; // 3 seconds
    private static final int BLINDNESS_DURATION_TICKS = 100; // 5 seconds, refreshed every cycle
    private static final double TELEPORT_HEAL_AMOUNT = 4.0;
    private static final double BOSS_BAR_RADIUS = 25.0;
    private static final double ARROW_DAMAGE_HEAL_AMOUNT = 4.0;

    private static final long BUFF_PERIOD_TICKS = 100L; // 5 seconds
    private static final int BUFF_DURATION_TICKS = 200; // 10 seconds, refreshed every cycle
    private static final int SWIFTNESS_AMPLIFIER = 1; // Level II

    private final RandomGenerator random = RandomGenerator.getDefault();
    private final Map<UUID, BossBar> bossBars = new HashMap<>();
    private Plugin plugin;

    public static void start(Plugin plugin) {
        WitchCombatTask task = new WitchCombatTask();
        task.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(task, plugin);
        task.runTaskTimer(plugin, PERIOD_TICKS, PERIOD_TICKS);
        Bukkit.getScheduler().runTaskTimer(plugin, task::applyCombatBuffs, BUFF_PERIOD_TICKS, BUFF_PERIOD_TICKS);
    }

    @Override
    public void run() {
        Set<UUID> aliveWitchIds = new HashSet<>();
        for (World world : Bukkit.getWorlds()) {
            for (Witch witch : world.getEntitiesByClass(Witch.class)) {
                aliveWitchIds.add(witch.getUniqueId());
                handleWitch(witch);
            }
        }
        cleanupStaleBossBars(aliveWitchIds);
    }

    /**
     * Safety net: a Witch can disappear (death, /kill, chunk unload, plugin conflicts)
     * without EntityDeathEvent ever reaching {@link #onWitchDeath}, which would otherwise
     * leave its boss bar stuck on-screen forever since nothing else touches it again.
     */
    private void cleanupStaleBossBars(Set<UUID> aliveWitchIds) {
        bossBars.entrySet().removeIf(entry -> {
            if (aliveWitchIds.contains(entry.getKey())) {
                return false;
            }
            entry.getValue().removeAll();
            return true;
        });
    }

    private void handleWitch(Witch witch) {
        Player target = witch.getTarget() instanceof Player p ? p : null;
        if (target != null) {
            if (TeleportUtil.tryChorusFruitTeleport(witch, random, -1)) {
                double max = witch.getAttribute(Attribute.MAX_HEALTH).getValue();
                witch.setHealth(Math.min(witch.getHealth() + TELEPORT_HEAL_AMOUNT, max));
            }
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, BLINDNESS_DURATION_TICKS, 0));
        }

        Player nearestPlayer = findNearestPlayer(witch, BOSS_BAR_RADIUS);
        if (nearestPlayer == null) {
            hideBossBar(witch);
            return;
        }
        updateBossBar(witch, nearestPlayer, target);
    }

    private Player findNearestPlayer(Witch witch, double radius) {
        Player nearest = null;
        double nearestDistanceSquared = radius * radius;
        for (Player player : witch.getWorld().getPlayers()) {
            double distanceSquared = player.getLocation().distanceSquared(witch.getLocation());
            if (distanceSquared <= nearestDistanceSquared) {
                nearest = player;
                nearestDistanceSquared = distanceSquared;
            }
        }
        return nearest;
    }

    private void applyCombatBuffs() {
        for (World world : Bukkit.getWorlds()) {
            for (Witch witch : world.getEntitiesByClass(Witch.class)) {
                if (!(witch.getTarget() instanceof Player)) {
                    continue;
                }
                witch.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, BUFF_DURATION_TICKS, SWIFTNESS_AMPLIFIER));
            }
        }
    }

    private void updateBossBar(Witch witch, Player localeReference, Player opponent) {
        BossBar bar = bossBars.computeIfAbsent(witch.getUniqueId(), id ->
                Bukkit.createBossBar(Lang.pick(localeReference, "Witch", "Cadı"), BarColor.PURPLE, BarStyle.SOLID));

        double max = witch.getAttribute(Attribute.MAX_HEALTH).getValue();
        double progress = Math.max(0.0, Math.min(1.0, witch.getHealth() / max));
        bar.setProgress(progress);
        bar.setTitle(opponent != null
                ? Lang.pick(localeReference, "Witch — fighting " + opponent.getName(), "Cadı — " + opponent.getName() + " ile çatışmada")
                : Lang.pick(localeReference, "Witch", "Cadı"));
        bar.setVisible(true);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!bar.getPlayers().contains(online)) {
                bar.addPlayer(online);
            }
        }
        for (Player viewer : new ArrayList<>(bar.getPlayers())) {
            if (!viewer.isOnline()) {
                bar.removePlayer(viewer);
            }
        }
    }

    private void hideBossBar(Witch witch) {
        BossBar bar = bossBars.get(witch.getUniqueId());
        if (bar != null) {
            bar.setVisible(false);
        }
    }

    @EventHandler
    public void onWitchArrowDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Witch witch)) {
            return;
        }
        if (!(event.getDamager() instanceof AbstractArrow)) {
            return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!witch.isValid()) {
                return;
            }
            double max = witch.getAttribute(Attribute.MAX_HEALTH).getValue();
            witch.setHealth(Math.min(witch.getHealth() + ARROW_DAMAGE_HEAL_AMOUNT, max));
        });
    }

    @EventHandler
    public void onWitchDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Witch witch)) {
            return;
        }
        BossBar bar = bossBars.remove(witch.getUniqueId());
        if (bar != null) {
            bar.removeAll();
        }
    }
}
