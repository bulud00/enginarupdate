package com.enginarupdate.plugin.loot;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;

import java.util.EnumSet;
import java.util.Set;

/**
 * Removes Netherite-related items from every chest loot table.
 */
public class LootTableListener implements Listener {

    private static final Set<Material> REMOVED = EnumSet.of(
            Material.NETHERITE_INGOT,
            Material.ANCIENT_DEBRIS,
            Material.NETHERITE_SCRAP,
            Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE
    );

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        event.getLoot().removeIf(item -> REMOVED.contains(item.getType()));
    }
}
