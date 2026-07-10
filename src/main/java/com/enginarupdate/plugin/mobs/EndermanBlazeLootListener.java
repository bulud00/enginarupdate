package com.enginarupdate.plugin.mobs;

import org.bukkit.Material;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

/**
 * Custom Enderman and Blaze loot, plus the Blaze kill reward.
 * General rule: the Looting enchantment scales these drops the same way it does in
 * vanilla (+1% chance per level).
 */
public class EndermanBlazeLootListener implements Listener {

    private static final Random RANDOM = new Random();

    private static final double ENDER_PEARL_BASE_CHANCE = 0.20;
    private static final double BLAZE_ROD_BASE_CHANCE = 0.05;
    private static final double BLAZE_POWDER_BASE_CHANCE = 0.05;
    private static final int BLAZE_POWDER_MIN = 1;
    private static final int BLAZE_POWDER_MAX = 2;
    private static final double LOOTING_BONUS_PER_LEVEL = 0.01;

    private static final int BLAZE_VANILLA_XP = 10;
    private static final int BLAZE_XP_MULTIPLIER = 2;

    private static final int REGEN_DURATION_TICKS = 200; // 10 seconds
    private static final int REGEN_AMPLIFIER = 1; // Level II
    private static final int FIRE_RESISTANCE_DURATION_TICKS = 60; // 3 seconds

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Enderman) {
            handleEnderman(event);
        } else if (event.getEntity() instanceof Blaze) {
            handleBlaze(event);
        }
    }

    private void handleEnderman(EntityDeathEvent event) {
        int lootingLevel = lootingLevelOf(event.getEntity().getKiller());

        event.getDrops().removeIf(item -> item.getType() == Material.ENDER_PEARL);
        event.setDroppedExp(0);

        double chance = ENDER_PEARL_BASE_CHANCE + LOOTING_BONUS_PER_LEVEL * lootingLevel;
        if (RANDOM.nextDouble() < chance) {
            event.getDrops().add(new ItemStack(Material.ENDER_PEARL, 1));
        }
    }

    private void handleBlaze(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        int lootingLevel = lootingLevelOf(killer);

        event.getDrops().removeIf(item -> item.getType() == Material.BLAZE_ROD);
        event.setDroppedExp(BLAZE_VANILLA_XP * BLAZE_XP_MULTIPLIER);

        double rodChance = BLAZE_ROD_BASE_CHANCE + LOOTING_BONUS_PER_LEVEL * lootingLevel;
        if (RANDOM.nextDouble() < rodChance) {
            event.getDrops().add(new ItemStack(Material.BLAZE_ROD, 1));
        }

        double powderChance = BLAZE_POWDER_BASE_CHANCE + LOOTING_BONUS_PER_LEVEL * lootingLevel;
        if (RANDOM.nextDouble() < powderChance) {
            int amount = BLAZE_POWDER_MIN + RANDOM.nextInt(BLAZE_POWDER_MAX - BLAZE_POWDER_MIN + 1);
            event.getDrops().add(new ItemStack(Material.BLAZE_POWDER, amount));
        }

        if (killer != null) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, REGEN_DURATION_TICKS, REGEN_AMPLIFIER));
            killer.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, FIRE_RESISTANCE_DURATION_TICKS, 0));
        }
    }

    private int lootingLevelOf(Player killer) {
        if (killer == null) {
            return 0;
        }
        ItemStack weapon = killer.getInventory().getItemInMainHand();
        return weapon.getEnchantmentLevel(Enchantment.LOOTING);
    }
}
