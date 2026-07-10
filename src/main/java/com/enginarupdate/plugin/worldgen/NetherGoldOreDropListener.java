package com.enginarupdate.plugin.worldgen;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * Nether Gold Ore's Gold Nugget drop range changes from 2-6 to 1-2; Fortune is applied
 * the same way vanilla does it (+1 to the upper bound per level).
 */
public class NetherGoldOreDropListener implements Listener {

    private static final int MIN_NUGGETS = 1;
    private static final int BASE_MAX_NUGGETS = 2;

    private final Random random = new Random();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.NETHER_GOLD_ORE) {
            return;
        }
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return; // Silk Touch: vanilla behavior (the block itself drops) is preserved.
        }

        int fortune = tool.getEnchantmentLevel(Enchantment.FORTUNE);
        int max = BASE_MAX_NUGGETS + fortune;
        int amount = MIN_NUGGETS + random.nextInt(max - MIN_NUGGETS + 1);

        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.GOLD_NUGGET, amount));

        if (random.nextBoolean()) {
            event.getBlock().getWorld().spawn(
                    event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                    ExperienceOrb.class,
                    orb -> orb.setExperience(1)
            );
        }
    }
}
