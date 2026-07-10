package com.enginarupdate.plugin.mobs;

import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Custom Witch loot table, replacing every vanilla drop.
 */
public class WitchLootListener implements Listener {

    private static final Random RANDOM = new Random();

    private static final double ENDER_EYE_CHANCE = 0.0125;

    private static final int CHORUS_FRUIT_MIN = 1;
    private static final int CHORUS_FRUIT_MAX = 4;

    private static final int ENCHANTED_BOOK_MIN = 1;
    private static final int ENCHANTED_BOOK_MAX = 2;

    private static final List<Enchantment> ALL_ENCHANTMENTS;

    static {
        List<Enchantment> list = new ArrayList<>();
        Registry.ENCHANTMENT.forEach(list::add);
        ALL_ENCHANTMENTS = List.copyOf(list);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Witch)) {
            return;
        }

        event.getDrops().clear();

        if (RANDOM.nextDouble() < ENDER_EYE_CHANCE) {
            event.getDrops().add(new ItemStack(Material.ENDER_EYE, 1));
        }
        addRandomAmount(event, Material.CHORUS_FRUIT, CHORUS_FRUIT_MIN, CHORUS_FRUIT_MAX);

        int bookCount = ENCHANTED_BOOK_MIN + RANDOM.nextInt(ENCHANTED_BOOK_MAX - ENCHANTED_BOOK_MIN + 1);
        for (int i = 0; i < bookCount; i++) {
            event.getDrops().add(randomEnchantedBook());
        }
    }

    private void addRandomAmount(EntityDeathEvent event, Material material, int min, int max) {
        int amount = min + RANDOM.nextInt(max - min + 1);
        if (amount > 0) {
            event.getDrops().add(new ItemStack(material, amount));
        }
    }

    private ItemStack randomEnchantedBook() {
        Enchantment enchantment = ALL_ENCHANTMENTS.get(RANDOM.nextInt(ALL_ENCHANTMENTS.size()));
        int level = 1 + RANDOM.nextInt(enchantment.getMaxLevel());

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(enchantment, level, true);
        book.setItemMeta(meta);
        return book;
    }
}
