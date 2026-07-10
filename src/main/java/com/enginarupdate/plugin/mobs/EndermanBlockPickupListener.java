package com.enginarupdate.plugin.mobs;

import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

/**
 * Disables Endermen picking up (and later placing back) blocks.
 */
public class EndermanBlockPickupListener implements Listener {

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Enderman) {
            event.setCancelled(true);
        }
    }
}
