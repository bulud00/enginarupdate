package com.enginarupdate.plugin.enchant;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The Mending enchantment can no longer be applied to an Elytra, neither at the
 * Enchanting Table nor at the Anvil.
 */
public class ElytraMendingBlockListener implements Listener {

    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
        if (event.getItem().getType() != Material.ELYTRA) {
            return;
        }
        EnchantmentOffer[] offers = event.getOffers();
        for (int i = 0; i < offers.length; i++) {
            if (offers[i] != null && offers[i].getEnchantment() == Enchantment.MENDING) {
                offers[i] = null;
            }
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (event.getItem().getType() == Material.ELYTRA) {
            event.getEnchantsToAdd().remove(Enchantment.MENDING);
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack base = inventory.getItem(0);
        ItemStack result = event.getResult();
        if (base == null || result == null || base.getType() != Material.ELYTRA) {
            return;
        }
        ItemMeta resultMeta = result.getItemMeta();
        if (resultMeta != null && resultMeta.hasEnchant(Enchantment.MENDING)) {
            event.setResult(null);
        }
    }
}
