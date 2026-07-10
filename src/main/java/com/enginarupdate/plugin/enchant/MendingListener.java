package com.enginarupdate.plugin.enchant;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The Mending repair rate is updated to 1 XP = 1 durability (vanilla was 1 XP = 2 durability).
 */
public class MendingListener implements Listener {

    @EventHandler
    public void onMend(PlayerItemMendEvent event) {
        int xp = event.getExperienceOrb().getExperience();

        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable)) {
            return;
        }
        int remainingDamage = damageable.getDamage();

        int newRepairAmount = Math.min(xp, remainingDamage);
        event.setRepairAmount(newRepairAmount);
    }
}
